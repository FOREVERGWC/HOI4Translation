package com.example.hoi4translation;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.util.ParadoxParser;
import com.example.hoi4translation.util.ParadoxUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class Hoi4TranslationApplicationTests {
    private final String[] keys = {"callsign", "create_faction", "desc", "division_template", "equipment_variant", "has_template", "localization", "long_name", "name", "set_state_name", "subject", "template_name", "title", "tooltip", "unique", "variant_name", "version_name"};
    private final String name = "人名";
    private final String stateName = "地名";
    private final String partyName = "政党";
    private final String unitName = "部队";
    private final String text = "文本";
    private final String equipment = "装备";
    private final String intelligenceAgency = "间谍机构";
    private final String faction = "阵营";
    private final String shipName = "舰名";
    private final String randomName = "随机名";
    private final String callsign = "呼号";
    private final String fleet = "舰队";
    private final String wing = "飞行联队";
    private final String railwayGun = "列车炮";
    private final String experience = "部队经历";
    private final String industrialConcern = "工业制造商";
    private final String materielManufacturer = "装备制造商";
    private final String aircraftManufacturer = "飞机制造商";
    private final String b = "坦克制造商";
    private final String navalManufacturer = "海军制造商";
    private final String ideas = "民族精神";
    private final String bonus = "研究加成";
    private final String codeName = "间谍代号";
    @Autowired
    private Hoi4Filter hoi4Filter;

    @Test
    @DisplayName("导入词条")
    void contextLoads() {
        List<StringVO> list = new ArrayList<>();
        List<Word> words = new ArrayList<>();
        String path = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\56之路原版";
        FileUtil.loopFiles(path, hoi4Filter)
                .stream()
                .collect(Collectors.groupingBy(file -> StrUtil.removeSuffix(StrUtil.removePrefix(file.getAbsolutePath(), path), file.getName()), TreeMap::new, Collectors.toList()))
                .forEach((directory, fileList) -> {
                    directory = FileUtil.getAbsolutePath(directory);
                    switch (directory) {
//                        case "/common/characters/" -> {
//                            List<StringVO> vos = fileList.stream()
//                                    .map(file -> {
//                                        ParadoxParser parser = new ParadoxParser();
//                                        JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                        return ParadoxUtils.getString(object, new ArrayList<>(), "", "name");
//                                    })
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                            list.addAll(vos);
//                        }
//                        case "/common/decisions/", "/common/decisions/categories/", "/common/ideas/", "/common/national_focus/", "/common/on_actions/", "/common/operations/", "/common/scripted_effects/", "/common/scripted_triggers/", "/events/", "/history/countries/", "/history/units/" -> {
//                            List<StringVO> vos = fileList.stream()
//                                    .map(file -> {
//                                        ParadoxParser parser = new ParadoxParser();
//                                        JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                        return ParadoxUtils.getString(object, new ArrayList<>(), "", keys);
//                                    })
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                            list.addAll(vos);
//                        }
//                        case "/common/intelligence_agencies/" -> {
//                            List<StringVO> vos = fileList.stream()
//                                    .map(file -> {
//                                        ParadoxParser parser = new ParadoxParser();
//                                        JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                        return ParadoxUtils.getString(object, new ArrayList<>(), "", "names");
//                                    })
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                            list.addAll(vos);
//                        }
//                        case "/common/names/" -> {
//                            List<StringVO> vos = fileList.stream()
//                                    .map(file -> {
//                                        ParadoxParser parser = new ParadoxParser();
//                                        JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                        return ParadoxUtils.getString(object, new ArrayList<>(), "", "names", "surnames", "callsigns");
//                                    })
//                                    .flatMap(Collection::stream)
//                                    .toList();
//                            list.addAll(vos);
//                        }
//                case "/common/units/codenames_operatives/" -> {
//                    List<StringVO> vos =fileList.stream()
//                            .map(file -> {
//                                ParadoxParser parser = new ParadoxParser();
//                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                return ParadoxUtils.getString(object, new ArrayList<>(), "", "fallback_name", "unique");
//                            })
//                            .flatMap(Collection::stream)
//                            .toList();
//                    List<Word> codeNames = vos.stream().map(vo -> Word.builder().original(vo.getOriginal()).key(codeName).translation("").build()).toList();
//                    words.addAll(codeNames);
//                }
//                case "/common/units/names_ships/" -> {
//                    List<StringVO> vos =fileList.stream()
//                            .map(file -> {
//                                ParadoxParser parser = new ParadoxParser();
//                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                                return ParadoxUtils.getString(object, new ArrayList<>(), "", "fallback_name", "unique");
//                            })
//                            .flatMap(Collection::stream)
//                            .toList();
//                    List<Word> shipNames = vos.stream().map(vo -> Word.builder().original(vo.getOriginal()).key(shipName).translation("").build()).toList();
//                    words.addAll(shipNames);
//                }
                case "/common/units/names/" -> {
                    List<StringVO> vos =fileList.stream()
                                    .map(file -> {
                                        ParadoxParser parser = new ParadoxParser();
                                        JSONObject object = parser.parse(FileUtil.readUtf8String(file));
                                        return ParadoxUtils.getString(object, new ArrayList<>(), "", "generic", "unique");
                                    })
                            .flatMap(Collection::stream)
                            .toList();
                    list.addAll(vos);
                }
//                case "/common/units/names_division/", "/common/units/names_divisions/" -> fileList.forEach(file -> {
//                    ParadoxParser parser = new ParadoxParser();
//                    JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                    ParadoxUtils.getString(object, "", "name", "fallback_name");
//                });
//                case "/common/units/names_railway_guns/" -> fileList.forEach(file -> {
//                    ParadoxParser parser = new ParadoxParser();
//                    JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                    ParadoxUtils.getString(object, "", "fallback_name");
//                });
                    }
                });
        FileUtil.writeUtf8String("", "C:\\Users\\FOREVERGWC\\IdeaProjects\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\util\\Info.txt");
        for (StringVO vo : list) {
            String key = vo.getKey();
            String val = vo.getOriginal();
//            FileUtil.appendUtf8String(vo + "\n", "C:\\Users\\FOREVERGWC\\IdeaProjects\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\util\\Info.txt");
            if (ReUtil.isMatch("^characters\\|.*\\|name$", key)
                    || ReUtil.isMatch(".*create_country_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|has_country_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*create_field_marshal(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*create_corps_commander(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*create_navy_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*create_operative_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|set_country_leader_name(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch("^ideas\\|political_advisor\\|([^|]+)\\|name$", key)
                    || StrUtil.endWith(key, "|officer|name")
                    || StrUtil.endWith(key, "|add_corps_commander_role|name")) {
                // 人名
                words.add(Word.builder().original(val).key(name).translation("").build());
            } else if (StrUtil.endWith(key, "|set_state_name") || ReUtil.isMatch(".*\\|set_province_name(\\$\\$[0-9a-fA-F]+)?\\|name$", key)) {
                // 地名
                words.add(Word.builder().original(val).key(stateName).translation("").build());
            } else if (ReUtil.isMatch(".*set_party_name(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*set_party_name(\\$\\$[0-9a-fA-F]+)?\\|long_name", key)
                    || StrUtil.endWith(key, "|set_politics|long_name")
                    || StrUtil.endWith(key, "|set_politics|name")) {
                // 政党
                words.add(Word.builder().original(val).key(partyName).translation("").build());
            } else if (ReUtil.isMatch(".*division_template(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*division(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.endWith(key, "|division_name|name")
                    || StrUtil.endWith(key, "|division_template")
                    || StrUtil.endWith(key, "|has_template")
                    || StrUtil.endWith(key, "|template_name")
                    || ReUtil.isMatch("[^|]+\\|(infantry|cavalry|light_armor|medium_armor|motorized|mountaineers)\\|generic\\|[^|]+", key)) {
                // 部队
                words.add(Word.builder().original(val).key(unitName).translation("").build());
            } else if (StrUtil.endWith(key, "|desc")
                    || StrUtil.endWith(key, "|tooltip")
                    || ReUtil.isMatch("^.*country_event(\\$\\$[0-9a-fA-F]+)?\\|title$", key)
                    || ReUtil.isMatch("^.*country_event(\\$\\$[0-9a-fA-F]+)?\\|option(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.endWith(key, "add_autonomy_score|localization")) {
                // 文本
                words.add(Word.builder().original(val).key(text).translation("").build());
            } else if (ReUtil.isMatch(".*add_equipment_to_stockpile(\\$\\$[0-9a-fA-F]+)?\\|variant_name$", key)
                    || ReUtil.isMatch(".*create_equipment_variant(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.endWith(key, "|version_name")
                    || StrUtil.endWith(key, "|equipment_variant")) {
                // 装备型号
                words.add(Word.builder().original(val).key(equipment).translation("").build());
            } else if (StrUtil.endWith(key, "|create_intelligence_agency|name")
                    || ReUtil.isMatch("^intelligence_agency(\\$\\$[0-9a-fA-F]+)?\\|names.*", key)) {
                // 间谍机构
                words.add(Word.builder().original(val).key(intelligenceAgency).translation("").build());
            } else if (StrUtil.endWith(key, "|create_faction")) {
                // 创建阵营
                words.add(Word.builder().original(val).key(faction).translation("").build());
            } else if (ReUtil.isMatch(".*\\|add_equipment_production(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|ship(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|create_ship(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch("[^|]+\\|(?i)(submarine|destroyer|light_cruiser|heavy_cruiser|battle_cruiser|battleship|carrier)\\|(unique|generic)\\|[^|]+", key)) {
                // 舰名
                words.add(Word.builder().original(val).key(shipName).translation("").build());
            } else if (ReUtil.isMatch(".*\\|add_ace(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|ace(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.startWith(key, "default|surnames|")
                    || StrUtil.startWith(key, "default|male|names|")
                    || ReUtil.isMatch("[^|]+\\|names\\|[^|]+", key)
                    || ReUtil.isMatch("[^|]+\\|male\\|names\\|[^|]+", key)
                    || ReUtil.isMatch("[^|]+\\|female\\|names\\|[^|]+", key)
                    || ReUtil.isMatch("[^|]+\\|surnames\\|[^|]+", key)
                    || ReUtil.isMatch("[^|]+\\|male\\|surnames\\|[^|]+", key)
                    || ReUtil.isMatch("[^|]+\\|female\\|surnames\\|[^|]+", key)) {
                // 随机名
                words.add(Word.builder().original(val).key(randomName).translation("").build());
            } else if (ReUtil.isMatch(".*\\|add_ace(\\$\\$[0-9a-fA-F]+)?\\|callsign$", key)
                    || ReUtil.isMatch(".*\\|ace(\\$\\$[0-9a-fA-F]+)?\\|callsign$", key)
                    || StrUtil.startWith(key, "default|callsigns|")
                    || ReUtil.isMatch("[^|]+\\|callsigns\\|[^|]+", key)) {
                // 呼号
                words.add(Word.builder().original(val).key(callsign).translation("").build());
            } else if (ReUtil.isMatch(".*\\|task_force(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|fleet(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || Objects.equals("units|navy|name", key)) {
                // 舰队
                words.add(Word.builder().original(val).key(fleet).translation("").build());
            } else if (ReUtil.isMatch("^air_wings\\|(\\d+)?\\|name$", key)
                    || ReUtil.isMatch("[^|]+\\|(?i)(cv_small_plane_airframe|cv_small_plane_cas_airframe|cv_small_plane_naval_bomber_airframe|small_plane_airframe|small_plane_cas_airframe|small_plane_naval_bomber_airframe|medium_plane_airframe|medium_plane_scout_plane_airframe|medium_plane_fighter_airframe|large_plane_airframe|large_plane_maritime_patrol_plane_airframe|transport_plane_equipment|jet_fighter_equipment)\\|(unique|generic)\\|[^|]+", key)) {
                // 飞行联队
                words.add(Word.builder().original(val).key(wing).translation("").build());
            } else if (ReUtil.isMatch(".*\\|create_railway_gun(\\$\\$[0-9a-fA-F]+)?\\|name$", key)) {
                // 列车炮
                words.add(Word.builder().original(val).key(railwayGun).translation("").build());
            } else if (StrUtil.endWith(key, "|add_history_entry|subject")) {
                // 部队经历
                words.add(Word.builder().original(val).key(experience).translation("").build());
            } else if (ReUtil.isMatch("^ideas\\|industrial_concern\\|([^|]+)\\|name$", key)) {
                // 工业制造商
                words.add(Word.builder().original(val).key(industrialConcern).translation("").build());
            } else if (ReUtil.isMatch("^ideas\\|materiel_manufacturer\\|([^|]+)\\|name$", key)) {
                // 装备制造商
                words.add(Word.builder().original(val).key(materielManufacturer).translation("").build());
            } else if (ReUtil.isMatch("^ideas\\|naval_manufacturer\\|([^|]+)\\|name$", key)) {
                // 海军制造商
                words.add(Word.builder().original(val).key(navalManufacturer).translation("").build());
            } else if (ReUtil.isMatch("^ideas\\|aircraft_manufacturer\\|([^|]+)\\|name$", key)) {
                // 飞机制造商
                words.add(Word.builder().original(val).key(aircraftManufacturer).translation("").build());
            } else if (ReUtil.isMatch("^ideas\\|country(\\$\\$[0-9a-fA-F]+)?\\|([^|]+)\\|name$", key)) {
                // 民族精神
                words.add(Word.builder().original(val).key(ideas).translation("").build());
            } else if (ReUtil.isMatch(".*\\|add_tech_bonus(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.endWith(key, "|add_doctrine_cost_reduction|name")) {
                // 研究加成
                words.add(Word.builder().original(val).key(bonus).translation("").build());
            } else {
                FileUtil.appendUtf8String(vo + "\n", "C:\\Users\\FOREVERGWC\\IdeaProjects\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\util\\Info.txt");
            }
        }
    }
}
