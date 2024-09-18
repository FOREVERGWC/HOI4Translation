package com.example.hoi4translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.SuggestionVO;
import com.example.hoi4translation.service.ParatranzService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@DisplayName("56之路")
class Hoi4TheRoadTo56ApplicationTests {
    private final Integer projectId = 5762;
    private final String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";

    @Autowired
    private ParatranzService paratranzService;

    @Test
    @DisplayName("从平台对比词条")
    void t5sfa() {
        paratranzService.compareParatranz(projectId, authorization);
    }

    @Test
    @DisplayName("导出词条到平台")
    void t6() {
        paratranzService.exportParatranz(projectId, authorization);
    }

    @Test
    void t7() {
        Comparator<Integer> comparator = Comparator.comparingLong(key -> //
                switch (key) {
                    case 1219 -> 1;
                    case 96 -> 2;
                    default -> 3; // 其他键都归类为5
                });
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
//        String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";
        String authorization = "372b9368102ca7c77dcb65b28726c83f";

        for (int i = 1; i < 1000; i++) {
            String stringUrl = StrUtil.format("https://paratranz.cn/api/projects/1219/strings?file=1169030&stage=0&pageSize=800&page={}", i);
            try (HttpResponse response = HttpUtil.createGet(stringUrl).auth(authorization).execute()) {
                if (!response.isOk()) {
                    return;
                }
                PageVO pageVO = JSONUtil.toBean(response.body(), PageVO.class);
                pageVO.getResults().forEach(item -> {
                    if (ReUtil.isMatch("\\$.*?\\$", item.getOriginal())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("stage", -1);
                        map.put("uid", null);
                        String updateUrl = StrUtil.format("https://paratranz.cn/api/projects/1219/strings/{}", item.getId());
                        try (HttpResponse updateResponse = HttpRequest.put(updateUrl).auth(authorization).contentType("application/json").body(JSONUtil.toJsonStr(map, jsonConfig)).execute()) {
                            log.info("已隐藏词条：{}", updateResponse.body());
                        }
                    } else {
                        String suggestionUrl = StrUtil.format("https://paratranz.cn/api/projects/1219/strings/{}/suggestions", item.getId());
                        try (HttpResponse suggestionsResponse = HttpUtil.createGet(suggestionUrl).auth(authorization).execute()) {
                            if (!suggestionsResponse.isOk()) {
                                return;
                            }
                            List<SuggestionVO> suggestionVOS = JSONUtil.toList(suggestionsResponse.body(), SuggestionVO.class);
                            Map<Integer, List<SuggestionVO>> listMap = suggestionVOS.stream() //
                                    .filter(suggestionVO -> suggestionVO.getMatching().compareTo(BigDecimal.ONE) > -1) //
                                    .collect(Collectors.groupingBy(suggestion -> suggestion.getProject().getId()));
                            Map<Integer, List<SuggestionVO>> sortedMap = listMap.entrySet().stream() //
                                    .sorted(Map.Entry.comparingByKey(comparator)) //
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                            List<SuggestionVO> values = sortedMap.entrySet().stream() //
                                    .findFirst() //
                                    .map(Map.Entry::getValue) //
                                    .orElseGet(ArrayList::new);

                            if (CollectionUtil.isEmpty(values)) {
                                return;
                            }
                            Map<String, Object> map = new HashMap<>();
                            map.put("translation", values.get(0).getTranslation());
                            map.put("stage", 1);
                            map.put("uid", null);
                            String updateUrl = StrUtil.format("https://paratranz.cn/api/projects/1219/strings/{}", item.getId());
                            try (HttpResponse updateResponse = HttpRequest.put(updateUrl).auth(authorization).contentType("application/json").body(JSONUtil.toJsonStr(map, jsonConfig)).execute()) {
                                log.info("已更新词条：{}", updateResponse.body());
                            }
                        }
                    }
                });
                if (pageVO.getPageCount() <= i) {
                    return;
                }
            }
        }
    }
}
