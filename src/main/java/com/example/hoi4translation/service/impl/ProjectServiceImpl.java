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
import com.example.hoi4translation.strategy.ExportFileFileProcessorContext;
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
    private final ExportFileFileProcessorContext exportFileFileProcessorContext = new ExportFileFileProcessorContext();

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
    public void exportProject(String vanilla, String mod, String destination, FileFilter filter) {
        // 清空目录
        FileUtil.clean(destination);
        // 复制文件
        long start = System.currentTimeMillis();
        fileService.fileCopy(vanilla, destination, filter);
        fileService.fileCopy(mod, destination, filter);
        long end = System.currentTimeMillis();
        log.info("【复制文件】耗时：{}秒", (end - start) * 1.0 / 1000);
        // 写入文件
        start = System.currentTimeMillis();
        FileUtil.loopFiles(destination, filter)
                .parallelStream()
                .collect(Collectors.groupingBy(file -> getRelativeDirectory(file.getAbsolutePath(), destination), ConcurrentHashMap::new, Collectors.toList()))
                .forEach(exportFileFileProcessorContext::processFiles);
        end = System.currentTimeMillis();
        log.info("【写入文件】耗时：{}秒", (end - start) * 1.0 / 1000);
    }

    private String getRelativeDirectory(String filePath, String basePath) {
        // TODO 路径考虑Linux适配
        String relativePath = StrUtil.removePrefix(filePath, basePath);
        String withoutFileName = StrUtil.removeSuffix(relativePath, FileUtil.getName(filePath));
        return StrUtil.replace(withoutFileName, StrPool.BACKSLASH, StrPool.SLASH);
    }
}
