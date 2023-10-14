package com.example.hoi4translation.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringVO {
    private Long id; // 词条ID
    private String createdAt;
    private String updatedAt;
    private String key; // 词条键值，文件内必须唯一
    private String original; // 词条原文
    private String translation; // 词条译文
    private FileVO file; // 词条所属的文件ID
    private Integer stage; // 词条状态
    private Integer project;
    private String uid; // 词条最后编辑用户的ID
    private String extra;
    private String context; // 词条上下文，可通过上传文件或API添加
    private Integer words; // 词条原文字数
    private String version;
    private String history;
    private String importHistory;
    private String comments;
    private Integer fileId;
}
