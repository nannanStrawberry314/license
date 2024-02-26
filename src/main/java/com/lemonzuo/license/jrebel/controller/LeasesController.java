package com.lemonzuo.license.jrebel.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lemonzuo.constant.ServerConstant;
import com.lemonzuo.util.JrebelSign;
import com.lemonzuo.vo.JrebelLeasesHandlerVO;
import com.lemonzuo.vo.JrebelLeasesOneHandlerVO;
import com.lemonzuo.vo.JrebelValidateHandlerVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author ZKQ
 * @date 2022-12-09 16:12
 */
@RestController
public class LeasesController {
    @RequestMapping(value = {"/jrebel/leases", "/agent/leases"})
    public JrebelLeasesHandlerVO jrebelLeasesHandler(
            @RequestParam(value = "randomness") String clientRandomness,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "guid") String guid,
            @RequestParam(value = "offline", required = false) boolean offline,
            @RequestParam(value = "clientTime", required = false) Long clientTime) {
        Long validFrom = null;
        Long validUntil = null;
        if (offline) {
            long clientTimeUntil = clientTime + 180L * 24 * 60 * 60 * 1000;
            validFrom = clientTime;
            validUntil = clientTimeUntil;
        }

        JrebelLeasesHandlerVO vo = new JrebelLeasesHandlerVO();
        vo.setServerVersion(ServerConstant.SERVER_VERSION).setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID).setSignature(ServerConstant.JREBEL_LEASES_HANDLER_SIGNATURE)
                .setServerRandomness(ServerConstant.SERVER_RANDOMNESS)
                .setGroupType(ServerConstant.GROUP_TYPE).setId(1)
                .setLicenseType(1).setEvaluationLicense(false).setSeatPoolType(ServerConstant.SEAT_POOL_TYPE).setStatusCode(ServerConstant.STATUS_CODE)
                .setCompany(username).setLicenseValidFrom(1490544001000L).setLicenseValidUntil(1691839999000L)
                .setOffline(offline).setValidFrom(validFrom).setValidUntil(validUntil)
                .setOrderId(IdUtil.getSnowflakeNextIdStr()).setZeroIds(Collections.emptyList());

        JrebelSign jrebelSign = new JrebelSign();
        jrebelSign.toLeaseCreateJson(clientRandomness, guid, offline, validFrom, validUntil);
        String signature = jrebelSign.getSignature();

        vo.setSignature(signature);
        return vo;
    }

    @RequestMapping(value = {"/jrebel/leases/1", "/agent/leases/1"})
    public JrebelLeasesOneHandlerVO jrebelLeases1Handler(@RequestParam(value = "username", required = false) String username) {
        JrebelLeasesOneHandlerVO vo = new JrebelLeasesOneHandlerVO();
        vo.setServerVersion(ServerConstant.SERVER_VERSION)
                .setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID)
                .setGroupType(ServerConstant.GROUP_TYPE).setStatusCode(ServerConstant.STATUS_CODE).setCompany(username)
                .setMsg(StrUtil.NULL).setStatusMessage(StrUtil.NULL);
        return vo;
    }

    @RequestMapping(value = {"/jrebel/validate-connection"})
    public JrebelValidateHandlerVO jrebelValidateHandler() {
        JrebelValidateHandlerVO vo = new JrebelValidateHandlerVO();
        vo.setServerVersion(ServerConstant.SERVER_VERSION)
                .setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID)
                .setGroupType(ServerConstant.GROUP_TYPE).setStatusCode(ServerConstant.STATUS_CODE).setCompany(ServerConstant.COMPANY)
                .setCanGetLease(true).setLicenseType(1).setEvaluationLicense(false).setSeatPoolType(ServerConstant.SEAT_POOL_TYPE);
        return vo;
    }
}
