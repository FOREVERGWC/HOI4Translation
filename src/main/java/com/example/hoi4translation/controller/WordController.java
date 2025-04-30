package com.example.hoi4translation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hoi4translation.common.enums.WordStage;
import com.example.hoi4translation.domain.Result;
import com.example.hoi4translation.domain.dto.WordDto;
import com.example.hoi4translation.domain.dto.WordTranslationDto;
import com.example.hoi4translation.domain.dto.WordUnTranslationDto;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.WordVo;
import com.example.hoi4translation.service.IWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 词条前端控制器
 * </p>
 */
@RestController
@RequestMapping("/word")
@Tag(name = "词条", description = "词条")
public class WordController {
    @Resource
    private IWordService wordService;

//    /**
//     * 添加、修改词条
//     *
//     * @param word 词条
//     * @return 结果
//     */
//    @PostMapping
//    @Operation(summary = "添加、修改词条", description = "添加、修改词条", method = "POST")
//    public Result<Void> save(@RequestBody Word word) {
//        wordService.saveOrUpdate(word);
//        return Result.success();
//    }

//    /**
//     * 删除词条
//     *
//     * @param ids ID列表
//     * @return 结果
//     */
//    @DeleteMapping("/{ids}")
//    @Operation(summary = "删除词条", description = "删除词条", method = "DELETE")
//    public Result<Void> removeBatchByIds(@PathVariable List<Long> ids) {
//        wordService.removeBatchByIds(ids);
//        return Result.success();
//    }

    /**
     * 查询词条列表
     *
     * @param dto 词条
     * @return 结果
     */
    @GetMapping("/list")
    @Operation(summary = "查询词条列表", description = "查询词条列表", method = "GET")
    public Result<List<WordVo>> getList(WordDto dto) {
        List<WordVo> list = wordService.getList(dto);
        return Result.success(list);
    }

    /**
     * 查询词条分页
     *
     * @param dto 词条
     * @return 结果
     */
    @PostMapping("/page")
    @Operation(summary = "查询词条分页", description = "查询词条分页", method = "GET")
    public Result<IPage<WordVo>> getPage(@RequestBody WordDto dto) {
        IPage<WordVo> page = wordService.getPage(dto);
        return Result.success(page);
    }

    /**
     * 查询词条
     *
     * @param dto 词条
     * @return 结果
     */
    @GetMapping
    @Operation(summary = "查询词条", description = "查询词条", method = "GET")
    public Result<WordVo> getOne(WordDto dto) {
        WordVo vo = wordService.getOne(dto);
        return Result.success(vo);
    }

//    /**
//     * 查询词条
//     *
//     * @param id 主键ID
//     * @return 结果
//     */
//    @GetMapping("/{id}")
//    @Operation(summary = "查询词条", description = "查询词条", method = "GET")
//    public Result<WordVo> getById(@PathVariable Long id) {
//        WordVo vo = wordService.getOne(WordDto.builder().original(id).build());
//        return Result.success(vo);
//    }

    /**
     * 导出词条
     *
     * @param dto      词条
     * @param response 响应对象
     */
    @PostMapping("/export")
    @Operation(summary = "导出词条", description = "导出词条", method = "GET")
    public void exportExcel(@RequestBody WordDto dto, HttpServletResponse response) {
        wordService.exportExcel(dto, response);
    }

    /**
     * 翻译词条
     *
     * @param dto 词条
     * @return 结果
     */
    @PatchMapping("/translation")
    @Operation(summary = "翻译词条", description = "翻译词条", method = "PATCH")
    public Result<Void> handleTranslation(@Validated @RequestBody WordTranslationDto dto) {
        Word word = new Word();
        BeanUtils.copyProperties(dto, word);
        word.setStage(WordStage.TRANSLATED.getCode());
        wordService.updateByMultiId(word);
        return Result.success();
    }

    /**
     * 还原词条
     *
     * @param dto 词条
     * @return 结果
     */
    @PatchMapping("/un/translation")
    @Operation(summary = "还原词条", description = "还原词条", method = "PATCH")
    public Result<Void> handleUnTranslation(@Validated @RequestBody WordUnTranslationDto dto) {
        Word word = Word.builder()
                .original(dto.getOriginal())
                .key(dto.getKey())
                .stage(WordStage.UNTRANSLATED.getCode())
                .build();
        wordService.updateByMultiId(word);
        return Result.success();
    }
}
