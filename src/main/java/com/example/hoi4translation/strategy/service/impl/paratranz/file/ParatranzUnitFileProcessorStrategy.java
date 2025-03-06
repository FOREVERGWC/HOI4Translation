package com.example.hoi4translation.strategy.service.impl.paratranz.file;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.common.enums.WordStage;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.PageVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.strategy.service.ParatranzFileProcessorStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class ParatranzUnitFileProcessorStrategy implements ParatranzFileProcessorStrategy {
    private final WordKey wordKey;

    public ParatranzUnitFileProcessorStrategy(WordKey wordKey) {
        this.wordKey = wordKey;
    }

    @Override
    public void processFiles(String authorization, List<FileVO> fileList, List<StringVO> list, Set<Word> words) {
        List<StringVO> voList = fileList.stream()
                .map(file -> parseFile(authorization, file))
                .flatMap(List::stream)
                .toList();
        List<Word> newWords = voList.stream()
                .map(vo -> new Word(vo.getOriginal().trim(), wordKey, vo.getTranslation().trim(), WordStage.TRANSLATED))
                .toList();
        words.addAll(newWords);
    }


    private List<StringVO> parseFile(String authorization, FileVO file) {
        int pageSize = 800;
        int pageCount = (file.getTotal() - 1) / pageSize + 1;
        return IntStream.rangeClosed(1, pageCount) //
                .mapToObj(pageNum -> {
                    String str = "https://paratranz.cn/api/projects/{}/strings?file={}&stage=1&stage=2&stage=3&stage=5&stage=9&page={}&pageSize=800";
                    String url = StrUtil.format(str, file.getProject(), file.getId(), pageNum);
                    try (HttpResponse response = HttpRequest.get(url).auth(authorization).execute()) {
                        return response.body();
                    }
                })
                .map(body -> JSONUtil.toBean(body, PageVO.class).getResults())
                .flatMap(List::stream)
                .filter(result -> StrUtil.isNotBlank(result.getTranslation()))
                .toList();
    }
}
