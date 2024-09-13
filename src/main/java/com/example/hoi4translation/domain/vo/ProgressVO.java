package com.example.hoi4translation.domain.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 进度
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProgressVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8163803808826368048L;
    /**
     * translate
     */
    private Integer translate;
    /**
     * review
     */
    private Integer review;
    /**
     * check
     */
    private Integer check;
}
