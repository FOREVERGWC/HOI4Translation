package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hoi4translation.domain.dto.WordDto;
import com.example.hoi4translation.domain.vo.WordVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class IWordServiceTest {
    @Resource
    private IWordService wordService;

    @Test
    void getPage() {
        WordDto dto = WordDto.builder().pageNo(1).pageSize(20).build();
        IPage<WordVo> page = wordService.getPage(dto);
        page.getRecords().forEach(System.out::println);
    }
}