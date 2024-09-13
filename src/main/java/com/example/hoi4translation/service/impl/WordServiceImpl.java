package com.example.hoi4translation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hoi4translation.domain.entity.Word;
import com.example.hoi4translation.mapper.WordMapper;
import com.example.hoi4translation.service.IWordService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WordServiceImpl extends MppServiceImpl<WordMapper, Word> implements IWordService {

}
