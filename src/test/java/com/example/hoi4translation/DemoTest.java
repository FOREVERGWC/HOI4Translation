package com.example.hoi4translation;

import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoTest {
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    @Test
    void a() {
        String key = "#LIST#|KAR|surnames|Minkenen";
        WordKey wordKey = keyMatcherContext.determineWordKey(key);
        System.out.println(wordKey);
    }
}
