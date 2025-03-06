package com.example.hoi4translation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hoi4translation.common.enums.WordStage;
import com.example.hoi4translation.domain.dto.WordDto;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.WordVo;
import com.example.hoi4translation.mapper.WordMapper;
import com.example.hoi4translation.service.IWordService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 * 词条服务实现类
 * </p>
 */
@Service
public class WordServiceImpl extends MppServiceImpl<WordMapper, Word> implements IWordService {
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
    public void exportExcel(Word entity, HttpServletResponse response) {
        // TODO 不需要
    }

    private LambdaQueryChainWrapper<Word> getWrapper(Word entity) {
        LambdaQueryChainWrapper<Word> wrapper = lambdaQuery()
                .eq(entity.getOriginal() != null, Word::getOriginal, entity.getOriginal())
                .eq(entity.getKey() != null, Word::getKey, entity.getKey())
                .eq(entity.getTranslation() != null, Word::getTranslation, entity.getTranslation())
                .eq(entity.getStage() != null && entity.getStage() != WordStage.ALL, Word::getStage, entity.getStage());
        if (entity instanceof WordDto dto) {
            String orderBy = dto.getOrderBy();
            Boolean isAsc = dto.getIsAsc();
            wrapper.orderByAsc(Word::getKey);
        }
        return wrapper;
    }
}
