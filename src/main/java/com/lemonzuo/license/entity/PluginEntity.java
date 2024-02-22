package com.lemonzuo.license.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LemonZuo
 * @create 2024-02-22 9:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_jetbrains_paid_plugin")
public class PluginEntity {
    @TableId
    private Long id;
    /**
     * 插件API详情JSON
     */
    private String pluginApiDetail;
    /**
     * 插件ID
     */
    private Long pluginId;
    /**
     *
     */
    private String pluginName;
    /**
     * 插件code
     */
    private String pluginCode;
}
