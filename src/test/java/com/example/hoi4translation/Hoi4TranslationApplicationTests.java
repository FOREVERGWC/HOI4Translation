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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@SpringBootTest
class Hoi4TranslationApplicationTests {
    private final String[] keys = {"callsign", "create_faction", "desc", "division_template", "equipment_variant", "has_template", "localization", "long_name", "name", "set_state_name", "subject", "template_name", "title", "tooltip", "unique", "variant_name", "version_name"};
    @Autowired
    private Hoi4Filter hoi4Filter;
    private final String name = "人名";
    private final String stateName = "地名";
    private final String partyName = "政党";
    private final String unitName = "部队";
    private final String text = "文本";

    @Test
    @DisplayName("导入词条")
    void contextLoads() {
        List<StringVO> list = new ArrayList<>();
        String path = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\56之路原版";
        FileUtil.loopFiles(path, hoi4Filter)
                .stream()
                .collect(Collectors.groupingBy(file -> StrUtil.removeSuffix(StrUtil.removePrefix(file.getAbsolutePath(), path), file.getName()), TreeMap::new, Collectors.toList()))
                .forEach((directory, fileList) -> {
            directory = FileUtil.getAbsolutePath(directory);
            switch (directory) {
                case "/common/characters/" -> {
                    List<StringVO> vos = fileList.stream()
                            .map(file -> {
                                ParadoxParser parser = new ParadoxParser();
                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
                                return ParadoxUtils.getString(object, new ArrayList<>(), "", "name");
                            })
                            .flatMap(Collection::stream)
                            .toList();
                    list.addAll(vos);
                }
                case "/common/decisions/", "/common/decisions/categories/", "/common/ideas/", "/common/national_focus/", "/common/on_actions/", "/common/operations/", "/common/scripted_effects/", "/common/scripted_triggers/", "/events/", "/history/countries/", "/history/units/" -> {
                    List<StringVO> vos = fileList.stream()
                            .map(file -> {
                                ParadoxParser parser = new ParadoxParser();
                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
                                return ParadoxUtils.getString(object, new ArrayList<>(), "", keys);
                            })
                            .flatMap(Collection::stream)
                            .toList();
                    list.addAll(vos);
                }
                case "/common/intelligence_agencies/" -> {
                    List<StringVO> vos = fileList.stream()
                            .map(file -> {
                                ParadoxParser parser = new ParadoxParser();
                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
                                return ParadoxUtils.getString(object, new ArrayList<>(), "", "names");
                            })
                            .flatMap(Collection::stream)
                            .toList();
                    list.addAll(vos);
                }
                case "/common/names/" -> {
                    List<StringVO> vos = fileList.stream()
                            .map(file -> {
                                ParadoxParser parser = new ParadoxParser();
                                JSONObject object = parser.parse(FileUtil.readUtf8String(file));
                                return ParadoxUtils.getString(object, new ArrayList<>(), "", "names", "surnames", "callsigns");
                            })
                            .flatMap(Collection::stream)
                            .toList();
                    list.addAll(vos);
                }
//                case "/common/units/codenames_operatives/", "/common/units/names_ships/" -> fileList.forEach(file -> {
//                    ParadoxParser parser = new ParadoxParser();
//                    JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                    ParadoxUtils.getString(object, "", "fallback_name", "unique");
//                });
//                case "/common/units/names/" -> fileList.forEach(file -> {
//                    ParadoxParser parser = new ParadoxParser();
//                    JSONObject object = parser.parse(FileUtil.readUtf8String(file));
//                    ParadoxUtils.getString(object, "", "generic", "unique");
//                });
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
        List<Word> words = new ArrayList<>();
        FileUtil.writeUtf8String("", "C:\\Users\\FOREVERGWC\\IdeaProjects\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\util\\Info.txt");
        for (StringVO vo : list) {
            String key = vo.getKey();
            String val = vo.getOriginal();
            if (ReUtil.isMatch("^characters\\|.*\\|name$", key)
//                    || ReUtil.isMatch(".*\\|create_country_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || StrUtil.endWith(key, "|create_country_leader|name")
                    || ReUtil.isMatch(".*\\|create_field_marshal(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|create_corps_commander(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|create_navy_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|create_operative_leader(\\$\\$[0-9a-fA-F]+)?\\|name$", key)) {
                // 人名
                words.add(Word.builder().original(val).key(name).translation("").build());
            } else if (StrUtil.endWith(key, "|set_state_name") || ReUtil.isMatch(".*\\|set_province_name(\\$\\$[0-9a-fA-F]+)?\\|name$", key)) {
                // 地名
                words.add(Word.builder().original(val).key(stateName).translation("").build());
            } else if (ReUtil.isMatch(".*\\|set_party_name(\\$\\$[0-9a-fA-F]+)?\\|name$", key)
                    || ReUtil.isMatch(".*\\|set_party_name(\\$\\$[0-9a-fA-F]+)?\\|long_name", key)) {
                // 政党
                words.add(Word.builder().original(val).key(partyName).translation("").build());
            } else if (StrUtil.endWith(key, "|division_template|name")
                    || StrUtil.endWith(key, "|division|name")
                    || StrUtil.endWith(key, "|division|division_template")
                    || StrUtil.endWith(key, "|delete_unit_template_and_units|division_template")) {
                // 部队
                words.add(Word.builder().original(val).key(unitName).translation("").build());
            } else if (StrUtil.endWith(key, "|desc") || StrUtil.endWith(key, "|tooltip")) {
                // 文本
                words.add(Word.builder().original(val).key(text).translation("").build());
            } else {
                FileUtil.appendUtf8String(vo + "\n", "C:\\Users\\FOREVERGWC\\IdeaProjects\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\util\\Info.txt");
            }
        }
    }

    @Test
    void t2() {
        String a = "armenian_army_category|ARM_communist_generals|complete_effect|create_corps_commander|name";
//        String b = "armenian_army_category|ARM_communist_generals|complete_effect|create_corps_commander$$1|name";
        String b = "armenian_army_category|ARM_communist_generals|complete_effect|create_corps_commander";
        if (ReUtil.isMatch(".*\\|create_corps_commander(\\$\\$[0-9a-fA-F]+)?\\|name$", a)) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }

        if (ReUtil.isMatch(".*\\|create_corps_commander(\\$\\$[0-9a-fA-F]+)?\\|name$", b)) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }
    }
}
