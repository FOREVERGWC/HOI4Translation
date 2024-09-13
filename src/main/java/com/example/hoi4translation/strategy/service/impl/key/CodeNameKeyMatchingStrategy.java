package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class CodeNameKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = ".*_OPERATIVE_CODENAME_HISTORICAL\\|unique\\|.*"
                + "|.*\\|generic\\|codename\\|(unique|generic)\\|.*";
        return StrUtil.startWithAny(key, "generic|codename|unique|") || StrUtil.endWithAny(key, "_OPERATIVE_CODENAME_HISTORICAL|fallback_name") || ReUtil.isMatch(regex, key);
    }
}
