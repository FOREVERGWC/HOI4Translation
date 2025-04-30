package com.example.hoi4translation.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 高级查询
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Schema(name = "高级查询实体", description = "高级查询")
public class SuperQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -4529546068902658896L;
    /**
     * 查询内容
     */
    private String value;
    /**
     * 查询方式
     */
    private String operator;
}
