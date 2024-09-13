package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class TroopKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = "(.*\\|)?division_template(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*\\|division(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*\\|(infantry|cavalry|motorized|mechanized|light_armor|medium_armor|heavy_armor|super_heavy_armor|modern_armor|mountaineers|marine|paratrooper|garrison|militia|anti_air_brigade|anti_tank_brigade|artillery_brigade|engineer|field_hospital|logistics_company|maintenance_company|military_police|recon|signal_company)\\|generic\\|.*";
        return ReUtil.isMatch(regex, key) || StrUtil.endWithAny(key, "|division_name|name", "|division_template", "|has_template", "|template_name");
    }
}
