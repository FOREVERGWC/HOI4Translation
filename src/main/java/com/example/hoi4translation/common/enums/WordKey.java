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
 * 词条键值
 */
@Getter
@AllArgsConstructor
public enum WordKey {
    /**
     * 其他
     */
    OTHER(0, "其他"),
    /**
     * 人物
     */
    CHARACTER(1, "人物"),
    /**
     * 地名
     */
    TOPONYM(2, "地名"),
    /**
     * 政党
     */
    PARTY(3, "政党"),
    /**
     * 部队
     */
    TROOP(4, "部队"),
    /**
     * 文本
     */
    TEXT(5, "文本"),
    /**
     * 装备
     */
    EQUIPMENT(6, "装备"),
    /**
     * 间谍机构
     */
    AGENCY(7, "间谍机构"),
    /**
     * 阵营
     */
    FACTION(8, "阵营"),
    /**
     * 舰名
     */
    SHIP(9, "舰名"),
    /**
     * 人名
     */
    NAME(10, "人名"),
    /**
     * 呼号
     */
    CALLSIGN(11, "呼号"),
    /**
     * 舰队
     */
    FLEET(12, "舰队"),
    /**
     * 飞行联队
     */
    WING(13, "飞行联队"),
    /**
     * 列车炮
     */
    RAILWAY_GUN(14, "列车炮"),
    /**
     * 部队经历
     */
    EXPERIENCE(15, "部队经历"),
    /**
     * 工业制造商
     */
    INDUSTRIAL_CONCERN(16, "工业制造商"),
    /**
     * 装备制造商
     */
    MATERIEL_MANUFACTURER(17, "装备制造商"),
    /**
     * 飞机制造商
     */
    AIRCRAFT_MANUFACTURER(18, "飞机制造商"),
    /**
     * 海军制造商
     */
    NAVAL_MANUFACTURER(19, "海军制造商"),
    /**
     * 民族精神
     */
    IDEAS(20, "民族精神"),
    /**
     * 研究加成
     */
    BONUS(21, "研究加成"),
    /**
     * 间谍代号
     */
    CODE(22, "间谍代号"),
    /**
     * 行动
     */
    OPERATION(23, "行动"),
    /**
     * 词缀
     */
    AFFIX(24, "词缀"),
    /**
     * 军工组织
     */
    MIO(25, "军工组织");

    private static final Map<Integer, WordKey> map = new HashMap<>();

    static {
        for (WordKey item : WordKey.values()) {
            map.put(item.getCode(), item);
        }
    }

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String msg;

    @JsonCreator
    private static WordKey jacksonInstance(final JsonNode jsonNode) {
        Integer code = Convert.toInt(jsonNode.asText(), 0);
        return map.get(code);
    }

    /**
     * 根据键获取枚举
     *
     * @param code 键
     * @return 结果
     */
    public static WordKey getByCode(Integer code) {
        return map.get(code);
    }
}
