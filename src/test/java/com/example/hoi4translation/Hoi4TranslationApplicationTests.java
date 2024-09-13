package com.example.hoi4translation;

import cn.hutool.core.io.FileUtil;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.FileService;
import com.example.hoi4translation.service.ParatranzService;
import com.example.hoi4translation.service.ProjectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.*;

@Slf4j
@SpringBootTest
class Hoi4TranslationApplicationTests {
    @Resource
    private Hoi4Filter hoi4Filter;
    @Resource
    private FileService fileService;
    @Resource
    private ParatranzService paratranzService;
    @Resource
    private ProjectService projectService;

    private final String vanilla = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hearts of Iron IV";
    private final String workshop_56 = "C:\\Program Files (x86)\\Steam\\steamapps\\workshop\\content\\394360\\820260968";
    private final String original_56 = "C:\\Users\\91658\\Desktop\\Games\\56之路原版";
    private final Integer projectId = 5762;
    private final String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";

    @Test
    @DisplayName("导入56词条")
    void contextLoads() {
        projectService.importProject(vanilla, workshop_56, original_56, hoi4Filter);
    }

    @Test
    @DisplayName("上传56文件")
    void upload() {
        // 清空目录
        FileUtil.clean(original_56);
        // 复制文件
        long start = System.currentTimeMillis();
        fileService.fileCopy(vanilla, original_56, hoi4Filter);
        fileService.fileCopy(workshop_56, original_56, hoi4Filter);
        long end = System.currentTimeMillis();
        log.info("【复制文件】耗时：{}秒", (end - start) * 1.0 / 1000);
        // TODO 清空空词条文件
        // 上传文件
        Map<String, Long> map = paratranzService.getFiles(projectId, authorization);
        List<File> files = FileUtil.loopFiles(original_56, hoi4Filter);
        files.forEach(file -> {
            String path = FileUtil.subPath(original_56, file.getParent());
            String fileName = path + "/" + FileUtil.getName(file);
            Long fileId = map.get(fileName);
            if (fileId == null) {
                paratranzService.uploadFile(projectId, authorization, file, path);
            } else {
                paratranzService.updateFile(projectId, authorization, file, path, fileId);
            }
        });
    }

    @Test
    @DisplayName("从平台导入词条")
    void t5() {
        paratranzService.importParatranz(projectId, authorization);
    }
}
