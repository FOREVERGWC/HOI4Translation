package com.example.hoi4translation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.*;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.service.*;
import com.example.hoi4translation.strategy.FileProcessorContext;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import com.example.hoi4translation.strategy.ParatranzFileProcessorContext;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ParatranzServiceImpl implements ParatranzService {
    @Resource
    private IWordService wordService;

    final int pageSize = 800;
    private final ParatranzFileProcessorContext paratranzFileProcessorContext = new ParatranzFileProcessorContext();
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    @Override
    public List<FileVO> getFilesByProjectIdAndAuthorization(Integer projectId, String authorization) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files", projectId);
        try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
            String body = response.body();
            if (!JSONUtil.isTypeJSON(body)) {
                if (Objects.equals(response.getStatus(), 429)) {
                    int time = Integer.parseInt(response.header("Retry-After"));
                    TimeUnit.SECONDS.sleep(time);
                    return getFilesByProjectIdAndAuthorization(projectId, authorization);
                }
                log.error("错误请求头：{}", response.headers());
                log.error("错误状态码：{}", response.getStatus());
                log.error("错误状态：{}", response.isOk());
            }
            return JSONUtil.toList(body, FileVO.class);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            String body = response.body();
            if (!JSONUtil.isTypeJSON(body)) {
                if (Objects.equals(response.getStatus(), 429)) {
                    int time = Integer.parseInt(response.header("Retry-After"));
                    TimeUnit.SECONDS.sleep(time);
                    uploadFile(projectId, authorization, file, path);
                } else {
                    log.error("错误请求头：{}", response.headers());
                    log.error("错误状态码：{}", response.getStatus());
                    log.error("错误状态：{}", response.isOk());
                    log.error("响应信息：{}::{}", file, response.body());
                }
                return;
            }
            JSONObject object = JSONUtil.parseObj(body);
            if (Objects.equals(object.getByPath("status", String.class), "empty")) {
                return;
            }
            FileVO fileVO = object.getByPath("file", FileVO.class);
            if (fileVO == null) {
                System.out.println(response.body());
                System.out.println(StrUtil.format("文件上传失败！路径：{}，名字：{}", path, file.getName()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
            String body = response.body();
            if (!JSONUtil.isTypeJSON(body)) {
                if ("Too many requests, please try again later.".equalsIgnoreCase(body)) {
                    TimeUnit.SECONDS.sleep(5);
                    updateFile(projectId, authorization, file, path, fileId);
                } else {
                    System.out.println(file + "：：" + body);
                }
                return;
            }
            JSONObject object = JSONUtil.parseObj(body);
            if (Objects.equals("empty", object.getByPath("status", String.class))) {
                deleteFile(projectId, authorization, fileId);
                return;
            }
            if (Objects.equals("hashMatched", object.getByPath("status", String.class))) {
                return;
            }
            FileVO fileVO = object.getByPath("file", FileVO.class);
            if (fileVO == null) {
                System.out.println(body);
                System.out.println(StrUtil.format("文件更新失败！路径：{}，名字：{}", path, file.getName()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(Integer projectId, String authorization, Long fileId) {
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/files/{}", projectId, fileId);
        try (HttpResponse ignored = HttpRequest.delete(url) //
                .auth(authorization) //
                .execute()) {
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
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?original%40={}", projectId, original.replaceAll(" ", "+").replaceAll("'", "%27")); //
        try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
            if (response.isOk()) {
                return JSONUtil.toBean(response.body(), PageVO.class).getResults();
            }
        }
        return null;
    }

    @Override
    public void compareParatranz(Integer projectId, String authorization) {
        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    switch (key) {
//                        case "common/characters" -> compareStrings(value, authorization, Character.class, CharacterService.class);
//                        case "common/decisions", "common/decisions/categories" -> compareStrings(value, authorization, Decision.class, DecisionService.class);
//                        case "common/ideas" -> compareStrings(value, authorization, Idea.class, IdeaService.class);
//                        case "common/intelligence_agencies" -> compareStrings(value, authorization, IntelligenceAgency.class, IntelligenceAgencyService.class);
//                        case "common/names" -> compareStrings(value, authorization, Name.class, NameService.class);
//                        case "common/national_focus" -> compareStrings(value, authorization, NationalFocus.class, NationalFocusService.class);
//                        case "common/on_actions" -> compareStrings(value, authorization, Action.class, ActionService.class);
//                        case "common/operations" -> compareStrings(value, authorization, Operation.class, OperationService.class);
//                        case "common/scripted_effects" -> compareStrings(value, authorization, ScriptedEffect.class, ScriptedEffectService.class);
//                        case "common/scripted_triggers" -> compareStrings(value, authorization, ScriptedTrigger.class, ScriptedTriggerService.class);
//                        case "common/units/codenames_operatives" -> compareStrings(value, authorization, Codename.class, CodenameService.class);
//                        case "common/units/names" -> compareStrings(value, authorization, UnitsName.class, UnitsNameService.class);
//                        case "common/units/names_division", "common/units/names_divisions" -> compareStrings(value, authorization, NamesDivision.class, NamesDivisionService.class);
//                        case "common/units/names_railway_guns" -> compareStrings(value, authorization, RailwayGun.class, RailwayGunService.class);
//                        case "common/units/names_ships" -> compareStrings(value, authorization, NamesShip.class, NamesShipService.class);
//                        case "events" -> compareStrings(value, authorization, Event.class, EventService.class);
//                        case "history/countries" -> compareStrings(value, authorization, HistoryCountry.class, HistoryCountryService.class);
//                        case "history/units" -> compareStrings(value, authorization, HistoryUnit.class, HistoryUnitService.class);
                    }
                });
    }

    @Override
    public void importParatranz(Integer projectId, String authorization) {
        List<StringVO> list = new ArrayList<>();
        Set<Word> words = new TreeSet<>(Comparator.comparing(Word::getKey).thenComparing(Word::getOriginal));

        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    paratranzFileProcessorContext.processFiles(authorization, key, value, list, words);
                });

        for (StringVO vo : list) {
            String key = vo.getKey();
            String original = vo.getOriginal().trim();
            String translation = vo.getTranslation().trim();
            WordKey wordKey = keyMatcherContext.determineWordKey(key);
            words.add(Word.builder().original(original).key(wordKey).translation(translation).stage(1).build());
        }

        List<Word> wordList = new ArrayList<>();
        for (Word word : words) {
            Word one = wordService.lambdaQuery()
                    .eq(Word::getOriginal, word.getOriginal())
                    .eq(Word::getKey, word.getKey())
                    .one();
            if (one != null) {
                if (Objects.equals(one.getStage(), word.getStage()) && Objects.equals(one.getTranslation(), word.getTranslation())) {
                    continue;
                }
                one.setTranslation(word.getTranslation().trim());
                one.setStage(1);
                wordList.add(one);
            } else {
                wordList.add(word);
            }
        }
        if (CollectionUtil.isNotEmpty(wordList)) {
            wordService.saveOrUpdateBatchByMultiId(wordList);
        }
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void compareStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass) {
        files.forEach(file -> {
            int pageCount = (file.getTotal() - 1) / pageSize + 1;
            for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&stage=0&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800", file.getProject(), file.getId(), pageNum);
                try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
                    String body = response.body();
                    if (!JSONUtil.isTypeJSON(body)) {
                        if (Objects.equals(response.getStatus(), 429)) {
                            pageNum--;
                            int time = Integer.parseInt(response.header("Retry-After"));
                            TimeUnit.SECONDS.sleep(time);
                            continue;
                        }
                        log.error("错误请求头：{}", response.headers());
                        log.error("错误状态码：{}", response.getStatus());
                        log.error("错误状态：{}", response.isOk());
                    }
                    JSONUtil.toBean(body, PageVO.class) //
                            .getResults() //
                            .forEach(result -> {
                                result.setOriginal(StrUtil.trim(result.getOriginal()));
                                T one = SpringUtil.getBean(sClass).getById(result.getOriginal());
                                // 若数据库不存在该词条则跳过
                                if (one == null) {
                                    return;
                                }
                                if (result.getStage() == 0) {
                                    // 若平台未翻译
                                    if (StrUtil.isNotBlank(one.getTranslation())) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("translation", one.getTranslation());
                                        map.put("stage", 1);
                                        map.put("uid", null);
                                        updateString(file.getProject(), result.getId(), authorization, map);
                                    }
                                } else if (result.getStage() == 1) {
                                    // 若平台已翻译，数据库未翻译，覆盖数据库
                                    if (StrUtil.isNotBlank(result.getTranslation()) && StrUtil.isBlank(one.getTranslation())) {
                                        one.setTranslation(result.getTranslation().trim());
                                        SpringUtil.getBean(sClass).updateById(one);
                                    } else if (StrUtil.isNotBlank(result.getTranslation()) && StrUtil.isNotBlank(one.getTranslation()) && !Objects.equals(result.getTranslation(), one.getTranslation())) {
                                        // 若平台已翻译，数据库已翻译，且两者译文差异，覆盖平台
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("translation", one.getTranslation());
                                        map.put("stage", 1);
                                        map.put("uid", null);
                                        updateString(file.getProject(), result.getId(), authorization, map);
                                    }
                                } else if (result.getStage() == 5) {
                                    // 若平台已审核，数据库未翻译，覆盖数据库
                                    if (StrUtil.isNotBlank(result.getTranslation()) && StrUtil.isBlank(one.getTranslation())) {
                                        one.setTranslation(result.getTranslation().trim());
                                        SpringUtil.getBean(sClass).updateById(one);
                                    } else if (StrUtil.isNotBlank(result.getTranslation()) && StrUtil.isNotBlank(one.getTranslation()) && !Objects.equals(result.getTranslation(), one.getTranslation())) {
                                        // 若平台已翻译，数据库已翻译，且两者译文差异，输出差异
                                        log.info("原文：{}，翻译：{}，数据库翻译：{}", result.getOriginal(), result.getTranslation(), one.getTranslation());
                                    }
                                }
                            });
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
                String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings?file={}&stage=0&page={}&pageSize=800", file.getProject(), file.getId(), pageNum);
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
    public void updateString(Integer projectId, Long stringId, String authorization, Map<String, Object> map) {
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
        String url = StrUtil.format("https://paratranz.cn/api/projects/{}/strings/{}", projectId, stringId);
        try (HttpResponse response = HttpRequest.put(url).auth(authorization).contentType(ContentType.JSON.getValue()).body(JSONUtil.toJsonStr(map, jsonConfig)).execute()) {
            String body = response.body();
            if (!JSONUtil.isTypeJSON(body)) {
                if (Objects.equals(response.getStatus(), 429)) {
                    int time = Integer.parseInt(response.header("Retry-After"));
                    TimeUnit.SECONDS.sleep(time);
                    updateString(projectId, stringId, authorization, map);
                } else {
                    log.error("错误请求头：{}", response.headers());
                    log.error("错误状态码：{}", response.getStatus());
                    log.error("错误状态：{}", response.isOk());
                }
                return;
            }
            log.info("已更新词条：{}", body);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportParatranz(Integer projectId, String authorization) {
        getFilesByProjectIdAndAuthorization(projectId, authorization).stream() //
                .collect(Collectors.groupingBy(FileVO::getFolder, TreeMap::new, Collectors.toList())) //
                .forEach((key, value) -> {
                    switch (key) {
//                        case "common/characters" -> exportStrings(value, authorization, Character.class, CharacterService.class);
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
//                        case "common/units/names" -> exportStrings(value, authorization, UnitsName.class, UnitsNameService.class);
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
