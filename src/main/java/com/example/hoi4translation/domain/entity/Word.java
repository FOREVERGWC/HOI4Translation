package com.example.hoi4translation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@TableName(value = "`word`")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word implements Serializable {
    private Long id; // ID
    private String original; // 原文
    @TableField("`key`")
    private String key; // 键值
    private String translation; // 译文
    private Integer stage; // 状态
}
