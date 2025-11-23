package com.example.hoi4translation.strategy;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.service.ExportFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.export.ExportGeneralFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.export.ExportUnitFileProcessorStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExportFileFileProcessorContext {
    private final Map<String, ExportFileProcessorStrategy> map = new HashMap<>();

    public ExportFileFileProcessorContext() {
        String[] keys = {"callsign", "create_faction", "desc", "division_template", "equipment_variant", "has_template", "localization", "long_name", "name", "surname", "set_state_name", "subject", "template_name", "title", "tooltip", "unique", "variant_name", "version_name"};
        map.put("/common/characters/", new ExportGeneralFileProcessorStrategy(new String[]{"name"}));
        map.put("/common/decisions/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/decisions/categories/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/ideas/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/intelligence_agencies/", new ExportGeneralFileProcessorStrategy(new String[]{"names"}));
        map.put("/common/military_industrial_organization/organizations/", new ExportGeneralFileProcessorStrategy(new String[]{"name"}));
        map.put("/common/names/", new ExportGeneralFileProcessorStrategy(new String[]{"names", "surnames", "callsigns"}));
        map.put("/common/national_focus/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/on_actions/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/operations/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/scripted_effects/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/scripted_triggers/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/common/units/codenames_operatives/", new ExportUnitFileProcessorStrategy(WordKey.CODE, "fallback_name", "unique"));
        map.put("/common/units/names/", new ExportGeneralFileProcessorStrategy(new String[]{"generic", "unique"}));
        map.put("/common/units/names_division/", new ExportUnitFileProcessorStrategy(WordKey.TROOP, "name", "fallback_name"));
        map.put("/common/units/names_divisions/", new ExportUnitFileProcessorStrategy(WordKey.TROOP, "name", "fallback_name"));
        map.put("/common/units/names_railway_guns/", new ExportUnitFileProcessorStrategy(WordKey.RAILWAY_GUN, "fallback_name"));
        map.put("/common/units/names_ships/", new ExportUnitFileProcessorStrategy(WordKey.SHIP, "fallback_name", "unique"));
        map.put("/events/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/history/countries/", new ExportGeneralFileProcessorStrategy(keys));
        map.put("/history/units/", new ExportGeneralFileProcessorStrategy(keys));
    }

    public void processFiles(String directory, List<File> fileList) {
        ExportFileProcessorStrategy strategy = map.get(directory);

        if (strategy == null) {
            log.info("没有对应的策略：{}", directory);
            return;
        }

        strategy.processFiles(fileList);
    }
}
