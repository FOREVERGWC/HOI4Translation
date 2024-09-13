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
 * 文件
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class FileVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3547985532189731300L;
    /**
     * 主键ID
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
     * modifiedAt
     */
    private String modifiedAt;
    /**
     * name
     */
    private String name;
    /**
     * project
     */
    private Integer project;
    /**
     * format
     */
    private String format;
    /**
     * 文件总词条数
     */
    private Integer total;
    /**
     * 已翻译词条数
     */
    private Integer translated;
    /**
     * 有疑问的词条数
     */
    private Integer disputed;
    /**
     * 已检查的词条数
     */
    private Integer checked;
    /**
     * 已审核的词条数
     */
    private Integer reviewed;
    /**
     * 已隐藏的词条数
     */
    private Integer hidden;
    /**
     * 已锁定的词条数
     */
    private Integer locked;
    /**
     * 总词数
     */
    private Integer words;
    /**
     * hash
     */
    private String hash;
    /**
     * extra
     */
    private String extra;
    /**
     * folder
     */
    private String folder;
    /**
     * progress
     */
    private ProgressVO progress;
}
