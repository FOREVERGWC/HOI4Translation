package com.example.hoi4translation.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@EqualsAndHashCode(callSuper = false)
@Schema(name = "词条实体", description = "词条")
public class WordUnTranslationDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -427700450014685934L;
    /**
     * 原文
     */
    @NotBlank(message = "原文不能为空！")
    private String original;
    /**
     * 键值
     */
    @NotNull(message = "键值不能为空！")
    private Integer key;
}
