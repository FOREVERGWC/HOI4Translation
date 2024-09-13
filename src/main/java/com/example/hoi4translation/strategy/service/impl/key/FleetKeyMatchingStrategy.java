package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class FleetKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return StrUtil.startWithAny(key, "units|navy|name") || ReUtil.isMatch(".*\\|fleet.*\\|name$", key);
    }
}
