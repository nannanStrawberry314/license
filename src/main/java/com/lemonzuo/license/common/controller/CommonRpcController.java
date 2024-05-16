package com.lemonzuo.license.common.controller;

import cn.hutool.core.util.StrUtil;
import com.lemonzuo.license.common.service.RpcService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2024-05-16 23:31
 */
@RestController
@RequestMapping("/rpc")
public class CommonRpcController {
    @Resource
    private RpcService jrebelRpcService;
    @Resource
    private RpcService jetbrainsRpcService;

    /**
     * ping
     * @param machineId 机器码
     * @param salt 盐
     * @return pong
     */
    @RequestMapping("/ping.action")
    public String ping(@RequestParam(required = false) String machineId,
                       @RequestParam String salt) {
        boolean isJetbrains = StrUtil.isNotEmpty(machineId);
        if (isJetbrains) {
            return jetbrainsRpcService.ping(machineId, salt);
        } else {
            return jrebelRpcService.ping(machineId, salt);
        }
    }


    /**
     * 获取授权
     * @param username 用户名
     * @param hostName 主机名
     * @param machineId 机器码
     * @param salt 盐
     * @return 授权信息
     */
    @RequestMapping("/obtainTicket.action")
    public String obtainTicket(@RequestParam(required = false) String username,
                               @RequestParam(required = false) String hostName,
                               @RequestParam(required = false) String machineId,
                               @RequestParam String salt) {
        boolean isJetbrains = StrUtil.isNotEmpty(machineId);
        if (isJetbrains) {
            return jetbrainsRpcService.obtainTicket(username, hostName, machineId, salt);
        } else {
            return jrebelRpcService.obtainTicket(username, hostName, machineId, salt);
        }
    }

    /**
     * 续期
     * @param machineId 机器码
     * @param salt 盐
     * @return 续期信息
     */
    @RequestMapping("/releaseTicket.action")
    public String releaseTicket(@RequestParam(required = false) String machineId,
                                @RequestParam String salt) {
        boolean isJetbrains = StrUtil.isNotEmpty(machineId);
        if (isJetbrains) {
            return jetbrainsRpcService.releaseTicket(machineId, salt);
        } else {
            return jrebelRpcService.releaseTicket(machineId, salt);
        }

    }

}
