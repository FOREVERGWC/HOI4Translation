package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class BonusKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        return ReUtil.isMatch(".*\\|(add_tech_bonus|add_doctrine_cost_reduction)(\\$\\$[0-9a-fA-F]+)?\\|name$", key);
    }
}
