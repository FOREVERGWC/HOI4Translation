package com.example.hoi4translation.common;

public interface Constants {
    /**
     * 每个文件工作簿数
     */
    Integer PER_FILE_SHEET_COUNT = 5;
    /**
     * 每次写入最大行数
     */
    Integer PER_WRITE_ROW_COUNT = 200000;
    /**
     * 每张工作簿最大行数
     */
    Integer PER_SHEET_ROW_COUNT = 200000;
    /**
     * 每个文件最大数据量
     */
    Integer PER_FILE_ROW_COUNT = PER_FILE_SHEET_COUNT * PER_SHEET_ROW_COUNT;
}
