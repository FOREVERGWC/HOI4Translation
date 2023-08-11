package com.example.hoi4translation.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.service.FileService;
import com.example.hoi4translation.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileFilter;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private FileService fileService;

    @Override
    public void importProject(String path, FileFilter filter) {
        long startTime = System.currentTimeMillis();

        FileUtil.loopFiles(path, filter).stream()
                .collect(Collectors.groupingBy(file -> StrUtil.removeSuffix(StrUtil.removePrefix(file.getAbsolutePath(), path), file.getName()), TreeMap::new, Collectors.toList()))
                .forEach((directory, fileList) -> {
                    directory = FileUtil.getAbsolutePath(directory);
                    switch (directory) {
                        case "/common/characters/" -> fileService.importCharacters(fileList, directory);
                        case "/common/decisions/", "/common/decisions/categories/" -> fileService.importDecisions(fileList, directory);
                        case "/common/ideas/" -> fileService.importIdeas(fileList, directory);
                        case "/common/intelligence_agencies/" -> fileService.importAgencies(fileList, directory);
                        case "/common/names/" -> fileService.importNames(fileList, directory);
                        case "/common/national_focus/" -> fileService.importFocuses(fileList, directory);
                        case "/common/on_actions/" -> fileService.importActions(fileList, directory);
                        case "/common/operations/" -> fileService.importOperations(fileList, directory);
                        case "/common/scripted_effects/" -> fileService.importEffects(fileList, directory);
                        case "/common/scripted_triggers/" -> fileService.importTriggers(fileList, directory);
                        case "/common/units/codenames_operatives/" -> fileService.importCodeNames(fileList, directory);
                        case "/common/units/names/" -> fileService.importUnitNames(fileList, directory);
                        case "/common/units/names_division/", "/common/units/names_divisions/" -> fileService.importDivisions(fileList, directory);
                        case "/common/units/names_railway_guns/" -> fileService.importRailwayGuns(fileList, directory);
                        case "/common/units/names_ships/" -> fileService.importShips(fileList, directory);
                        case "/events/" -> fileService.importEvents(fileList, directory);
                        case "/history/countries/" -> fileService.importHistoryCountries(fileList, directory);
                        case "/history/units/" -> fileService.importHistoryUnits(fileList, directory);
                    }
                });

        long endTime = System.currentTimeMillis();
        log.info("导入项目运行时间：{}ms", endTime - startTime);
    }

    @Override
    public void exportProject(String path, FileFilter filter) {
        long startTime = System.currentTimeMillis();

        FileUtil.loopFiles(path, filter).stream()
                .collect(Collectors.groupingBy(file -> StrUtil.removeSuffix(StrUtil.removePrefix(file.getAbsolutePath(), path), file.getName()), TreeMap::new, Collectors.toList()))
                .forEach((directory, fileList) -> {
                    directory = FileUtil.getAbsolutePath(directory);
                    switch (directory) {
                        case "/common/characters/" -> fileService.exportCharacters(fileList, directory);
                        case "/common/decisions/", "/common/decisions/categories/" -> fileService.exportDecisions(fileList, directory);
                        case "/common/ideas/" -> fileService.exportIdeas(fileList, directory);
                        case "/common/intelligence_agencies/" -> fileService.exportAgencies(fileList, directory);
                        case "/common/names/" -> fileService.exportNames(fileList, directory);
                        case "/common/national_focus/" -> fileService.exportFocuses(fileList, directory);
                        case "/common/on_actions/" -> fileService.exportActions(fileList, directory);
                        case "/common/operations/" -> fileService.exportOperations(fileList, directory);
                        case "/common/scripted_effects/" -> fileService.exportEffects(fileList, directory);
                        case "/common/scripted_triggers/" -> fileService.exportTriggers(fileList, directory);
                        case "/common/units/codenames_operatives/" -> fileService.exportCodeNames(fileList, directory);
                        case "/common/units/names/" -> fileService.exportUnitNames(fileList, directory);
                        case "/common/units/names_division/", "/common/units/names_divisions/" -> fileService.exportDivisions(fileList, directory);
                        case "/common/units/names_railway_guns/" -> fileService.exportRailwayGuns(fileList, directory);
                        case "/common/units/names_ships/" -> fileService.exportShips(fileList, directory);
                        case "/events/" -> fileService.exportEvents(fileList, directory);
                        case "/history/countries/" -> fileService.exportHistoryCountries(fileList, directory);
                        case "/history/units/" -> fileService.exportHistoryUnits(fileList, directory);
                    }
                });

        long endTime = System.currentTimeMillis();
        log.info("导出项目运行时间：{}ms", endTime - startTime);
    }
}
