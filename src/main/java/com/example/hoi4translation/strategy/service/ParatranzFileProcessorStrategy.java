package com.example.hoi4translation.strategy.service;

import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.StringVO;

import java.util.List;
import java.util.Set;

public interface ParatranzFileProcessorStrategy {
    void processFiles(String authorization, List<FileVO> fileList, List<StringVO> list, Set<Word> words);
}
