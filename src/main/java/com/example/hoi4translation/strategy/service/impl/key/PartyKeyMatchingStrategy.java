package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class PartyKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = ".*set_party_name(\\$\\$[0-9a-fA-F]+)?\\|(name|long_name)$";
        return ReUtil.isMatch(regex, key) || StrUtil.endWithAny(key, "|set_politics|long_name", "|set_politics|name");
    }
}
