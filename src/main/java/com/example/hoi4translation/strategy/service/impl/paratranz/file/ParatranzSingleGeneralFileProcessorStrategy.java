package com.example.hoi4translation.strategy.service.impl.paratranz.file;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import com.example.hoi4translation.strategy.service.ParatranzSingleFileProcessorStrategy;

public class ParatranzSingleGeneralFileProcessorStrategy implements ParatranzSingleFileProcessorStrategy {
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    @Override
    public WordKey processFile(String key) {
        return keyMatcherContext.determineWordKey(key);
    }
}
