package com.example.hoi4translation.service;

import org.springframework.stereotype.Repository;

import java.io.FileFilter;

@Repository
public interface ProjectService {
    /**
     * 导入项目
     *
     * @param vanilla     游戏原版路径
     * @param mod         模组路径
     * @param destination 目标路径
     * @param filter      文件过滤器
     */
    void importProject(String vanilla, String mod, String destination, FileFilter filter);

    void exportProject(String path, FileFilter filter);
}
