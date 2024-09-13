package com.example.hoi4translation.strategy.service.impl.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.strategy.service.FileProcessorStrategy;
import com.example.hoi4translation.test.ParadoxParserUtil;
import com.example.hoi4translation.utils.ParadoxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UnitFileProcessorStrategy implements FileProcessorStrategy {
    private final WordKey wordKey;
    private final String[] keys;

    public UnitFileProcessorStrategy(WordKey wordKey, String... valueKeys) {
        this.wordKey = wordKey;
        this.keys = valueKeys;
    }

    @Override
    public void processFiles(List<File> fileList, Collection<StringVO> list, Set<Word> words) {
        List<StringVO> vos = fileList.parallelStream()
                .map(file -> parseFile(file, keys))
                .flatMap(Collection::stream)
                .filter(item -> StrUtil.isNotBlank(item.getOriginal()))
                .toList();

        vos.stream()
                .map(vo -> Word.builder().original(vo.getOriginal().trim()).key(wordKey).translation("").stage(0).build())
                .forEach(words::add);
    }

    private List<StringVO> parseFile(File file, String... keys) {
        JSONObject object = ParadoxParserUtil.parse(FileUtil.readUtf8Lines(file));
        return ParadoxUtils.getString(object, new ArrayList<>(), "", keys);
        // TODO 判断List<StringVO>为空列表则删除该文件
    }
}
