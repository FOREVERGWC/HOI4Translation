package com.example.hoi4translation.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringVO {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String key;
    private String original;
    private String translation;
    private FileVO file;
    private Integer stage;
    private Integer project;
    private String uid;
    private String extra;
    private String context;
    private Integer words;
    private String version;
    private String history;
    private String importHistory;
    private String comments;
    private Integer fileId;
}
