package com.example.hoi4translation.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StringVO {
    /**
     * 词条ID
     */
    private Long id;
    /**
     * createdAt
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    /**
     * updatedAt
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    /**
     * 词条键值，文件内必须唯一
     */
    private String key;
    /**
     * 词条原文
     */
    private String original;
    /**
     * 词条译文
     */
    private String translation;
    /**
     * 词条所属的文件ID
     */
    private FileVO file;
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
