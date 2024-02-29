package com.lemonzuo.license.mobaxterm.generator;

/**
 * @author wanna
 * @since 2019-01-03
 */
public class License {

    private static final String LICENSE = "%d#%s|%d%d#%d#%d3%d6%d#%d#%d#%d#";

    /**
     * 许可协议
     */
    private LicenseType licenseType = LicenseType.Professional;

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
    private int count = 1;

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

    public License(String userName, int majorVersion, int minorVersion) {
        this.userName = userName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public License(String userName, String version) {
        this(userName, Integer.valueOf(version.split("\\.")[0]), Integer.valueOf(version.split("\\.")[1]));
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public boolean isOpenGames() {
        return openGames;
    }

    public void setOpenGames(boolean openGames) {
        this.openGames = openGames;
    }

    public boolean isOpenPlugins() {
        return openPlugins;
    }

    public void setOpenPlugins(boolean openPlugins) {
        this.openPlugins = openPlugins;
    }

    /**
     * 生成待计算 key 的字符串
     *
     * @return licenseString
     */
    public String getLicenseKey() {
        int game = this.openGames ? 1 : 0;
        int plugin = this.openPlugins ? 1 : 0;
        return String.format(LICENSE, licenseType.getCode(),
                this.userName, this.majorVersion, this.minorVersion, this.count,
                this.majorVersion, this.minorVersion, this.minorVersion,
                this.unknown, game, plugin);
    }
}
