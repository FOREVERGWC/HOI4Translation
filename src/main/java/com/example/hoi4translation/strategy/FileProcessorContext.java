package com.example.hoi4translation.strategy;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.strategy.service.FileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.file.GeneralFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.file.UnitFileProcessorStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

@Slf4j
public class FileProcessorContext {
    private final Map<String, FileProcessorStrategy> map = new HashMap<>();

    public FileProcessorContext() {
        String[] keys = {"callsign", "create_faction", "desc", "division_template", "equipment_variant", "has_template", "localization", "long_name", "name", "set_state_name", "subject", "template_name", "title", "tooltip", "unique", "variant_name", "version_name"};
        map.put("/common/characters/", new GeneralFileProcessorStrategy(new String[]{"name"}));
        map.put("/common/decisions/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/decisions/categories/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/ideas/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/intelligence_agencies/", new GeneralFileProcessorStrategy(new String[]{"names"}));
        map.put("/common/names/", new GeneralFileProcessorStrategy(new String[]{"names", "surnames", "callsigns"}));
        map.put("/common/national_focus/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/on_actions/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/operations/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/scripted_effects/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/scripted_triggers/", new GeneralFileProcessorStrategy(keys));
        map.put("/common/units/codenames_operatives/", new UnitFileProcessorStrategy(WordKey.CODE, "fallback_name", "unique"));
        map.put("/common/units/names/", new GeneralFileProcessorStrategy(new String[]{"generic", "unique"}));
        map.put("/common/units/names_division/", new UnitFileProcessorStrategy(WordKey.TROOP, "name", "fallback_name"));
        map.put("/common/units/names_divisions/", new UnitFileProcessorStrategy(WordKey.TROOP, "name", "fallback_name"));
        map.put("/common/units/names_railway_guns/", new UnitFileProcessorStrategy(WordKey.RAILWAY_GUN, "fallback_name"));
        map.put("/common/units/names_ships/", new UnitFileProcessorStrategy(WordKey.SHIP, "fallback_name", "unique"));
        map.put("/events/", new GeneralFileProcessorStrategy(keys));
        map.put("/history/countries/", new GeneralFileProcessorStrategy(keys));
        map.put("/history/units/", new GeneralFileProcessorStrategy(keys));
    }

    public void processFiles(String directory, List<File> fileList, Collection<StringVO> list, Set<Word> words) {
        FileProcessorStrategy strategy = map.get(directory);
        if (strategy == null) {
            log.info("没有对应的策略：{}", directory);
            return;
        }
        strategy.processFiles(fileList, list, words);
    }
}
