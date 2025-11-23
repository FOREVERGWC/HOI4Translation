package com.example.hoi4translation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hoi4translation.domain.dto.SuperQuery;
import com.example.hoi4translation.domain.dto.WordDto;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.WordVo;
import com.example.hoi4translation.mapper.WordMapper;
import com.example.hoi4translation.service.IBaseService;
import com.example.hoi4translation.service.IWordService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 词条服务实现类
 * </p>
 */
@Service
public class WordServiceImpl extends MppServiceImpl<WordMapper, Word> implements IWordService, IBaseService<Word> {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public List<WordVo> getList(WordDto dto) {
        List<Word> list = getWrapper(dto).list();
        if (CollectionUtil.isEmpty(list)) {
            return List.of();
        }
        // 组装VO
        return list.stream().map(item -> {
            WordVo vo = new WordVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
    }

    @Override
    public IPage<WordVo> getPage(WordDto dto) {
        Page<Word> info = getWrapper(dto).page(new Page<>(dto.getPageNo(), dto.getPageSize()));
        if (CollectionUtil.isEmpty(info.getRecords())) {
            return new Page<>(dto.getPageNo(), dto.getPageSize(), 0);
        }
        // 组装VO
        return info.convert(item -> {
            WordVo vo = new WordVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        });
    }

    @Override
    public WordVo getOne(WordDto dto) {
        Word one = getWrapper(dto).one();
        if (one == null) {
            return null;
        }
        // 组装VO
        WordVo vo = new WordVo();
        BeanUtils.copyProperties(one, vo);
        return vo;
    }

    @Override
    public void exportExcel(WordDto dto, HttpServletResponse response) {
        List<WordVo> list = getList(dto);
        try {
            String fileName = URLEncoder.encode("word导出", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream(), Word.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("导出数据")
                    .doWrite(list);
        } catch (Exception e) {
            log.error("导出失败", e);
            if (!response.isCommitted()) {
                try {
                    response.reset();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().println("{\"code\":500,\"message\":\"导出失败\"}");
                } catch (IOException ioException) {
                    log.error("写入错误信息失败", ioException);
                }
            }
        }
    }

    private LambdaQueryChainWrapper<Word> getWrapper(WordDto entity) {
        SuperQuery original = entity.getOriginal();
        String originalValue = original == null ? null : original.getValue();
        String originalOperator = original == null ? null : (original.getOperator() == null ? "eq" : original.getOperator());

        SuperQuery translation = entity.getTranslation();
        String translationValue = translation == null ? null : translation.getValue();
        String translationOperator = translation == null ? null : (translation.getOperator() == null ? "eq" : translation.getOperator());

        LambdaQueryChainWrapper<Word> wrapper = lambdaQuery()
                .eq(StrUtil.isNotBlank(originalValue) && "eq".equals(originalOperator), Word::getOriginal, originalValue)
                .like(StrUtil.isNotBlank(originalValue) && "like".equals(originalOperator), Word::getOriginal, originalValue)
                .in(CollectionUtil.isNotEmpty(entity.getKey()), Word::getKey, entity.getKey())
                .eq(StrUtil.isNotBlank(translationValue) && "eq".equals(translationOperator), Word::getTranslation, translationValue)
                .like(StrUtil.isNotBlank(translationValue) && "like".equals(translationOperator), Word::getTranslation, translationValue)
                .eq(entity.getStage() != null, Word::getStage, entity.getStage());

        if (entity instanceof WordDto dto) {
            String orderBy = dto.getOrderBy();
            boolean isAsc = dto.getIsAsc() == null || dto.getIsAsc();
            wrapper.orderBy(Objects.equals(orderBy, "original"), isAsc, Word::getOriginal);
            wrapper.orderBy(Objects.equals(orderBy, "key"), isAsc, Word::getKey);
            wrapper.orderBy(Objects.equals(orderBy, "translation"), isAsc, Word::getTranslation);
            wrapper.orderBy(Objects.equals(orderBy, "stage"), isAsc, Word::getStage);
        }

        return wrapper;
    }

    @Override
    public List<Word> getPageList(Word entity, IPage<Word> page) {
        return List.of();
    }

    @Override
    public LambdaQueryChainWrapper<Word> getWrapper(Word entity) {
        return null;
    }
}
