package com.example.hoi4translation;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hoi4translation.domain.entity.Character;
import com.example.hoi4translation.domain.entity.*;
import com.example.hoi4translation.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@DisplayName("数据库操作")
class TableApplicationTests {
    @Autowired
    private CharacterService characterService;
    @Autowired
    private DecisionService decisionService;
    @Autowired
    private NationalFocusService nationalFocusService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private ScriptedEffectService scriptedEffectService;
    @Autowired
    private UnitsNameService unitsNameService;
    @Autowired
    private NamesDivisionService namesDivisionService;
    @Autowired
    private NamesShipService namesShipService;
    @Autowired
    private EventService eventService;
    @Autowired
    private HistoryCountryService historyCountryService;
    @Autowired
    private HistoryUnitService historyUnitService;

    @Test
    @DisplayName("覆盖表common/characters相同词条到表common/national_focus")
    void t1() {
        characterService.list(new LambdaQueryWrapper<Character>().ne(Character::getTranslation, "")).forEach(item -> {
            NationalFocus one = nationalFocusService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                nationalFocusService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/characters相同词条到表common/scripted_effects")
    void t2() {
        characterService.list(new LambdaQueryWrapper<Character>().ne(Character::getTranslation, "")).forEach(item -> {
            ScriptedEffect one = scriptedEffectService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                scriptedEffectService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/characters相同词条到表events")
    void t3() {
        characterService.list(new LambdaQueryWrapper<Character>().ne(Character::getTranslation, "")).forEach(item -> {
            Event one = eventService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                eventService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/characters相同词条到表history/countries")
    void t4() {
        characterService.list(new LambdaQueryWrapper<Character>().ne(Character::getTranslation, "")).forEach(item -> {
            HistoryCountry one = historyCountryService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyCountryService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/decisions相同词条到表common/national_focus")
    void t5() {
        decisionService.list(new LambdaQueryWrapper<Decision>().ne(Decision::getTranslation, "")).forEach(item -> {
            NationalFocus one = nationalFocusService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                nationalFocusService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/decisions相同词条到表common/on_actions")
    void t6() {
        decisionService.list(new LambdaQueryWrapper<Decision>().ne(Decision::getTranslation, "")).forEach(item -> {
            Action one = actionService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                actionService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/decisions相同词条到表events")
    void t7() {
        decisionService.list(new LambdaQueryWrapper<Decision>().ne(Decision::getTranslation, "")).forEach(item -> {
            Event one = eventService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                eventService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/decisions相同词条到表history/countries")
    void t8() {
        decisionService.list(new LambdaQueryWrapper<Decision>().ne(Decision::getTranslation, "")).forEach(item -> {
            HistoryCountry one = historyCountryService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyCountryService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/national_focus相同词条到表events")
    void t9() {
        nationalFocusService.list(new LambdaQueryWrapper<NationalFocus>().ne(NationalFocus::getTranslation, "")).forEach(item -> {
            Event one = eventService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                eventService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/national_focus相同词条到表history/countries")
    void t10() {
        nationalFocusService.list(new LambdaQueryWrapper<NationalFocus>().ne(NationalFocus::getTranslation, "")).forEach(item -> {
            HistoryCountry one = historyCountryService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyCountryService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/units/names相同词条到表history/units")
    void t11() {
        unitsNameService.list(new LambdaQueryWrapper<UnitsName>().ne(UnitsName::getTranslation, "")).forEach(item -> {
            HistoryUnit one = historyUnitService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyUnitService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/units/names_divisions相同词条到表history/units")
    void t12() {
        namesDivisionService.list(new LambdaQueryWrapper<NamesDivision>().ne(NamesDivision::getTranslation, "")).forEach(item -> {
            HistoryUnit one = historyUnitService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyUnitService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表common/units/names_ships相同词条到表history/units")
    void t13() {
        namesShipService.list(new LambdaQueryWrapper<NamesShip>().ne(NamesShip::getTranslation, "")).forEach(item -> {
            HistoryUnit one = historyUnitService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyUnitService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表events相同词条到表history/countries")
    void t14() {
        eventService.list(new LambdaQueryWrapper<Event>().ne(Event::getTranslation, "")).forEach(item -> {
            HistoryCountry one = historyCountryService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyCountryService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }

    @Test
    @DisplayName("覆盖表history/countries相同词条到表history/units")
    void t15() {
        historyCountryService.list(new LambdaQueryWrapper<HistoryCountry>().ne(HistoryCountry::getTranslation, "")).forEach(item -> {
            HistoryUnit one = historyUnitService.getById(item.getOriginal());
            if (one != null && !StrUtil.equals(item.getTranslation(), one.getTranslation())) {
                one.setTranslation(item.getTranslation());
//                historyUnitService.updateById(one);
                log.info("更新词条：{}", one);
            }
        });
    }
}
