package com.example.hoi4translation.domain.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 用户
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5729647627545710314L;
    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户最近访问时间
     */
    private String lastVisit;
}
