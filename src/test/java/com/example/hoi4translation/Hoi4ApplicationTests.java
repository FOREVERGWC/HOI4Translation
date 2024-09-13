package com.example.hoi4translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.SuggestionVO;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@DisplayName("原版")
class Hoi4ApplicationTests {
    private final Integer projectId = 5239;
    private final String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private Hoi4Filter hoi4Filter;
    @Autowired
    private ParatranzService paratranzService;

    @Test
    @DisplayName("复制原版文件")
    void t1() {
        String resource = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV";
        String destination = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        fileService.fileCopy(resource, destination, hoi4Filter);
    }

    @Test
    @DisplayName("导出原版词条")
    void t3() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        projectService.exportProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("上传原版文件到平台")
    void t4() {
        Map<String, Long> map = paratranzService.getFiles(projectId, authorization);
        String dir = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        List<File> files = FileUtil.loopFiles(dir, hoi4Filter);
        files.forEach(file -> {
            String path = FileUtil.subPath(dir, file.getParent()); // 相对子路径
            String fileName = path + "/" + FileUtil.getName(file);
            Long fileId = map.get(fileName);
            if (fileId == null) {
                paratranzService.uploadFile(projectId, authorization, file, path);
            } else {
                paratranzService.updateFile(projectId, authorization, file, path, fileId);
            }
        });
    }

    @Test
    @DisplayName("从平台对比词条")
    void t5sfa() {
        paratranzService.compareParatranz(projectId, authorization);
    }

    @Test
    @DisplayName("从平台导入词条")
    void t5() {
        paratranzService.importParatranz(projectId, authorization);
    }

    @Test
    @DisplayName("导出词条到平台")
    void t6() {
        paratranzService.exportParatranz(projectId, authorization);
    }

    @Test
    void t9() {
        Comparator<Integer> comparator = Comparator.comparingLong(key -> //
                switch (key) {
                    case 5239 -> 1;
                    case 5762 -> 2;
                    case 96 -> 3;
                    case 2 -> 4;
                    default -> 5; // 其他键都归类为5
                });
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
        String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";

        for (int i = 1; i < 1000; i++) {
            String stringUrl = StrUtil.format("https://paratranz.cn/api/projects/5239/strings?file=1169030&stage=0&pageSize=800&page={}", i);
            try (HttpResponse response = HttpUtil.createGet(stringUrl).auth(authorization).execute()) {
                if (!response.isOk()) {
                    return;
                }
                PageVO pageVO = JSONUtil.toBean(response.body(), PageVO.class);
                pageVO.getResults().forEach(item -> {
                    String suggestionUrl = StrUtil.format("https://paratranz.cn/api/projects/5239/strings/{}/suggestions", item.getId());
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
                        String updateUrl = StrUtil.format("https://paratranz.cn/api/projects/5239/strings/{}", item.getId());
                        try (HttpResponse updateResponse = HttpRequest.put(updateUrl).auth(authorization).contentType("application/json").body(JSONUtil.toJsonStr(map, jsonConfig)).execute()) {
                            log.info("已更新词条：{}", updateResponse.body());
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
