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
import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

@EnableMPP
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
    @Resource
    private IWordService wordService;

    @Value("${path.vanilla}")
    private String vanilla;
    @Value("${path.workshop_56}")
    private String workshop_56;
    @Value("${path.original_56}")
    private String original_56;
    @Value("${path.local_56}")
    private String local_56;
    private final Integer projectId = 5762;
    @Value("${path.authorization}")
    private String authorization;

    private final ParatranzKeyMatcherProcessorContext paratranzKeyMatcherProcessorContext = new ParatranzKeyMatcherProcessorContext();

    @Test
    @DisplayName("导入【56之路】词条")
    void t1() {
        projectService.importProject(vanilla, workshop_56, original_56, hoi4Filter);
    }

    @Test
    @DisplayName("上传【56之路】文件")
    void t2() {
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
    @DisplayName("导出【56之路】词条")
    void t4() {
        projectService.exportProject(vanilla, workshop_56, original_56, hoi4Filter);
    }

    @Test
    @DisplayName("复制【56之路】到本地模组")
    void t5() {
        // 清空目录
        FileUtil.clean(local_56 + "\\common");
        FileUtil.clean(local_56 + "\\events");
        FileUtil.clean(local_56 + "\\history");
        fileService.fileCopy(original_56, local_56, hoi4Filter);
        // TODO 添加md信息
    }
}
