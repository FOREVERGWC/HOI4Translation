package com.example.hoi4translation.strategy.service.impl.paratranz.file;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.service.ParatranzSingleFileProcessorStrategy;

public class ParatranzSingleUnitFileProcessorStrategy implements ParatranzSingleFileProcessorStrategy {
    private final WordKey wordKey;

    public ParatranzSingleUnitFileProcessorStrategy(String key, WordKey wordKey) {
        this.wordKey = wordKey;
    }

    @Override
    public WordKey processFile(String key) {
        return wordKey;
    }
}
