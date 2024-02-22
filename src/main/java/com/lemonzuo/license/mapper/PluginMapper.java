package com.lemonzuo.license.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemonzuo.license.entity.PluginEntity;
import org.apache.ibatis.annotations.Update;

/**
 * @author LemonZuo
 * @create 2024-02-22 9:32
 */
public interface PluginMapper extends BaseMapper<PluginEntity> {
    /**
     * 清空表
     */
    @Update("DELETE FROM sys_jetbrains_paid_plugin")
    void truncate();
}
