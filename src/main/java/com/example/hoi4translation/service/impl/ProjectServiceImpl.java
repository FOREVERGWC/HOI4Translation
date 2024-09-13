package com.example.hoi4translation.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.service.FileService;
import com.example.hoi4translation.service.IWordService;
import com.example.hoi4translation.service.ProjectService;
import com.example.hoi4translation.strategy.FileProcessorContext;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private FileService fileService;
    @Resource
    private IWordService wordService;

    private final FileProcessorContext fileProcessorContext = new FileProcessorContext();
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    @Override
    public void importProject(String vanilla, String mod, String destination, FileFilter filter) {
        Queue<StringVO> list = new ConcurrentLinkedQueue<>();
        Set<Word> words = new ConcurrentSkipListSet<>(Comparator.comparing(Word::getKey).thenComparing(Word::getOriginal));

        // 清空目录
        FileUtil.clean(destination);
        // 复制文件
        long start = System.currentTimeMillis();
        fileService.fileCopy(vanilla, destination, filter);
        fileService.fileCopy(mod, destination, filter);
        long end = System.currentTimeMillis();
        log.info("【复制文件】耗时：{}秒", (end - start) * 1.0 / 1000);
        // TODO 清空空词条文件
        // 读入内容
        start = System.currentTimeMillis();
        FileUtil.loopFiles(destination, filter)
                .parallelStream()
                .collect(Collectors.groupingBy(file -> getRelativeDirectory(file.getAbsolutePath(), destination), ConcurrentHashMap::new, Collectors.toList()))
                .forEach((directory, fileList) -> fileProcessorContext.processFiles(directory, fileList, list, words));
        end = System.currentTimeMillis();
        log.info("【读入文件】耗时：{}秒", (end - start) * 1.0 / 1000);
        // 写入数据库
        start = System.currentTimeMillis();
        for (StringVO vo : list) {
            String key = vo.getKey();
            String original = vo.getOriginal();
            WordKey wordKey = keyMatcherContext.determineWordKey(key);
            words.add(Word.builder().original(original).key(wordKey).translation("").stage(0).build());
        }

        List<Word> wordList = new ArrayList<>();
        for (Word word : words) {
            Word one = wordService.lambdaQuery()
                    .eq(Word::getOriginal, word.getOriginal())
                    .eq(Word::getKey, word.getKey())
                    .one();
            if (one != null) {
                continue;
            }
            wordList.add(word);
        }
        wordService.saveBatch(wordList);
        end = System.currentTimeMillis();
        log.info("【写入数据库】耗时：{}秒", (end - start) * 1.0 / 1000);
    }

    @Override
    public void exportProject(String path, FileFilter filter) {
        FileUtil.loopFiles(path, filter).stream() //
                .collect(Collectors.groupingBy(file -> StrUtil.removeSuffix(StrUtil.removePrefix(file.getAbsolutePath(), path), file.getName()), TreeMap::new, Collectors.toList())) //
                .forEach((directory, fileList) -> {
                    directory = FileUtil.getAbsolutePath(directory);
                    switch (directory) {
                        case "/common/characters/" -> fileService.exportCharacters(fileList, directory);
                        case "/common/decisions/", "/common/decisions/categories/" ->
                                fileService.exportDecisions(fileList, directory);
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
                        case "/common/units/names_division/", "/common/units/names_divisions/" ->
                                fileService.exportDivisions(fileList, directory);
                        case "/common/units/names_railway_guns/" -> fileService.exportRailwayGuns(fileList, directory);
                        case "/common/units/names_ships/" -> fileService.exportShips(fileList, directory);
                        case "/events/" -> fileService.exportEvents(fileList, directory);
                        case "/history/countries/" -> fileService.exportHistoryCountries(fileList, directory);
                        case "/history/units/" -> fileService.exportHistoryUnits(fileList, directory);
                    }
                });
    }

    private String getRelativeDirectory(String filePath, String basePath) {
        String relativePath = StrUtil.removePrefix(filePath, basePath);
        String withoutFileName = StrUtil.removeSuffix(relativePath, FileUtil.getName(filePath));
        return StrUtil.replace(withoutFileName, StrPool.BACKSLASH, StrPool.SLASH);
    }
}
