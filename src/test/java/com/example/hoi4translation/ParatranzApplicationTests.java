package com.example.hoi4translation;

import cn.hutool.core.io.FileUtil;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.ParatranzService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@DisplayName("云平台")
public class ParatranzApplicationTests {
    Integer projectId = 7694;
    String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";
    // TODO: 2023/12/12 批量上传原版文件
    // TODO: 2023/12/12 批量上传56文件
    // TODO: 2023/12/12 对比原版文件数据库差异词条
    // TODO: 2023/12/12 对比56文件数据库差异词条
    @Autowired
    private Hoi4Filter hoi4Filter;
    @Autowired
    private ParatranzService paratranzService;

    @Test
    @DisplayName("上传原版文件到平台")
    void t1() {
        Map<String, Long> map = paratranzService.getFiles(projectId, authorization);
        String dir = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\原版汉化";
        List<File> files = FileUtil.loopFiles(dir, hoi4Filter);
        files.forEach(file -> {
            String path = FileUtil.subPath(dir, file.getParent()); // 相对子路径
            String fileName = path + "/" + FileUtil.getName(file);
            Long fileId = map.get(fileName);
            if (fileId == null) {
                paratranzService.uploadFile(projectId, authorization, file, path);
            } else {
                paratranzService.updateFile(projectId, authorization, file, path, fileId);
            }
        });
    }
}
