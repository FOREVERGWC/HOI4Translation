package com.example.hoi4translation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.hoi4translation.common.enums.WordKey;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 词条
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(of = {"original", "key"})
public class Word implements Serializable {
    @Serial
    private static final long serialVersionUID = -678097295922921481L;
    /**
     * 原文
     */
    @MppMultiId
    @TableField
    private String original;
    /**
     * 键值
     */
    @MppMultiId
    @TableField("`key`")
    private WordKey key;
    /**
     * 译文
     */
    private String translation;
    /**
     * 状态(0未翻译、1已翻译、2忽略)
     */
    private Integer stage;
}
