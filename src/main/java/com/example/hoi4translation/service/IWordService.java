package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hoi4translation.domain.dto.WordDto;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.domain.vo.WordVo;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 词条服务类
 * </p>
 */
public interface IWordService extends IMppService<Word> {
    /**
     * 查询词条列表
     *
     * @param dto 词条
     * @return 结果
     */
    List<WordVo> getList(WordDto dto);

    /**
     * 查询词条分页
     *
     * @param dto 词条
     * @return 结果
     */
    IPage<WordVo> getPage(WordDto dto);

    /**
     * 查询词条
     *
     * @param dto 词条
     * @return 结果
     */
    WordVo getOne(WordDto dto);

    /**
     * 导出词条
     *
     * @param entity   词条
     * @param response 响应对象
     */
    void exportExcel(Word entity, HttpServletResponse response);
}
