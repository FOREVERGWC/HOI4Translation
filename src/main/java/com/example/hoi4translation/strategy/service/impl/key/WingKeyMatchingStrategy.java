package com.example.hoi4translation.strategy.service.impl.key;

import cn.hutool.core.util.ReUtil;
import com.example.hoi4translation.strategy.service.KeyMatchingStrategy;

public class WingKeyMatchingStrategy implements KeyMatchingStrategy {
    @Override
    public boolean matches(String key) {
        String regex = "^air_wings\\|[^|]+\\|name(\\$\\$[0-9a-fA-F]+)?$"
                + "|.*\\|(cv_small_plane_airframe|cv_small_plane_cas_airframe|cv_small_plane_naval_bomber_airframe"
                + "|small_plane_airframe|small_plane_cas_airframe|small_plane_naval_bomber_airframe|medium_plane_airframe"
                + "|medium_plane_scout_plane_airframe|medium_plane_fighter_airframe|large_plane_airframe"
                + "|large_plane_maritime_patrol_plane_airframe|transport_plane_equipment|jet_fighter_equipment"
                + "|jet_tac_bomber_equipment|jet_strat_bomber_equipment|small_plane_suicide_airframe"
                + "|cv_small_plane_suicide_airframe|fighter_equipment|fighter_bomber_equipment|heavy_fighter_equipment"
                + "|hfighter_bomber_equipment|CAS_equipment|cv_fighter_equipment|cv_CAS_equipment"
                + "|cv_nav_bomber_equipment|tac_bomber_equipment|nav_bomber_equipment|strat_bomber_equipment"
                + "|strat_maritime_equipment|flying_boat_equipment|scout_plane_equipment|jet_strategic_bomber_equipment"
                + "|med_maritime_equipment|strategic_bomber_equipment|rocket_interceptor_equipment"
                + "|strat_maritime_equipment_equipment|cv_jet_fighter_equipment)(\\$\\$[0-9a-fA-F]+)?\\|(unique|generic)\\|.*";
        return ReUtil.isMatch(regex, key);
    }
}
