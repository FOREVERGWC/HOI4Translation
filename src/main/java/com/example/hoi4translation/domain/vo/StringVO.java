package com.example.hoi4translation.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 词条
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class StringVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -799885866673798561L;
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
    /**
     * 词条状态
     */
    private Integer stage;
    /**
     * project
     */
    private Integer project;
    /**
     * 词条最后编辑用户的ID
     */
    private String uid;
    /**
     * extra
     */
    private String extra;
    /**
     * 词条上下文，可通过上传文件或API添加
     */
    private String context;
    /**
     * 词条原文字数
     */
    private Integer words;
    /**
     * version
     */
    private String version;
    /**
     * history
     */
    private String history;
    /**
     * importHistory
     */
    private String importHistory;
    /**
     * comments
     */
    private String comments;
    /**
     * fileId
     */
    private Integer fileId;
}
