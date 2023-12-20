package com.example.hoi4translation.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.domain.entity.BaseEntity;
import com.example.hoi4translation.domain.entity.Character;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.service.CharacterService;
import com.example.hoi4translation.service.ParatranzService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ParatranzServiceImpl implements ParatranzService {
    final int pageSize = 800;

    @Override
    public List<FileVO> getFilesByProjectIdAndAuthorization(Integer projectId, String authorization) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files", projectId);
        try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
            return JSONUtil.toList(response.body(), FileVO.class);
        }
    }

    @Override
    public Map<String, Long> getFiles(Integer projectId, String authorization) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files", projectId);
        try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
            List<FileVO> vos = JSONUtil.toList(response.body(), FileVO.class);
            return vos.stream().collect(Collectors.toMap(FileVO::getName, FileVO::getId));
        }
    }

    @Override
    public void uploadFile(Integer projectId, String authorization, File file, String path) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files", projectId);
        try (HttpResponse response = HttpRequest.post(url) //
                .auth(authorization) //
                .contentType(ContentType.MULTIPART.getValue()) //
                .form("file", file) //
                .form("filename", file.getName()) //
                .form("path", path) //
                .execute()) {
            JSONObject object = JSONUtil.parseObj(response.body());
            if (Objects.equals(object.getByPath("status", String.class), "empty")) {
                return;
            }
            FileVO fileVO = object.getByPath("file", FileVO.class);
            if (fileVO == null) {
                System.out.println(response.body());
                System.out.println(StrUtil.format("文件上传失败！路径：{}，名字：{}", path, file.getName()));
            }
        }
    }

    @Override
    public void updateFile(Integer projectId, String authorization, File file, String path, Long fileId) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files/{}", projectId, fileId);
        try (HttpResponse response = HttpRequest.post(url) //
                .auth(authorization) //
                .contentType(ContentType.MULTIPART.getValue()) //
                .form("file", file) //
                .form("filename", file.getName()) //
                .form("path", path) //
                .execute()) {
            JSONObject object = JSONUtil.parseObj(response.body());
            if (Arrays.asList("empty", "hashMatched").contains(object.getByPath("status", String.class))) {
                return;
            }
            FileVO fileVO = object.getByPath("file", FileVO.class);
            if (fileVO == null) {
                System.out.println(response.body());
                System.out.println(StrUtil.format("文件更新失败！路径：{}，名字：{}", path, file.getName()));
            }
        }
    }

    @Override
    public List<StringVO> getStringsByProjectIdAndAuthorization(Integer projectId, String authorization) {
        return getFilesByProjectIdAndAuthorization(projectId, authorization).stream().flatMap(file -> IntStream.rangeClosed(1, (file.getTotal() + 800 - 1) / 800).mapToObj(pageNum -> {
                            String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&page={}&pageSize=800", projectId, file.getId(), pageNum);
                            return HttpRequest.get(url).auth(authorization).execute().body();
                        }) //
                        .map(body -> JSONUtil.toBean(body, PageVO.class).getResults()) //
                        .flatMap(List::stream) //
                        .toList() //
                        .stream()) //
                .collect(Collectors.toList());
    }

    @Override
    public List<StringVO> getStringsByProjectIdAndOriginalAndAndAuthorization(Integer projectId, String original, String authorization) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?original%40={}", projectId, original.replaceAll(" ", "+")); //
        try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
            if (response.isOk()) {
                return JSONUtil.toBean(response.body(), PageVO.class).getResults();
            }
        }
        return null;
    }

    @SneakyThrows
    private <T extends BaseEntity> T getT(Class<T> clazz, StringVO item) {
        return clazz.getConstructor(String.class, String.class).newInstance(item.getOriginal(), item.getTranslation());
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void importStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass) {
        List<T> list = files.stream() //
                .map(file -> {
                    int pageCount = (file.getTotal() - 1) / pageSize + 1;
                    return IntStream.rangeClosed(1, pageCount) //
                            .mapToObj(pageNum -> {
                                String str = "https://paratranz.cn/api/projects/{}/strings?file={}&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800";
                                String url = StrUtil.format(str, file.getProject(), file.getId(), pageNum);
                                try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
                                    return response.body();
                                }
                            }) //
                            .map(body -> JSONUtil.toBean(body, PageVO.class).getResults()) //
                            .flatMap(List::stream) //
                            .filter(result -> StrUtil.isNotBlank(result.getTranslation())) //
                            .map(item -> getT(clazz, item)) //
                            .toList();
                }) //
                .flatMap(List::stream) //
                .toList();
        SpringUtil.getBean(sClass).saveOrUpdateBatch(list);
    }

    @Override
    public void compareParatranz(Integer projectId, String authorization) {
        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    switch (key) {
                        case "common/characters" -> compareStrings(value, authorization, Character.class, CharacterService.class);
//                        case "common/decisions", "common/decisions/categories" -> importStrings(value, authorization, Decision.class, DecisionService.class);
//                        case "common/ideas" -> importStrings(value, authorization, Idea.class, IdeaService.class);
//                        case "common/intelligence_agencies" -> importStrings(value, authorization, IntelligenceAgency.class, IntelligenceAgencyService.class);
//                        case "common/names" -> importStrings(value, authorization, Name.class, NameService.class);
//                        case "common/national_focus" -> importStrings(value, authorization, NationalFocus.class, NationalFocusService.class);
//                        case "common/on_actions" -> importStrings(value, authorization, Action.class, ActionService.class);
//                        case "common/operations" -> importStrings(value, authorization, Operation.class, OperationService.class);
//                        case "common/scripted_effects" -> importStrings(value, authorization, ScriptedEffect.class, ScriptedEffectService.class);
//                        case "common/scripted_triggers" -> importStrings(value, authorization, ScriptedTrigger.class, ScriptedTriggerService.class);
//                        case "common/units/codenames_operatives" -> importStrings(value, authorization, Codename.class, CodenameService.class);
//                        case "common/units/names/" -> importStrings(value, authorization, UnitsName.class, UnitsNameService.class);
//                        case "common/units/names_division", "common/units/names_divisions" -> importStrings(value, authorization, NamesDivision.class, NamesDivisionService.class);
//                        case "common/units/names_railway_guns" -> importStrings(value, authorization, RailwayGun.class, RailwayGunService.class);
//                        case "common/units/names_ships" -> importStrings(value, authorization, NamesShip.class, NamesShipService.class);
//                        case "events" -> importStrings(value, authorization, Event.class, EventService.class);
//                        case "history/countries" -> importStrings(value, authorization, HistoryCountry.class, HistoryCountryService.class);
//                        case "history/units" -> importStrings(value, authorization, HistoryUnit.class, HistoryUnitService.class);
                    }
                });
    }

