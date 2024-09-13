package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class ToponymKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return ReUtil.isMatch(".*\\|((set_state_name|set_province_name)(\\$\\$[0-9a-fA-F]+)?(\\|name)?)$", key);
    }
}
