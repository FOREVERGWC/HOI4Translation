package com.example.hoi4translation.strategy;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;
import com.example.hoi4translation.strategy.service.impl.key.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class KeyMatcherContext {
    private final Map<WordKey, KeyMatchingStrategy> map = new LinkedHashMap<>();

    public KeyMatcherContext() {
        map.put(WordKey.NAME, new NameKeyMatchingStrategy());
        map.put(WordKey.SHIP, new ShipKeyMatchingStrategy());
        map.put(WordKey.TROOP, new TroopKeyMatchingStrategy());
        map.put(WordKey.CHARACTER, new CharacterKeyMatchingStrategy());
        map.put(WordKey.EQUIPMENT, new EquipmentKeyMatchingStrategy());
        map.put(WordKey.CODE, new CodeNameKeyMatchingStrategy());
        map.put(WordKey.CALLSIGN, new CallsignKeyMatchingStrategy());
        map.put(WordKey.WING, new WingKeyMatchingStrategy());
        map.put(WordKey.FLEET, new FleetKeyMatchingStrategy());
        map.put(WordKey.PARTY, new PartyKeyMatchingStrategy());
        map.put(WordKey.TOPONYM, new ToponymKeyMatchingStrategy());
        map.put(WordKey.TEXT, new TextKeyMatchingStrategy());
        map.put(WordKey.AGENCY, new AgencyKeyMatchingStrategy());
        map.put(WordKey.RAILWAY_GUN, new RailwayGunKeyMatchingStrategy());
        map.put(WordKey.FACTION, new FactionKeyMatchingStrategy());
        map.put(WordKey.IDEAS, new IdeasKeyMatchingStrategy());
        map.put(WordKey.INDUSTRIAL_CONCERN, new IndustrialConcernKeyMatchingStrategy());
        map.put(WordKey.BONUS, new BonusKeyMatchingStrategy());
        map.put(WordKey.EXPERIENCE, new ExperienceKeyMatchingStrategy());
        map.put(WordKey.MATERIEL_MANUFACTURER, new MaterielManufacturerKeyMatchingStrategy());
        map.put(WordKey.NAVAL_MANUFACTURER, new NavalManufacturerKeyMatchingStrategy());
        map.put(WordKey.AIRCRAFT_MANUFACTURER, new AircraftManufacturerKeyMatchingStrategy());
        map.put(WordKey.OPERATION, new OperationKeyMatchingStrategy());
        map.put(WordKey.AFFIX, new AffixKeyMatchingStrategy());
    }

    public WordKey determineWordKey(String key) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().matches(key))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(WordKey.OTHER);
    }
}
