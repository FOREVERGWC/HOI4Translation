package com.example.hoi4translation.strategy.service;

import java.io.File;
import java.util.List;

public interface ExportFileProcessorStrategy {
    void processFiles(List<File> fileList);
}
