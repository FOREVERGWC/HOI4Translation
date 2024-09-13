package com.example.hoi4translation.domain.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 分页
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class PageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5864167754089507252L;
    /**
     * 页码
     */
    private Integer page;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 总条数
     */
    private Integer rowCount;
    /**
     * 总页数
     */
    private Integer pageCount;
    /**
     * results
     */
    private List<StringVO> results;
}
