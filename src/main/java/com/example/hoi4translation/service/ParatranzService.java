package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.domain.entity.BaseEntity;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.StringVO;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;

@Repository
public interface ParatranzService {
    /**
     * 根据项目ID和令牌查询项目所有文件
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     * @return 文件列表
     */
    List<FileVO> getFilesByProjectIdAndAuthorization(Integer projectId, String authorization);

    /**
     * 根据项目ID和令牌获取文件名与文件ID集合
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     * @return 文件名与文件ID集合
     */
    Map<String, Long> getFiles(Integer projectId, String authorization);

    /**
     * 上传文件
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     * @param file          文件对象
     * @param path          路径
     */
    void uploadFile(Integer projectId, String authorization, File file, String path);

    /**
     * 更新文件
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     * @param file          文件对象
     * @param path          路径
     * @param fileId        文件ID
     */
    void updateFile(Integer projectId, String authorization, File file, String path, Long fileId);

    /**
     * 根据项目ID和令牌查询项目所有词条
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     * @return 词条列表
     */
    List<StringVO> getStringsByProjectIdAndAuthorization(Integer projectId, String authorization);

    /**
     * 根据项目ID、原文和令牌查询项目指定词条
     *
     * @param projectId     项目ID
     * @param original      原文
     * @param authorization 令牌
     * @return 词条列表
     */
    List<StringVO> getStringsByProjectIdAndOriginalAndAndAuthorization(Integer projectId, String original, String authorization);

    <T extends BaseEntity, S extends IService<T>> void importStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass);

    /**
     * 从平台导入词条
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     */
    void importParatranz(Integer projectId, String authorization);

    <T extends BaseEntity, S extends IService<T>> void exportStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass);

    /**
     * 导出词条到平台
     *
     * @param projectId     项目ID
     * @param authorization 令牌
     */
    void exportParatranz(Integer projectId, String authorization);
}
