package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class AircraftManufacturerKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return ReUtil.isMatch("^ideas\\|aircraft_manufacturer\\|([^|]+)\\|name$", key);
    }
}
