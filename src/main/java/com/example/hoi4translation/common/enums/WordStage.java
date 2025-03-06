package com.example.hoi4translation.common.enums;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 词条状态
 */
@Getter
@AllArgsConstructor
public enum WordStage {
    /**
     * 全部
     */
    ALL(null, "全部"),
    /**
     * 未翻译
     */
    UNTRANSLATED(0, "未翻译"),
    /**
     * 已翻译
     */
    TRANSLATED(1, "已翻译"),
    /**
     * 忽略
     */
    IGNORED(2, "忽略");

    private static final Map<Integer, WordStage> map = new HashMap<>();

    static {
        for (WordStage item : WordStage.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static WordStage jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.toInt(jsonNode.asText(), 0);
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static WordStage getByCode(Integer code) {
        return map.get(code);
    }
}