//    @Override
//    public <T extends BaseEntity, S extends IService<T>> void importStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass) {
//        files.forEach(file -> {
//            int pageCount = (file.getTotal() + 800 - 1) / 800;
//            IntStream.rangeClosed(1, pageCount) //
//                    .mapToObj(pageNum -> {
//                        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800", file.getProject(), file.getId(), pageNum);
//                        return HttpRequest.get(url).auth(authorization).execute().body();
//                    }) //
//                    .map(body -> JSONUtil.toBean(body, PageVO.class).getResults()) //
//                    .flatMap(List::stream) //
//                    .filter(result -> {
//                        T one = SpringUtil.getBean(sClass).getById(result.getOriginal());
//                        return one != null && StrUtil.isBlank(one.getTranslation());
//                    }) //
//                    .forEach(result -> {
//                        try {
//                            T t = clazz.getConstructor(String.class, String.class).newInstance(result.getOriginal(), result.getTranslation());
//                            SpringUtil.getBean(sClass).saveOrUpdate(t);
//                        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//        });
//    }

    @Override
    public void importParatranz(Integer projectId, String authorization) {
        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    switch (key) {
                        case "common/characters" -> importStrings(value, authorization, Character.class, CharacterService.class);
//                        case "common/decisions", "common/decisions/categories" -> importStrings(value, authorization, Decision.class, DecisionService.class);
//                        case "common/ideas" -> importStrings(value, authorization, Idea.class, IdeaService.class);
//                        case "common/intelligence_agencies" -> importStrings(value, authorization, IntelligenceAgency.class, IntelligenceAgencyService.class);
//                        case "common/names" -> importStrings(value, authorization, Name.class, NameService.class);
//                        case "common/national_focus" -> importStrings(value, authorization, NationalFocus.class, NationalFocusService.class);
//                        case "common/on_actions" -> importStrings(value, authorization, Action.class, ActionService.class);
//                        case "common/operations" -> importStrings(value, authorization, Operation.class, OperationService.class);
//                        case "common/scripted_effects" -> importStrings(value, authorization, ScriptedEffect.class, ScriptedEffectService.class);
//                        case "common/scripted_triggers" -> importStrings(value, authorization, ScriptedTrigger.class, ScriptedTriggerService.class);
//                        case "common/units/codenames_operatives" -> importStrings(value, authorization, Codename.class, CodenameService.class);
//                        case "common/units/names/" -> importStrings(value, authorization, UnitsName.class, UnitsNameService.class);
//                        case "common/units/names_division", "common/units/names_divisions" -> importStrings(value, authorization, NamesDivision.class, NamesDivisionService.class);
//                        case "common/units/names_railway_guns" -> importStrings(value, authorization, RailwayGun.class, RailwayGunService.class);
//                        case "common/units/names_ships" -> importStrings(value, authorization, NamesShip.class, NamesShipService.class);
//                        case "events" -> importStrings(value, authorization, Event.class, EventService.class);
//                        case "history/countries" -> importStrings(value, authorization, HistoryCountry.class, HistoryCountryService.class);
//                        case "history/units" -> importStrings(value, authorization, HistoryUnit.class, HistoryUnitService.class);
                    }
                });
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void compareStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass) {
        files.forEach(file -> {
            int pageCount = (file.getTotal() - 1) / pageSize + 1;
            for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&stage=0&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800", file.getProject(), file.getId(), pageNum);
                try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
                    JSONUtil.toBean(response.body(), PageVO.class) //
                            .getResults() //
                            .forEach(result -> {
                                T one = SpringUtil.getBean(sClass).getById(result.getOriginal());
                                if (one == null) {
                                    log.info("原文：{}，翻译：{}，数据库不存在该词条！", result.getOriginal(), result.getTranslation());
                                } else if (StrUtil.isBlank(result.getTranslation()) && StrUtil.isNotBlank(one.getTranslation())) {
                                    updateString();
                                    // TODO: 2023/12/15 更新平台词条
                                } else if (!Objects.equals(one.getTranslation(), result.getTranslation())) {
                                    log.info("原文：{}，翻译：{}，数据库翻译：{}", result.getOriginal(), result.getTranslation(), one.getTranslation());
                                }
                            });
                }
            }
        });
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void exportStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass) {
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);

        files.forEach(file -> {
            int pageCount = (file.getTotal() - 1) / pageSize + 1;
            for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&stage=0&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800", file.getProject(), file.getId(), pageNum);
                try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
                    JSONUtil.toBean(response.body(), PageVO.class) //
                            .getResults() //
                            .forEach(result -> {
                                T one = SpringUtil.getBean(sClass).getById(result.getOriginal());
                                if (one == null) {
                                    log.info("原文：{}，翻译：{}，数据库不存在该词条！", result.getOriginal(), result.getTranslation());
                                } else if (!Objects.equals(one.getTranslation(), result.getTranslation())) {
                                    log.info("原文：{}，翻译：{}，数据库翻译：{}", result.getOriginal(), result.getTranslation(), one.getTranslation());
                                }
                            });
                }


//                JSONUtil.toBean(body, PageVO.class) //
//                        .getResults() //
//                        .stream() //
//                        .filter(result -> {
//                            T one = SpringUtil.getBean(sClass).getById(result.getOriginal());
//                            if (one != null && !Objects.equals(one.getTranslation(), result.getTranslation())) {
//                                log.info("平台词条原文：{}，翻译：{}，数据库翻译：{}", result.getOriginal(), result.getTranslation(), one.getTranslation());
//                            }
//                            if (one != null && !StrUtil.equals(one.getTranslation(), result.getTranslation())) {
//                                result.setTranslation(one.getTranslation());
//                                return true;
//                            } else {
//                                return false;
//                            }
//                        });
//                        .forEach(result -> {
//                            String url2 = StrUtil.format("https://paratranz.cn/api/projects/{}/strings/{}", file.getProject(), result.getId());
//                            Map<String, Object> map = new HashMap<>();
//                            map.put("translation", result.getTranslation());
//                            map.put("stage", 1);
//                            map.put("uid", null);
//                            String body2 = HttpRequest.put(url2).auth(authorization).contentType("application/json").body(JSONUtil.toJsonStr(map, jsonConfig)).execute().body();
//                            log.info("已更新词条：{}", body2);
//                        });
            }
        });
    }


    @Override
    public void updateString(Integer projectId, Integer stringId, String authorization, Map<String, Object> map) {
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
        String url2 = StrUtil.format("https://paratranz.cn/api/projects/{}/strings/{}", projectId, stringId);
//        Map<String, Object> map = new HashMap<>();
//        map.put("translation", result.getTranslation());
//        map.put("stage", 1);
//        map.put("uid", null);
        try (HttpResponse response = HttpRequest.put(url2).auth(authorization).contentType(ContentType.JSON.getValue()).body(JSONUtil.toJsonStr(map, jsonConfig)).execute()) {
            String body = response.body();
            log.info("已更新词条：{}", body);
        }
    }

    @Override
    public void exportParatranz(Integer projectId, String authorization) {
        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    switch (key) {
                        case "common/characters" -> exportStrings(value, authorization, Character.class, CharacterService.class);
//                        case "common/decisions", "common/decisions/categories" -> exportStrings(value, authorization, Decision.class, DecisionService.class);
//                        case "common/ideas" -> exportStrings(value, authorization, Idea.class, IdeaService.class);
//                        case "common/intelligence_agencies" -> exportStrings(value, authorization, IntelligenceAgency.class, IntelligenceAgencyService.class);
//                        case "common/names" -> exportStrings(value, authorization, Name.class, NameService.class);
//                        case "common/national_focus" -> exportStrings(value, authorization, NationalFocus.class, NationalFocusService.class);
//                        case "common/on_actions" -> exportStrings(value, authorization, Action.class, ActionService.class);
//                        case "common/operations" -> exportStrings(value, authorization, Operation.class, OperationService.class);
//                        case "common/scripted_effects" -> exportStrings(value, authorization, ScriptedEffect.class, ScriptedEffectService.class);
//                        case "common/scripted_triggers" -> exportStrings(value, authorization, ScriptedTrigger.class, ScriptedTriggerService.class);
//                        case "common/units/codenames_operatives" -> exportStrings(value, authorization, Codename.class, CodenameService.class);
//                        case "common/units/names/" -> exportStrings(value, authorization, UnitsName.class, UnitsNameService.class);
//                        case "common/units/names_division", "common/units/names_divisions" -> exportStrings(value, authorization, NamesDivision.class, NamesDivisionService.class);
//                        case "common/units/names_railway_guns" -> exportStrings(value, authorization, RailwayGun.class, RailwayGunService.class);
//                        case "common/units/names_ships" -> exportStrings(value, authorization, NamesShip.class, NamesShipService.class);
//                        case "events" -> exportStrings(value, authorization, Event.class, EventService.class);
//                        case "history/countries" -> exportStrings(value, authorization, HistoryCountry.class, HistoryCountryService.class);
//                        case "history/units" -> exportStrings(value, authorization, HistoryUnit.class, HistoryUnitService.class);
                    }
                });
    }
}
