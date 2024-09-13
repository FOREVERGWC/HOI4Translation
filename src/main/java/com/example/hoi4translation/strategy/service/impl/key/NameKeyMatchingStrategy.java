package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class NameKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        if (key.contains("intelligence_agency")) {
            return false;
        }
        String regex = ".*\\|(add_ace|ace|names|surnames)(\\$\\$[0-9a-fA-F]+)?\\|(name|surname)$"
                + "|.*\\|male\\|names\\|[^|]+"
                + "|.*\\|female\\|names\\|[^|]+"
                + "|.*\\|male\\|surnames\\|[^|]+"
                + "|.*\\|female\\|surnames\\|[^|]+"
                + "|.*\\|surnames\\|[^|]+"
                + "|.*\\|names\\|[^|]+";
        return StrUtil.startWithAny(key, "default|surnames|", "default|male|names|") || ReUtil.isMatch(regex, key);
    }
}
