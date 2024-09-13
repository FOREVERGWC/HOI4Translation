//package com.example.hoi4translation;
//
//import cn.hutool.core.io.FileUtil;
//import com.example.hoi4translation.filter.ColdWarFilter;
//import com.example.hoi4translation.service.FileService;
//import com.example.hoi4translation.service.ParatranzService;
//import com.example.hoi4translation.service.ProjectService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@SpringBootTest
//@DisplayName("冷战")
//class Hoi4ColdWarApplicationTests {
//    private final Integer projectId = 7653;
//    private final String authorization = "d61cac8fc2aaf5dc4a4d84b7cfe223c6";
//    @Autowired
//    private FileService fileService;
//    @Autowired
//    private ProjectService projectService;
//    @Autowired
//    private ColdWarFilter coldWarFilter;
//    @Autowired
//    private ParatranzService paratranzService;
//
//    @Test
//    @DisplayName("复制冷战文件")
//    void t1() {
//        String resource = "C:\\Program Files (x86)\\Steam\\steamapps\\workshop\\content\\394360\\1458561226";
//        String destination = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\冷战汉化";
//        fileService.fileCopy(resource, destination, coldWarFilter);
//    }
//
//    @Test
//    @DisplayName("导入冷战词条")
//    void t2() {
//        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\冷战汉化";
//        projectService.importProject(file, coldWarFilter);
//    }
//
//    @Test
//    @DisplayName("导出冷战词条")
//    void t3() {
//        String file = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\冷战汉化";
//        projectService.exportProject(file, coldWarFilter);
//    }
//
//    @Test
//    @DisplayName("上传冷战文件到平台")
//    void t4() {
//        Map<String, Long> map = paratranzService.getFiles(projectId, authorization);
//        String dir = "C:\\Users\\FOREVERGWC\\Desktop\\资料\\游戏\\钢铁雄心4\\Mod\\汉化参考\\冷战汉化";
//        List<File> files = FileUtil.loopFiles(dir, coldWarFilter);
//        files.forEach(file -> {
//            String path = FileUtil.subPath(dir, file.getParent()); // 相对子路径
//            String fileName = path + "/" + FileUtil.getName(file);
//            Long fileId = map.get(fileName);
//            if (fileId == null) {
//                paratranzService.uploadFile(projectId, authorization, file, path);
//            } else {
//                paratranzService.updateFile(projectId, authorization, file, path, fileId);
//            }
//        });
//    }
//
//    @Test
//    @DisplayName("从平台对比词条")
//    void t5() {
//        paratranzService.compareParatranz(projectId, authorization);
//    }
//
//    @Test
//    @DisplayName("从平台导入词条")
//    void t6() {
//        paratranzService.importParatranz(7653, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
//    }
//
//    @Test
//    @DisplayName("导出词条到平台")
//    void t7() {
//        paratranzService.exportParatranz(7653, "d61cac8fc2aaf5dc4a4d84b7cfe223c6");
//    }
//}
