package com.example.hoi4translation.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {
    private Integer page; // 页码
    private Integer pageSize; // 每页数量
    private Integer rowCount; // 总条数
    private Integer pageCount; // 总页数
    private List<StringVO> results;
}
