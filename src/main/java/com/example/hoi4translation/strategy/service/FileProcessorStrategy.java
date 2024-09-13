package com.example.hoi4translation.strategy.service;

import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FileProcessorStrategy {
    void processFiles(List<File> fileList, Collection<StringVO> list, Set<Word> words);
}
