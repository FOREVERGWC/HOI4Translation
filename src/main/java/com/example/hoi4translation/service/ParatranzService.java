package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.hoi4translation.domain.entity.BaseEntity;
import com.example.hoi4translation.domain.vo.FileVO;
import com.example.hoi4translation.domain.vo.StringVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParatranzService {
    List<FileVO> getFilesByProjectIdAndAuthorization(Integer projectId, String authorization);

    List<StringVO> getStringsByProjectIdAndAuthorization(Integer projectId, String authorization);

    <T extends BaseEntity, S extends IService<T>> void importStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass);

    void importParatranz(Integer projectId, String authorization);

    <T extends BaseEntity, S extends IService<T>> void exportStrings(List<FileVO> files, String authorization, Class<T> clazz, Class<S> sClass);

    void exportParatranz(Integer projectId, String authorization);
}
