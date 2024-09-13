package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class EquipmentKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = ".*(add_equipment_to_stockpile|create_equipment_variant)(\\$\\$[0-9a-fA-F]+)?\\|(variant_name|name)$";
        return ReUtil.isMatch(regex, key) || StrUtil.endWithAny(key, "|version_name", "|equipment_variant");
    }
}
