package com.example.hoi4translation.strategy.service.impl.export;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.service.impl.WordServiceImpl;
import com.example.hoi4translation.strategy.service.ExportFileProcessorStrategy;
import com.example.hoi4translation.test.ParadoxParserUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ExportAceFileProcessorStrategy implements ExportFileProcessorStrategy {
    @Override
    public void processFiles(List<File> fileList) {
        fileList.forEach(file -> {
            AtomicInteger counter = new AtomicInteger(0);
            JSONObject object = ParadoxParserUtil.parse(FileUtil.readUtf8Lines(file));

            getString(file.getPath(), object, "", counter, "name");

            if (counter.get() == 0) {
                FileUtil.del(file);
            } else {
                String content = ParadoxParserUtil.generate(object);
                FileUtil.writeUtf8String(content, file);
            }
        });
    }

    private void getString(String filePath, JSONObject json, String currentPath, AtomicInteger counter, String... targetKey) {
        json.entrySet().forEach(item -> processJsonObject(filePath, item, targetKey, currentPath, counter));
    }

    private void processJsonObject(String filePath, Map.Entry<String, Object> item, String[] targetKey, String currentPath, AtomicInteger counter) {
        String key = item.getKey();
        Object value = item.getValue();
        String newPath = currentPath.isEmpty() ? key : currentPath + "|" + key;

        switch (value) {
            case String strVal -> {
                String regex = "GFX_([\\w]+_)?ace_([f|m])?([^\\d]+)";
                var matcher = Pattern.compile(regex).matcher(strVal);

                var names = ReUtil.findAll(matcher.pattern(), strVal, 3);

                if (!matcher.matches()) {
                    break;
                }

                String name = names.getFirst();

                if ("none".equals(name)) {
                    break;
                }

                var stringList = name.split("_");

                String str = strVal;

                // TODO 使用反射优化查询为注入
                for (var s : stringList) {
                    var word = SpringUtil.getBean(WordServiceImpl.class)
                            .selectByMultiId(Word.builder()
                                    .original(s)
                                    .key(WordKey.NAME.getCode())
                                    .build());

                    if (word == null || !Objects.equals(word.getStage(), 1)) {
                        continue;
                    }

                    str = str.replace(s, word.getTranslation());
                    counter.incrementAndGet();
                }

                item.setValue("\"" + str + "\"");
            }
            case JSONObject object -> getString(filePath, object, newPath, counter, targetKey);
            case null, default -> System.out.println("其他对象：" + value);
        }
    }
}
