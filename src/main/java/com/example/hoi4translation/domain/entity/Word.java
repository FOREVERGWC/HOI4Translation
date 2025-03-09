package com.example.hoi4translation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.hoi4translation.common.enums.WordKey;
import com.example.hoi4translation.common.enums.WordStage;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.text.Normalizer;
import java.util.Objects;

/**
 * <p>
 * 词条
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@TableName("biz_word")
@Schema(name = "词条实体", description = "词条")
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
    private Integer key;
    /**
     * 译文
     */
    private String translation;
    /**
     * 状态(0未翻译、1已翻译、2忽略)
     */
    private Integer stage;

    @TableField(exist = false)
    private String normalizedOriginal;

    public Word(String original, Integer key, String translation, Integer stage) {
        this.original = original;
        this.key = key;
        this.translation = translation;
        this.stage = stage;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Word word = (Word) object;
        if (Objects.equals(original, word.original) && Objects.equals(key, word.key)) {
            return true;
        }
        String normalizedOriginal = Normalizer.normalize(original, Normalizer.Form.NFD);
        String originalWithoutAccents = normalizedOriginal.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String normalizedOtherOriginal = Normalizer.normalize(word.original, Normalizer.Form.NFD);
        String otherOriginalWithoutAccents = normalizedOtherOriginal.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return Objects.equals(originalWithoutAccents, otherOriginalWithoutAccents) && Objects.equals(key, word.key);
    }

    @Override
    public int hashCode() {
        String normalizedOriginal = Normalizer.normalize(original, Normalizer.Form.NFD);
        String originalWithoutAccents = normalizedOriginal.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return Objects.hash(originalWithoutAccents, key);
    }
}
