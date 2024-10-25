package com.example.hoi4translation.strategy;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.service.ParatranzSingleFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.paratranz.file.ParatranzSingleGeneralFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.paratranz.file.ParatranzSingleUnitFileProcessorStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ParatranzKeyMatcherProcessorContext {
    private final Map<String, ParatranzSingleFileProcessorStrategy> map = new HashMap<>();

    public ParatranzKeyMatcherProcessorContext() {
        map.put("common/characters", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/decisions", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/decisions/categories", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/ideas", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/intelligence_agencies", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/names", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/national_focus", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/on_actions", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/operations", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/scripted_effects", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/scripted_triggers", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/units/codenames_operatives", new ParatranzSingleUnitFileProcessorStrategy(null, WordKey.CODE));
        map.put("common/units/names", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("common/units/names_division", new ParatranzSingleUnitFileProcessorStrategy(null, WordKey.TROOP));
        map.put("common/units/names_divisions", new ParatranzSingleUnitFileProcessorStrategy(null, WordKey.TROOP));
        map.put("common/units/names_railway_guns", new ParatranzSingleUnitFileProcessorStrategy(null, WordKey.RAILWAY_GUN));
        map.put("common/units/names_ships", new ParatranzSingleUnitFileProcessorStrategy(null, WordKey.SHIP));
        map.put("events", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("history/countries", new ParatranzSingleGeneralFileProcessorStrategy());
        map.put("history/units", new ParatranzSingleGeneralFileProcessorStrategy());
    }

    public WordKey determineWordKey(String fileName, String key) {
        String folder = fileName.substring(0, fileName.lastIndexOf('/'));
        ParatranzSingleFileProcessorStrategy strategy = map.get(folder);
        return strategy.processFile(key);
    }
}
