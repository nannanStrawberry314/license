package com.lemonzuo.license.jetbrains.service;

import cn.hutool.core.util.RandomUtil;
import com.lemonzuo.license.common.service.RpcService;
import com.lemonzuo.license.jetbrains.online.LicenseServerUtils;
import com.lemonzuo.license.jetbrains.online.ObtainTicketResponse;
import org.springframework.stereotype.Service;

/**
 * @author LemonZuo
 * @create 2024-05-16 23:47
 */
@Service
public class JetbrainsRpcService implements RpcService {
    @Override
    public String ping(String machineId, String salt) {
        com.crazer.mjcas.pojo.server.PingResponse response = new com.crazer.mjcas.pojo.server.PingResponse();
        response.setAction("NONE");
        response.setConfirmationStamp(LicenseServerUtils.getConfirmationStamp(machineId));
        response.setLeaseSignature(LicenseServerUtils.getLeaseSignature());
        response.setMessage("");
        response.setResponseCode("OK");
        response.setSalt(salt);
        response.setServerLease(LicenseServerUtils.leaseContent);
        response.setServerUid(LicenseServerUtils.serverUid);
        response.setValidationDeadlinePeriod("-1");
        response.setValidationPeriod("600000");

        return LicenseServerUtils.getSignXml(response);
    }

    @Override
    public String obtainTicket(String username, String hostName, String machineId, String salt) {
        ObtainTicketResponse response = new ObtainTicketResponse();
        response.setAction("NONE");
        response.setConfirmationStamp(LicenseServerUtils.getConfirmationStamp(machineId));
        response.setLeaseSignature(LicenseServerUtils.getLeaseSignature());
        response.setMessage("");
        response.setProlongationPeriod("600000");
        response.setResponseCode("OK");
        response.setSalt(salt);
        response.setServerLease(LicenseServerUtils.leaseContent);
        response.setServerUid(LicenseServerUtils.serverUid);
        response.setTicketId(RandomUtil.randomString(10));
        // Personal License
        response.setTicketProperties(String.format("licensee=%s\tlicenseeType=5\tmetadata=0120211231PSAN000005", hostName));
        // response.setTicketProperties(String.format("licensee=%s\tlicenseeType=5", hostName));// 不显示下面
        response.setValidationDeadlinePeriod("-1");
        response.setValidationPeriod("600000");
        return LicenseServerUtils.getSignXml(response);
    }

    @Override
    public String releaseTicket(String machineId, String salt) {
        com.crazer.mjcas.pojo.server.ReleaseTicketResponse response = new com.crazer.mjcas.pojo.server.ReleaseTicketResponse();
        response.setAction("NONE");
        response.setConfirmationStamp(LicenseServerUtils.getConfirmationStamp(machineId));
        response.setLeaseSignature(LicenseServerUtils.getLeaseSignature());
        response.setMessage("");
        response.setResponseCode("OK");
        response.setSalt(salt);
        response.setServerLease(LicenseServerUtils.leaseContent);
        response.setServerUid(LicenseServerUtils.serverUid);
        response.setValidationDeadlinePeriod("-1");
        response.setValidationPeriod("600000");

        return LicenseServerUtils.getSignXml(response);
    }
}
