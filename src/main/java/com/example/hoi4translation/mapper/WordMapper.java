package com.example.hoi4translation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hoi4translation.domain.entity.Word;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WordMapper extends MppBaseMapper<Word> {

}
