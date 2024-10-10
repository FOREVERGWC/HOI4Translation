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

    @Test
    void c() {
        String string = reverseWords("a good   example");
        System.out.println(string);
    }

    public String reverseWords(String s) {
        s = s.trim();                                    // 删除首尾空格
        int j = s.length() - 1, i = j;
        StringBuilder res = new StringBuilder();
        while (i >= 0) {
            while (i >= 0 && s.charAt(i) != ' ') i--;     // 搜索首个空格
            res.append(s, i + 1, j + 1).append(" "); // 添加单词
            while (i >= 0 && s.charAt(i) == ' ') i--;     // 跳过单词间空格
            j = i;                                       // j 指向下个单词的尾字符
        }
        return res.toString().trim();                    // 转化为字符串并返回
    }
}
