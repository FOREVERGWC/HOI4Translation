package com.example.hoi4translation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;

import java.util.List;

/**
 * <p>
 * 基础服务类
 * </p>
 */
public interface IBaseService<T> {
    /**
     * 深度分页
     *
     * @param entity 实体类
     * @param page   分页参数
     * @return 结果
     */
    List<T> getPageList(T entity, IPage<T> page);

    /**
     * 组装查询包装器
     *
     * @param entity 实体类
     * @return 结果
     */
    LambdaQueryChainWrapper<T> getWrapper(T entity);
}
