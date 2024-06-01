package com.lemonzuo.license.jetbrains.service;

import cn.hutool.core.util.RandomUtil;
import com.lemonzuo.license.common.service.RpcService;
import com.lemonzuo.license.jetbrains.util.LicenseServerUtils;
import com.lemonzuo.license.jetbrains.vo.ObtainTicket;
import com.lemonzuo.license.jetbrains.vo.Ping;
import com.lemonzuo.license.jetbrains.vo.ReleaseTicket;
import org.springframework.stereotype.Service;

/**
 * @author LemonZuo
 * @create 2024-05-16 23:47
 */
@Service
public class JetbrainsRpcService implements RpcService {
    @Override
    public String ping(String machineId, String salt) {
        Ping response = new Ping();
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

    @Override
    public String obtainTicket(String username, String hostName, String machineId, String salt) {
        ObtainTicket response = new ObtainTicket();
        response.setAction("NONE");
        response.setConfirmationStamp(LicenseServerUtils.getConfirmationStamp(machineId));
        response.setLeaseSignature(LicenseServerUtils.getLeaseSignature());
        response.setMessage("");
        response.setProlongationPeriod("600000");
        response.setResponseCode("OK");
        response.setSalt(salt);
        response.setServerLease(LicenseServerUtils.LEASE_CONTENT);
        response.setServerUid(LicenseServerUtils.SERVER_UID);
        response.setTicketId(RandomUtil.randomString(10));
        // Personal License
        StringBuilder builder = new StringBuilder();
        builder.append("licensee=").append(hostName).append("\t")
                .append("licenseeType=PERSONAL\t")
                .append("assigneeName=\t")
                .append("metadata=0120231110PSAA003008").append("\t")
                .append("hash=51149839/0:-1370131430").append("\t")
                .append("gracePeriodDays=7\t")
                .append("autoProlongated=true\t")
                .append("isAutoProlongated=true\t")
                .append("trial=false");

        // response.setTicketProperties(String.format("licensee=%s\tlicenseeType=5\tmetadata=0120211231PSAN000005", hostName));
        response.setTicketProperties(builder.toString());
        // 不显示下面
        // response.setTicketProperties(String.format("licensee=%s\tlicenseeType=5", hostName));
        response.setValidationDeadlinePeriod("-1");
        response.setValidationPeriod("600000");
        return LicenseServerUtils.getSignXml(response);
    }

    @Override
    public String releaseTicket(String machineId, String salt) {
        ReleaseTicket response = new ReleaseTicket();
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
