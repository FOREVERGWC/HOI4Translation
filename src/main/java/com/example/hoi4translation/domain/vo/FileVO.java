package com.example.hoi4translation.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String modifiedAt;
    private String name;
    private Integer project;
    private String format;
    private Integer total; // 文件总词条数
    private Integer translated; // 已翻译词条数
    private Integer disputed; // 有疑问的词条数
    private Integer checked; // 已检查的词条数
    private Integer reviewed; // 已审核的词条数
    private Integer hidden; // 已隐藏的词条数
    private Integer locked; // 已锁定的词条数
    private Integer words; // 总词数
    private String hash;
    private String extra;
    private String folder;
    private ProgressVO progress;
}
