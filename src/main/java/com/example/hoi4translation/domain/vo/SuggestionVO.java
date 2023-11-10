package com.example.hoi4translation.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionVO {
    private Long id; //
    private Integer uid;
    private String createdAt;
    private String updatedAt;
    private String original;
    private String translation;
    private Integer file; //
    private Integer stage; //
    private ProjectVO project;
    private String key;
    private Integer pid;
    private UserVO user;
    private String others;
    private BigDecimal matching;
    private BigDecimal score;
}
