package com.example.hoi4translation.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(name = "词条实体", description = "词条")
public class WordDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 原文
     */
    private SuperQuery original;
    /**
     * 键值
     */
    private Integer key;
    /**
     * 译文
     */
    private SuperQuery translation;
    /**
     * 状态(0未翻译、1已翻译、2忽略)
     */
    private Integer stage;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 页面大小
     */
    private Integer pageSize;
    /**
     * 排序字段
     */
    private String orderBy;
    /**
     * 是否升序
     */
    private Boolean isAsc;
    /**
     * 查询参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;
}
