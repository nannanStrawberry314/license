package com.lemonzuo.license.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemonzuo.license.entity.ProductEntity;
import org.apache.ibatis.annotations.Update;

/**
 * @author LemonZuo
 * @create 2024-02-22 10:44
 */
public interface ProductMapper extends BaseMapper<ProductEntity> {
    /**
     * 清空表
     */
    @Update("DELETE FROM sys_jetbrains_product")
    void truncate();
}
