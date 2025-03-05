package com.example.hoi4translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.StringVO;
import com.example.hoi4translation.filter.Hoi4Filter;
import com.example.hoi4translation.service.FileService;
import com.example.hoi4translation.service.IWordService;
import com.example.hoi4translation.service.ParatranzService;
import com.example.hoi4translation.service.ProjectService;
import com.example.hoi4translation.strategy.ParatranzKeyMatcherProcessorContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@DisplayName("原版")
class Hoi4ApplicationTests {
    @Resource
    private Hoi4Filter hoi4Filter;
    @Resource
    private FileService fileService;
    @Resource
    private ParatranzService paratranzService;
    @Resource
    private ProjectService projectService;
    @Resource
    private IWordService wordService;

    @Value("${path.vanilla}")
    private String vanilla;
    @Value("${path.original_vanilla}")
    private String original_vanilla;
    @Value("${path.local_vanilla}")
    private String local_vanilla;
    private final Integer projectId = 5239;
    @Value("${path.authorization}")
    private String authorization;

    private final ParatranzKeyMatcherProcessorContext paratranzKeyMatcherProcessorContext = new ParatranzKeyMatcherProcessorContext();

    @Test
    @DisplayName("导入【原版】词条")
    void t1() {
        projectService.importProject(vanilla, null, original_vanilla, hoi4Filter);
    }

    @Test
    @DisplayName("上传【原版】文件")
    void t2() {
        // 清空目录
        FileUtil.clean(original_vanilla);
        // 复制文件
        long start = System.currentTimeMillis();
        fileService.fileCopy(vanilla, original_vanilla, hoi4Filter);
        long end = System.currentTimeMillis();
        log.info("【复制文件】耗时：{}秒", (end - start) * 1.0 / 1000);
        // TODO 清空空词条文件
        // 上传文件
        Map<String, Long> map = paratranzService.getFiles(projectId, authorization);
        List<File> files = FileUtil.loopFiles(original_vanilla, hoi4Filter);
        files.forEach(file -> {
            String path = FileUtil.subPath(original_vanilla, file.getParent());
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
    void t3() {
        paratranzService.importParatranz(projectId, authorization);
    }

    @Test
    @DisplayName("更新数据库未翻译词条")
    void t5sfa() {
        List<Word> wordList = wordService.lambdaQuery()
                .eq(Word::getTranslation, "")
                .eq(Word::getStage, 0)
                .list();
        for (Word word : wordList) {
            List<StringVO> stringList = paratranzService.getStringsByProjectIdAndOriginalAndAndAuthorization(projectId, word.getOriginal(), authorization);
            if (CollectionUtil.isEmpty(stringList)) {
                continue;
            }
            for (StringVO vo : stringList) {
                String fileName = vo.getFile().getName();
                WordKey wordKey = paratranzKeyMatcherProcessorContext.determineWordKey(fileName, vo.getKey());
                if (wordKey != word.getKey()) {
                    continue;
                }
                if (StrUtil.isBlank(vo.getTranslation())) {
                    continue;
                }
                System.out.println("UPDATE `钢铁雄心4`.`word` SET `translation` = '" + vo.getTranslation() + "', `stage` = 1 WHERE `original` = '" + word.getOriginal().replaceAll("'", "''") + "' AND `key` = " + word.getKey().getCode() + ";");
                break;
            }
        }
//        paratranzService.compareParatranz(projectId, authorization);
    }

    @Test
    @DisplayName("导出【原版】词条")
    void t4() {
        projectService.exportProject(vanilla, null, original_vanilla, hoi4Filter);
    }

    @Test
    @DisplayName("复制【原版】到本地模组")
    void t5() {
        // 清空目录
        FileUtil.clean(local_vanilla + "\\common");
        FileUtil.clean(local_vanilla + "\\events");
        FileUtil.clean(local_vanilla + "\\history");
        fileService.fileCopy(original_vanilla, local_vanilla, hoi4Filter);
        // TODO 添加md信息
    }
}
