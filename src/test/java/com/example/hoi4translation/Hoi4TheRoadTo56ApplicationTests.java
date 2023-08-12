package com.example.hoi4translation;

import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.domain.entity.HistoryUnit;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.FileService;
import com.example.hoi4translation.service.ParatranzService;
import com.example.hoi4translation.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
@DisplayName("56之路")
class Hoi4TheRoadTo56ApplicationTests {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private Hoi4Filter hoi4Filter;
    @Autowired
    private ParatranzService paratranzService;

    @Test
    @DisplayName("复制56之路文件")
    void t1() {
        String resource1 = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV";
        String resource2 = "C:\\Program Files (x86)\\Steam\\steamapps\\workshop\\content\\394360\\820260968";
        String destination = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\56之路原版";
        fileService.fileCopy(resource1, destination, hoi4Filter);
        fileService.fileCopy(resource2, destination, hoi4Filter);
    }

    @Test
    @DisplayName("导入56之路词条")
    void t2() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\56之路原版";
        projectService.importProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("导出56之路词条")
    void t3() {
        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\56之路原版";
        projectService.exportProject(file, hoi4Filter);
    }

    @Test
    @DisplayName("从平台导入词条")
    void t4() { // 从平台导入词条
        paratranzService.importParatranz(5762, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    @DisplayName("导出词条到平台")
    void t5() { // 导出词条到平台
        paratranzService.exportParatranz(5762, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
    }

    @Test
    void t6() throws ClassNotFoundException, SQLException {
        List<HistoryUnit> list = new ArrayList<>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/钢铁雄心4五六之路?serverTimezone=UTC", "root", "root");
        PreparedStatement preparedStatement = conn.prepareStatement("select * from `history/units` where translation = ''");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            list.add(HistoryUnit.builder().original(rs.getString(1)).translation(rs.getString(2)).build());
        }


        Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoi4?serverTimezone=UTC", "root", "root");
        for (HistoryUnit one : list) {
            PreparedStatement prepared = conn2.prepareStatement("select * from `history/units` where original = ?");
            prepared.setString(1, one.getOriginal());
            ResultSet resultSet = prepared.executeQuery();
            if (resultSet.next()) {
                if (StrUtil.isNotBlank(resultSet.getString(3))) {
                    one.setTranslation(resultSet.getString(3));
                    System.out.println(one);
//                    SpringUtil.getBean(HistoryUnitService.class).updateById(one);
                }
            }
        }
    }
}
