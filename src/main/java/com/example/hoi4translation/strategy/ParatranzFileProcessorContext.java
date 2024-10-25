package com.example.hoi4translation.strategy;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.strategy.service.ParatranzFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.paratranz.file.ParatranzGeneralFileProcessorStrategy;
import com.example.hoi4translation.strategy.service.impl.paratranz.file.ParatranzUnitFileProcessorStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ParatranzFileProcessorContext {
    private final Map<String, ParatranzFileProcessorStrategy> map = new HashMap<>();

    public ParatranzFileProcessorContext() {
        map.put("common/characters", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/decisions", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/decisions/categories", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/ideas", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/intelligence_agencies", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/names", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/national_focus", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/on_actions", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/operations", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/scripted_effects", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/scripted_triggers", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/units/codenames_operatives", new ParatranzUnitFileProcessorStrategy(WordKey.CODE));
        map.put("common/units/names", new ParatranzGeneralFileProcessorStrategy());
        map.put("common/units/names_division", new ParatranzUnitFileProcessorStrategy(WordKey.TROOP));
        map.put("common/units/names_divisions", new ParatranzUnitFileProcessorStrategy(WordKey.TROOP));
        map.put("common/units/names_railway_guns", new ParatranzUnitFileProcessorStrategy(WordKey.RAILWAY_GUN));
        map.put("common/units/names_ships", new ParatranzUnitFileProcessorStrategy(WordKey.SHIP));
        map.put("events", new ParatranzGeneralFileProcessorStrategy());
        map.put("history/countries", new ParatranzGeneralFileProcessorStrategy());
        map.put("history/units", new ParatranzGeneralFileProcessorStrategy());
//        map.put("localisation/english/replace", new ParatranzGeneralFileProcessorStrategy());
    }

    public void processFiles(String authorization, String directory, List<FileVO> fileList, List<StringVO> list, Set<Word> words) {
        ParatranzFileProcessorStrategy strategy = map.get(directory);
        if (strategy == null) {
            log.info("没有对应的策略：{}", directory);
            return;
        }
        strategy.processFiles(authorization, fileList, list, words);
    }
}
