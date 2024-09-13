package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class TextKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = "^.*country_event(\\$\\$[0-9a-fA-F]+)?\\|(title|option(\\$\\$[0-9a-fA-F]+)?\\|name)$";
        return StrUtil.endWithAny(key, "|desc", "|tooltip", "|custom_effect_tooltip", "add_autonomy_score|localization", "|message", "|text") || ReUtil.isMatch(regex, key);

    }
}
