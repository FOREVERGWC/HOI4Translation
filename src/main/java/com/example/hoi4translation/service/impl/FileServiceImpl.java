package com.example.hoi4translation.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.domain.entity.Character;
import com.example.hoi4translation.domain.entity.*;
import com.example.hoi4translation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    final Pattern p1 = Pattern.compile("name\\s*=\\s*\"([^_]*)\"");
    final Pattern p2 = Pattern.compile("\"([^_]*?[^\\\\])\"");
    final Pattern p3 = Pattern.compile("(callsign|create_faction|desc|division_template|equipment_variant|has_template|localization|long_name|name|set_state_name|subject|template_name|title|tooltip|variant_name|version_name)\\s*=\\s*(\"|\\\\\")([^\"_\\\\]*)(\"|\\\\\")");

    @Override
    public void fileCopy(String resource, String destination, FileFilter filter) {
        List<File> files = FileUtil.loopFiles(resource, filter);
        files.forEach(file -> FileUtil.copy(file, new File(destination + StrUtil.removePrefix(file.getPath(), resource)), true));
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void importFiles(List<File> files, Pattern pattern, int group, String tableName, Class<T> tClass, Class<S> sClass) {
        List<T> list = files.stream()
                .flatMap(file -> FileUtil.readUtf8Lines(file).stream())
                .map(line -> line.substring(0, !line.contains("#") ? line.length() : line.indexOf("#")))
                .filter(StrUtil::isNotBlank) // 过滤空行
                .flatMap(line -> ReUtil.findAll(pattern, line, group).stream())
                .map(StrUtil::trim) // 去除单词空格
                .filter(StrUtil::isNotBlank) // 过滤空单词
                .distinct() // 去重
                .filter(name -> SpringUtil.getBean(sClass).getById(name) == null)
                .map(name -> {
                    try {
                        return tClass.getConstructor(String.class, String.class).newInstance(name, "");
                    } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        SpringUtil.getBean(sClass).saveOrUpdateBatch(list);
        log.info("导入{}表：{}条信息", StrUtil.sub(tableName, 1, tableName.length() - 1), list.size());
    }

    @Override
    public void importCharacters(List<File> files, String tableName) {
        importFiles(files, p1, 1, tableName, Character.class, CharacterService.class);
    }

    @Override
    public void importDecisions(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, Decision.class, DecisionService.class);
    }

    @Override
    public void importIdeas(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, Idea.class, IdeaService.class);
    }

    @Override
    public void importAgencies(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, IntelligenceAgency.class, IntelligenceAgencyService.class);
    }

    @Override
    public void importNames(List<File> files, String tableName) {
        log.info("导入{}表：{}条信息", StrUtil.sub(tableName, 1, tableName.length() - 1), 0);
    }

    @Override
    public void importFocuses(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, NationalFocus.class, NationalFocusService.class);
    }

    @Override
    public void importActions(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, Action.class, ActionService.class);
    }

    @Override
    public void importOperations(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, Operation.class, OperationService.class);
    }

    @Override
    public void importEffects(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, ScriptedEffect.class, ScriptedEffectService.class);
    }

    @Override
    public void importTriggers(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, ScriptedTrigger.class, ScriptedTriggerService.class);
    }

    @Override
    public void importCodeNames(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, Codename.class, CodenameService.class);
    }

    @Override
    public void importUnitNames(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, UnitsName.class, UnitsNameService.class);
    }

    @Override
    public void importDivisions(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, NamesDivision.class, NamesDivisionService.class);
    }

    @Override
    public void importRailwayGuns(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, RailwayGun.class, RailwayGunService.class);
    }

    @Override
    public void importShips(List<File> files, String tableName) {
        importFiles(files, p2, 1, tableName, NamesShip.class, NamesShipService.class);
    }

    @Override
    public void importEvents(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, Event.class, EventService.class);
    }

    @Override
    public void importHistoryCountries(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, HistoryCountry.class, HistoryCountryService.class);
    }

    @Override
    public void importHistoryUnits(List<File> files, String tableName) {
        importFiles(files, p3, 3, tableName, HistoryUnit.class, HistoryUnitService.class);
    }

    @Override
    public <T extends BaseEntity, S extends IService<T>> void exportFiles(List<File> files, Pattern pattern, int group, String tableName, Class<T> tClass, Class<S> sClass) {
        files.forEach(file -> {
            AtomicInteger count = new AtomicInteger();
            List<String> list = FileUtil.readUtf8Lines(file).stream()
                    .map(line -> StrUtil.subBefore(line, "#", false)) // 去除注释
                    .map(StrUtil::trimEnd) // 去除右侧空格
                    .filter(StrUtil::isNotBlank) // 过滤空行
                    .map(line -> {
                        StringBuilder modifiedLine = new StringBuilder(line);

                        ReUtil.findAll(pattern, line, group).stream()
                                .filter(StrUtil::isNotBlank)
                                .map(StrUtil::trim)
                                .forEach(string -> Optional.ofNullable(SpringUtil.getBean(sClass).getById(string))
                                        .ifPresent(one -> {
                                            String translation = one.getTranslation();
                                            if (StrUtil.isNotBlank(translation)) {
                                                count.getAndIncrement();
                                                modifiedLine.replace(modifiedLine.indexOf(string), modifiedLine.indexOf(string) + string.length(), translation);
                                            }
                                        }));
                        return modifiedLine.toString();
                    })
                    .collect(Collectors.toList());
            if (count.get() > 0) {
                FileUtil.writeUtf8Lines(list, file);
            } else {
                FileUtil.del(file);
            }
        });
        log.info("导出{}表：{}个文件", StrUtil.sub(tableName, 1, tableName.length() - 1), files.size());
    }

    @Override
    public void exportCharacters(List<File> files, String tableName) {
        exportFiles(files, p1, 1, tableName, Character.class, CharacterService.class);
    }

    @Override
    public void exportDecisions(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, Decision.class, DecisionService.class);
    }

    @Override
    public void exportIdeas(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, Idea.class, IdeaService.class);
    }

    @Override
    public void exportAgencies(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, IntelligenceAgency.class, IntelligenceAgencyService.class);
    }

    @Override
    public void exportNames(List<File> files, String tableName) {
        log.info("导出{}表：{}个文件", StrUtil.sub(tableName, 1, tableName.length() - 1), 0);
    }

    @Override
    public void exportFocuses(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, NationalFocus.class, NationalFocusService.class);
    }

    @Override
    public void exportActions(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, Action.class, ActionService.class);
    }

    @Override
    public void exportOperations(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, Operation.class, OperationService.class);
    }

    @Override
    public void exportEffects(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, ScriptedEffect.class, ScriptedEffectService.class);
    }

    @Override
    public void exportTriggers(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, ScriptedTrigger.class, ScriptedTriggerService.class);
    }

    @Override
    public void exportCodeNames(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, Codename.class, CodenameService.class);
    }

    @Override
    public void exportUnitNames(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, UnitsName.class, UnitsNameService.class);
    }

    @Override
    public void exportDivisions(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, NamesDivision.class, NamesDivisionService.class);
    }

    @Override
    public void exportRailwayGuns(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, RailwayGun.class, RailwayGunService.class);
    }

    @Override
    public void exportShips(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, NamesShip.class, NamesShipService.class);
    }

    @Override
    public void exportEvents(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, Event.class, EventService.class);
    }

    @Override
    public void exportHistoryCountries(List<File> files, String tableName) {
        exportFiles(files, p3, 3, tableName, HistoryCountry.class, HistoryCountryService.class);
    }

    @Override
    public void exportHistoryUnits(List<File> files, String tableName) {
        exportFiles(files, p2, 1, tableName, HistoryUnit.class, HistoryUnitService.class);
    }
}
