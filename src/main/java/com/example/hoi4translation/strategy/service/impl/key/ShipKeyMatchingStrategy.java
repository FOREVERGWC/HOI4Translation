package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class ShipKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = ".*\\|(add_equipment_production|ship|create_ship|transfer_ship)(\\$\\$[0-9a-fA-F]+)?\\|(name|prefer_name)$"
                + "|.*\\|(?i)(submarine|destroyer|light_cruiser|heavy_cruiser|battle_cruiser|battleship|SH_battleship|carrier)\\|(unique|generic)\\|.*";
        return ReUtil.isMatch(regex, key);
    }
}
