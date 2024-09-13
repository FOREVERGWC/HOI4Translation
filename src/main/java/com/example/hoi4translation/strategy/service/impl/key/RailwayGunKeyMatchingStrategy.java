package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class RailwayGunKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return ReUtil.isMatch(".*\\|create_railway_gun(\\$\\$[0-9a-fA-F]+)?\\|name$", key);
    }
}
