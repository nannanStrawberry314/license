package com.lemonzuo.license.jetbrains.service;

import java.util.Date;

/**
 * @author LemonZuo
 * @create 2024-02-22 11:09
 */
public interface JetbrainsService {
    /**
     * 生成license
     * @param licenseeName 授权人
     * @param effectiveDate 有效日期
     * @return License
     */
    String generateLicense(String licenseeName, Date effectiveDate) throws Exception;
}
