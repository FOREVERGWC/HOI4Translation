package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class CharacterKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = "^characters\\|.*\\|(name|idea_token)(\\$\\$[0-9a-fA-F]+)?$"
                + "|.*create_country_leader(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*\\|has_country_leader(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*create_field_marshal(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*create_corps_commander(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*create_navy_leader(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*create_operative_leader(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|.*\\|set_country_leader_name(\\$\\$[0-9a-fA-F]+)?\\|name$"
                + "|^ideas\\|political_advisor\\|([^|]+)\\|name$"
                + "|.*\\|officer\\|name$"
                + "|.*\\|add_corps_commander_role\\|name$"
                + "|.*\\|set_character_name(\\|name)?$";
        return ReUtil.isMatch(regex, key);
    }
}
