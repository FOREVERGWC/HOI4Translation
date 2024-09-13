package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class CallsignKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = ".*\\|(add_ace|ace)(\\$\\$[0-9a-fA-F]+)?\\|callsign$" + "|.*\\|callsigns\\|.*";
        return StrUtil.startWithAny(key, "default|callsigns|") || ReUtil.isMatch(regex, key);
    }
}
