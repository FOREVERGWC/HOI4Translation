package com.example.hoi4translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hoi4translation.domain.entity.HistoryUnit;
import com.example.hoi4translation.domain.entity.NamesShip;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.domain.vo.SuggestionVO;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@DisplayName("原版")
class Hoi4ApplicationTests {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private Hoi4Filter hoi4Filter;
    @Autowired
    private ParatranzService paratranzService;

    @Autowired
    private CharacterService characterService;
    @Autowired
    private NamesShipService namesShipService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private ScriptedEffectService scriptedEffectService;
    @Autowired
    private ScriptedTriggerService scriptedTriggerService;
    @Autowired
    private NamesDivisionService namesDivisionService;
    @Autowired
    private EventService eventService;
    @Autowired
    private HistoryCountryService historyCountryService;
    @Autowired
    private HistoryUnitService historyUnitService;

    @Test
    @DisplayName("复制原版文件")
    void t1() {
        String resource = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV";
        String destination = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        fileService.fileCopy(resource, destination, hoi4Filter);
    }

    @Test
    @DisplayName("导入原版词条")
    void t2() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        projectService.importProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("导出原版词条")
    void t3() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        projectService.exportProject(file, hoi4Filter);
    }

    // https://paratranz.cn/api/projects/5762/files
    // POST
    // 上传文件
    // file: 二进制文件流
    // filename: _debug_decisions.txt
    // path: common/decisions
    @Test
    @DisplayName("上传文件到平台")
    void tt() {
        Integer projectId = 5239;
        String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("file", "");
        requestBody.put("filename", "");
        requestBody.put("path", "");
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files", projectId);
        try (HttpResponse response = HttpUtil.createPost(url).auth(authorization).body(JSONUtil.toJsonStr(requestBody)).execute()) {
            System.out.println(response.body());
        }
    }

    @Test
    @DisplayName("从平台导入词条")
    void t4() {
        paratranzService.importParatranz(5239, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    @DisplayName("导出词条到平台")
    void t5() {
        paratranzService.exportParatranz(5239, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    @DisplayName("从平台查询相同词条")
    void t6() {
        LambdaQueryWrapper<NamesShip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NamesShip::getTranslation, "");
        List<NamesShip> list = namesShipService.list(wrapper);
        list.forEach(item -> {
            List<StringVO> strings = paratranzService.getStringsByProjectIdAndOriginalAndAndAuthorization(96, item.getOriginal(), "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
            strings.forEach(stringVO -> {
                System.out.println("原文：" + stringVO.getOriginal() + "，译文：" + stringVO.getTranslation());
                item.setTranslation(stringVO.getTranslation());
//                namesShipService.updateById(item);
            });
        });
    }

    @SneakyThrows
    @Test
    @DisplayName("从hoi4库查询相同词条")
    void t7() {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoi4?serverTimezone=UTC", "root", "root");
        LambdaQueryWrapper<HistoryUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HistoryUnit::getTranslation, "");
        List<HistoryUnit> list = historyUnitService.list(wrapper);
        list.forEach(item -> {
            try {
                PreparedStatement ps = conn.prepareStatement("select * from `history/units` where original = ?;");
                ps.setString(1, item.getOriginal());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (!Objects.equals(item.getTranslation(), rs.getString(3))) {
                        if (StrUtil.isNotBlank(rs.getString(3))) {
//                            item.setTranslation(rs.getString(3));
                            System.out.println(item + "，hoi4库翻译：" + rs.getString(3));
                        }
//                        List<StringVO> strings = paratranzService.getStringsByProjectIdAndOriginalAndAndAuthorization(96, item.getOriginal(), "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
//                        if (CollectionUtil.isNotEmpty(strings) && Objects.equals(strings.get(0).getTranslation(), rs.getString(3))) {
//                            item.setTranslation(rs.getString(3));
//                            System.out.println(item + "，hoi4库翻译：" + rs.getString(3));
//                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        historyUnitService.updateBatchById(list);
    }

    @Test
    void t8() {
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
            String stringUrl = StrUtil.format("https://paratranz.cn/api/projects/5239/strings?file=765698&stage=0&pageSize=800&page={}", i);
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
