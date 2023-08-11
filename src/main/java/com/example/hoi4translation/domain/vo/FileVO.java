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
    private Integer total;
    private Integer translated;
    private Integer disputed;
    private Integer checked;
    private Integer reviewed;
    private Integer hidden;
    private Integer locked;
    private Integer words;
    private String hash;
    private String extra;
    private String folder;
    private ProgressVO progress;
}
