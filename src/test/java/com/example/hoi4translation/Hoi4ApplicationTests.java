package com.example.hoi4translation;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hoi4translation.domain.entity.*;
import com.example.hoi4translation.domain.entity.Character;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@SpringBootTest
@DisplayName("原版")
class Hoi4ApplicationTests {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private Hoi4Filter hoi4Filter;
    @Autowired
    private ParatranzService paratranzService;

    @Autowired
    private CharacterService characterService;
    @Autowired
    private NamesShipService namesShipService;

    @Test
    @DisplayName("复制原版文件")
    void t1() {
        String resource = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV";
        String destination = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        fileService.fileCopy(resource, destination, hoi4Filter);
    }

    @Test
    @DisplayName("导入原版词条")
    void t2() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        projectService.importProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("导出原版词条")
    void t3() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        projectService.exportProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("从平台导入词条")
    void t4() {
        paratranzService.importParatranz(5239, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    @DisplayName("导出词条到平台")
    void t5() {
        paratranzService.exportParatranz(5239, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    @DisplayName("从平台查询相同词条")
    void t6() {
        LambdaQueryWrapper<NamesShip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NamesShip::getTranslation, "");
        List<NamesShip> list = namesShipService.list(wrapper);
        list.forEach(item -> {
            List<StringVO> strings = paratranzService.getStringsByProjectIdAndOriginalAndAndAuthorization(96, item.getOriginal(), "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
            strings.forEach(stringVO -> {
                System.out.println("原文：" + stringVO.getOriginal() + "，译文：" + stringVO.getTranslation());
                item.setTranslation(stringVO.getTranslation());
//                namesShipService.updateById(item);
            });
        });
    }

    @Autowired
    private ActionService actionService;
    @Autowired
    private ScriptedEffectService scriptedEffectService;
    @Autowired
    private ScriptedTriggerService scriptedTriggerService;
    @Autowired
    private NamesDivisionService namesDivisionService;
    @Autowired
    private EventService eventService;
    @Autowired
    private HistoryCountryService historyCountryService;
    @Autowired
    private HistoryUnitService historyUnitService;

    @SneakyThrows
    @Test
    @DisplayName("从hoi4库查询相同词条")
    void t7() {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoi4?serverTimezone=UTC", "root", "root");
        LambdaQueryWrapper<HistoryUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HistoryUnit::getTranslation, "");
        List<HistoryUnit> list = historyUnitService.list(wrapper);
        list.forEach(item -> {
            try {
                PreparedStatement ps = conn.prepareStatement("select * from `history/units` where original = ?;");
                ps.setString(1, item.getOriginal());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (!Objects.equals(item.getTranslation(), rs.getString(3))) {
                        if (StrUtil.isNotBlank(rs.getString(3))) {
//                            item.setTranslation(rs.getString(3));
                            System.out.println(item + "，hoi4库翻译：" + rs.getString(3));
                        }
//                        List<StringVO> strings = paratranzService.getStringsByProjectIdAndOriginalAndAndAuthorization(96, item.getOriginal(), "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
//                        if (CollectionUtil.isNotEmpty(strings) && Objects.equals(strings.get(0).getTranslation(), rs.getString(3))) {
//                            item.setTranslation(rs.getString(3));
//                            System.out.println(item + "，hoi4库翻译：" + rs.getString(3));
//                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        historyUnitService.updateBatchById(list);
    }
}
