package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.domain.entity.BaseEntity;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public interface FileService {
    void fileCopy(String resource, String destination, FileFilter filter);

    <T extends BaseEntity, S extends IService<T>> void importFiles(List<File> files, Pattern pattern, int group, String tableName, Class<T> tClass, Class<S> sClass);

    void importCharacters(List<File> files, String tableName);

    void importDecisions(List<File> files, String tableName);

    void importIdeas(List<File> files, String tableName);

    void importAgencies(List<File> files, String tableName);

    void importNames(List<File> files, String tableName);

    void importFocuses(List<File> files, String tableName);

    void importActions(List<File> files, String tableName);

    void importOperations(List<File> files, String tableName);

    void importEffects(List<File> files, String tableName);

    void importTriggers(List<File> files, String tableName);

    void importCodeNames(List<File> files, String tableName);

    void importUnitNames(List<File> files, String tableName);

    void importDivisions(List<File> files, String tableName);

    void importRailwayGuns(List<File> files, String tableName);

    void importShips(List<File> files, String tableName);

    void importEvents(List<File> files, String tableName);

    void importHistoryCountries(List<File> files, String tableName);

    void importHistoryUnits(List<File> files, String tableName);

    <T extends BaseEntity, S extends IService<T>> void exportFiles(List<File> files, Pattern pattern, int group, String tableName, Class<T> tClass, Class<S> sClass);

    void exportCharacters(List<File> files, String tableName);

    void exportDecisions(List<File> files, String tableName);

    void exportIdeas(List<File> files, String tableName);

    void exportAgencies(List<File> files, String tableName);

    void exportNames(List<File> files, String tableName);

    void exportFocuses(List<File> files, String tableName);

    void exportActions(List<File> files, String tableName);

    void exportOperations(List<File> files, String tableName);

    void exportEffects(List<File> files, String tableName);

    void exportTriggers(List<File> files, String tableName);

    void exportCodeNames(List<File> files, String tableName);

    void exportUnitNames(List<File> files, String tableName);

    void exportDivisions(List<File> files, String tableName);

    void exportRailwayGuns(List<File> files, String tableName);

    void exportShips(List<File> files, String tableName);

    void exportEvents(List<File> files, String tableName);

    void exportHistoryCountries(List<File> files, String tableName);

    void exportHistoryUnits(List<File> files, String tableName);
}
