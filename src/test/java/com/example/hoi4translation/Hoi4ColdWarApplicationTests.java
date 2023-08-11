package com.example.hoi4translation;

import com.example.hoi4translation.domain.entity.Action;
import com.example.hoi4translation.service.ActionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class Hoi4ColdWarApplicationTests {
    @Autowired
    ActionService actionService;

    @Test
    void t1() {
        Action action = actionService.getById("Luo Ruiqing");
        log.info("{}", action);
        log.info("aaaaaaaaaaaaaaaaaaaa");
    }
}
