package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class AgencyKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = "(.*\\|)?(create_intelligence_agency|intelligence_agency)(\\$\\$[0-9a-fA-F]+)?\\|(name|names).*$";
        return ReUtil.isMatch(regex, key);
    }
}
