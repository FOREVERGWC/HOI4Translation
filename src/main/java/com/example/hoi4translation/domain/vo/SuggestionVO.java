package com.example.hoi4translation.domain.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 建议
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class SuggestionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2247578727154348388L;
    /**
     * id
     */
    private Long id;
    /**
     * uid
     */
    private Integer uid;
    /**
     * createdAt
     */
    private String createdAt;
    /**
     * updatedAt
     */
    private String updatedAt;
    /**
     * original
     */
    private String original;
    /**
     * translation
     */
    private String translation;
    /**
     * file
     */
    private Integer file;
    /**
     * stage
     */
    private Integer stage;
    /**
     * project
     */
    private ProjectVO project;
    /**
     * key
     */
    private String key;
    /**
     * pid
     */
    private Integer pid;
    /**
     * user
     */
    private UserVO user;
    /**
     * others
     */
    private String others;
    /**
     * matching
     */
    private BigDecimal matching;
    /**
     * score
     */
    private BigDecimal score;
}
