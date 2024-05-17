package com.lemonzuo.license.common.service;

/**
 * @author LemonZuo
 * @create 2024-05-16 23:33
 */
public interface RpcService {
    /**
     * ping
     * @param machineId 机器码
     * @param salt 盐
     * @return pong
     */
    String ping(String machineId, String salt);


    /**
     * 获取授权
     * @param username 用户名
     * @param hostName 主机名
     * @param machineId 机器码
     * @param salt 盐
     * @return 授权信息
     */
    String obtainTicket(String username, String hostName, String machineId, String salt);

    /**
     * 续期
     * @param machineId 机器码
     * @param salt 盐
     * @return 续期信息
     */
    String releaseTicket(String machineId, String salt);
}
