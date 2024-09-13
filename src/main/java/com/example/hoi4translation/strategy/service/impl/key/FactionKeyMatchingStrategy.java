package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class FactionKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return StrUtil.endWithAny(key, "|create_faction", "|set_faction_name");
    }
}
