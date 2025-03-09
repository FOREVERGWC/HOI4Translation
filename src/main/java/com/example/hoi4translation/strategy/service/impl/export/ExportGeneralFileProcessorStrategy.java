package com.example.hoi4translation.strategy.service.impl.export;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.service.impl.WordServiceImpl;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import com.example.hoi4translation.strategy.service.ExportFileProcessorStrategy;
import com.example.hoi4translation.test.ParadoxParserUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportGeneralFileProcessorStrategy implements ExportFileProcessorStrategy {
    private final String[] keys;
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    public ExportGeneralFileProcessorStrategy(String[] keys) {
        this.keys = keys;
    }

    @Override
    public void processFiles(List<File> fileList) {
        fileList.forEach(file -> {
            AtomicInteger counter = new AtomicInteger(0);
            JSONObject object = ParadoxParserUtil.parse(FileUtil.readUtf8Lines(file));
            getString(object, "", counter, keys);

            if (counter.get() == 0) {
                FileUtil.del(file);
            } else {
                String content = ParadoxParserUtil.generate(object);
                FileUtil.writeUtf8String(content, file);
            }
        });
    }

    private void getString(JSONObject json, String currentPath, AtomicInteger counter, String... targetKey) {
        json.entrySet().forEach(item -> processJsonObject(item, targetKey, currentPath, counter));
    }

    private void processJsonObject(Map.Entry<String, Object> item, String[] targetKey, String currentPath, AtomicInteger counter) {
        String key = item.getKey();
        Object value = item.getValue();
        String newPath = currentPath.isEmpty() ? key : currentPath + "|" + key;
        switch (value) {
            case String stringValue when Arrays.stream(targetKey).anyMatch(target -> StrUtil.equals(key, target)) -> {
                if (ReUtil.isMatch("\"[^\"]*\"", stringValue) && !stringValue.contains("_")) {
                    String val = StrUtil.removeSuffix(StrUtil.removePrefix(stringValue, "\""), "\"").trim();
                    WordKey wordKey = keyMatcherContext.determineWordKey(newPath);
                    // TODO 反射获取bean查询优化为注入
                    Word word = SpringUtil.getBean(WordServiceImpl.class).selectByMultiId(Word.builder().original(val).key(wordKey.getCode()).build());
                    if (word == null || !Objects.equals(word.getStage(), 1)) {
                        return;
                    }
                    item.setValue("\"" + word.getTranslation() + "\"");
                    counter.incrementAndGet();
                }
            }
            case JSONObject object -> getString(object, newPath, counter, targetKey);
            case JSONArray array -> processJsonArray(array, newPath, counter, targetKey);
            case null, default -> {
//            System.out.println("其他对象：" + value);
            }
        }
    }

    private void processJsonArray(JSONArray jsonArray, String currentPath, AtomicInteger counter, String... targetKey) {
        String path = StrUtil.subAfter(currentPath, "|", true);
        for (int i = 0; i < jsonArray.size(); i++) {
            Object object = jsonArray.get(i);
            String key = StrUtil.removeSuffix(StrUtil.removePrefix(String.valueOf(object), "\""), "\"").trim();
            if (Arrays.stream(targetKey).anyMatch(target -> StrUtil.equals(path, target)) || currentPath.contains("|ordered|")) {
                // TODO 反射获取bean查询优化为注入
                WordKey wordKey = keyMatcherContext.determineWordKey(currentPath + "|" + key);
                Word word = SpringUtil.getBean(WordServiceImpl.class).selectByMultiId(Word.builder().original(key).key(wordKey.getCode()).build());
                if (word == null || !Objects.equals(word.getStage(), 1)) {
                    continue;
                }
                jsonArray.set(i, "\"" + word.getTranslation() + "\"");
                counter.incrementAndGet();
            }
        }
    }
}
