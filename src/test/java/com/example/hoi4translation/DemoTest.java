package com.example.hoi4translation;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.strategy.KeyMatcherContext;
import com.example.hoi4translation.test.ParadoxParserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DemoTest {
    private final KeyMatcherContext keyMatcherContext = new KeyMatcherContext();

    @Test
    void a() {
        String key = "#LIST#|KAR|surnames|Minkenen";
        WordKey wordKey = keyMatcherContext.determineWordKey(key);
        System.out.println(wordKey);
    }

    @Test
    void b() {
        List<String> strings = FileUtil.readUtf8Lines("C:\\Users\\91658\\Desktop\\Projects\\Java\\HOI4Translation\\src\\main\\java\\com\\example\\hoi4translation\\utils\\demo.txt");
        JSONObject object = ParadoxParserUtil.parse(strings);
        String string = ParadoxParserUtil.generate(object);
        System.out.println(string);
    }
}
