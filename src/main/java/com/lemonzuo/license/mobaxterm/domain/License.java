package com.lemonzuo.license.mobaxterm.domain;

import com.lemonzuo.license.mobaxterm.enums.LicenseEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author wanna
 * @since 2019-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class License {

    private static final String LICENSE = "%d#%s|%d%d#%d#%d3%d6%d#%d#%d#%d#";

    /**
     * 许可协议
     */
    private LicenseEnum licenseType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 大版本号
     */
    private int majorVersion;

    /**
     * 小版本号
     */
    private int minorVersion;

    /**
     * 适用 人数
     */
    private int count;

    /**
     * 未知
     */
    private int unknown = 0;

    /**
     * No Games flag. 0 means "NoGames = false".
     * But it does not work.
     */
    private boolean openGames = false;

    /**
     * No Plugins flag. 0 means "NoPlugins = false".
     * But it does not work.
     */
    private boolean openPlugins = false;

    /**
     * 生成待计算 key 的字符串
     *
     * @return licenseString
     */
    public String getLicenseKey() {
        int game = this.openGames ? 1 : 0;
        int plugin = this.openPlugins ? 1 : 0;
        return String.format(LICENSE, licenseType.getCode(), this.userName, this.majorVersion, this.minorVersion, this.count, this.majorVersion, this.minorVersion, this.minorVersion, this.unknown, game, plugin);
    }
}
