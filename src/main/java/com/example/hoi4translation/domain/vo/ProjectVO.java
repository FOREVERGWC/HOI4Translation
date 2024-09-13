package com.example.hoi4translation.domain.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 项目
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProjectVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1792460491064668238L;
    /**
     * id
     */
    private Integer id;
    /**
     * name
     */
    private String name;
}
