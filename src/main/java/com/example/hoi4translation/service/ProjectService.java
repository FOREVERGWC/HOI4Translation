package com.example.hoi4translation.service;

import org.springframework.stereotype.Repository;

import java.io.FileFilter;

@Repository
public interface ProjectService {
    void importProject(String path, FileFilter filter);

    void exportProject(String path, FileFilter filter);
}
