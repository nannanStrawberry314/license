package com.lemonzuo.license.jetbrains.controller;

import com.lemonzuo.license.jetbrains.util.LicenseServerUtils;
import com.lemonzuo.license.jetbrains.vo.ProlongTicket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * LicenseServerController
 * 授权服务器控制器
 * @author LemonZuo
 * @create 2024-05-16 23:33
 */
@Slf4j
@RestController
@RequestMapping(value = "/rpc")
public class ServerRpcController {

    /**
     * prolongTicket 授权续期
     * @param salt 盐
     * @return pong 续期信息
     */
    @RequestMapping("/prolongTicket.action")
    public Object prolongTicket(@RequestParam String machineId,
                                @RequestParam String salt) {

        ProlongTicket response = new ProlongTicket();
        response.setAction("NONE");
        response.setConfirmationStamp(LicenseServerUtils.getConfirmationStamp(machineId));
        response.setLeaseSignature(LicenseServerUtils.getLeaseSignature());
        response.setMessage("");
        response.setResponseCode("OK");
        response.setSalt(salt);
        response.setServerLease(LicenseServerUtils.LEASE_CONTENT);
        response.setServerUid(LicenseServerUtils.SERVER_UID);
        response.setValidationDeadlinePeriod("-1");
        response.setValidationPeriod("600000");

        return LicenseServerUtils.getSignXml(response);
    }
}