package com.lemonzuo.license.jrebel.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.lemonzuo.license.jrebel.constant.ServerConstant;
import com.lemonzuo.license.jrebel.vo.JrebelLeasesHandlerVO;
import com.lemonzuo.license.jrebel.vo.JrebelLeasesOneHandlerVO;
import com.lemonzuo.license.jrebel.vo.JrebelValidateHandlerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @author ZKQ
 * @date 2022-12-09 16:12
 */
@Slf4j
@RestController
@RequestMapping(value = {"/jrebel", "/agent"})
public class LeasesController {
    private static final String PRIVATE_KEY_BASE64 = """
            MIICXAIBAAKBgQDQ93CP6SjEneDizCF1P/MaBGf582voNNFcu8oMhgdTZ/N6qa6O7XJDr1FSCyaDdKSsPCdxPK7Y4Usq/fOPas2kCgYcRS/iebrtPEFZ/7TLfk39H
            LuTEjzo0/CNvjVsgWeh9BYznFaxFDLx7fLKqCQ6w1OKScnsdqwjpaXwXqiulwIDAQABAoGATOQvvBSMVsTNQkbgrNcqKdGjPNrwQtJkk13aO/95ZJxkgCc9vwPqPr
            OdFbZappZeHa5IyScOI2nLEfe+DnC7V80K2dBtaIQjOeZQt5HoTRG4EHQaWoDh27BWuJoip5WMrOd+1qfkOtZoRjNcHl86LIAh/+3vxYyebkug4UHNGPkCQQD+N4Z
            UkhKNQW7mpxX6eecitmOdN7Yt0YH9UmxPiW1LyCEbLwduMR2tfyGfrbZALiGzlKJize38shGC1qYSMvZFAkEA0m6psWWiTUWtaOKMxkTkcUdigalZ9xFSEl6jXFB9
            4AD+dlPS3J5gNzTEmbPLc14VIWJFkO+UOrpl77w5uF2dKwJAaMpslhnsicvKMkv31FtBut5iK6GWeEafhdPfD94/bnidpP362yJl8Gmya4cI1GXvwH3pfj8S9hJVA
            5EFvgTB3QJBAJP1O1uAGp46X7Nfl5vQ1M7RYnHIoXkWtJ417Kb78YWPLVwFlD2LHhuy/okT4fk8LZ9LeZ5u1cp1RTdLIUqAiAECQC46OwOm87L35yaVfpUIjqg/1g
            sNwNsj8HvtXdF/9d30JIM3GwdytCvNRLqP35Ciogb9AO8ke8L6zY83nxPbClM=
            """;

    private String sign(String clientRandomness, String guid, boolean offline, Long validFrom, Long validUntil) {
        String serverRandomness = ServerConstant.SERVER_RANDOMNESS;
        String signature;
        if (offline) {
            signature = StrUtil.join(";", clientRandomness, serverRandomness, guid, true, validFrom, validUntil);
        } else {
            signature = StrUtil.join(";", clientRandomness, serverRandomness, guid, false);
        }
        log.info("signature: {}", signature);

        Sign sign = SecureUtil.sign(SignAlgorithm.SHA1withRSA, PRIVATE_KEY_BASE64, null);
        // 直接使用Sign对象进行数据签名
        return Base64.encode(sign.sign(signature.getBytes()));
    }

    @RequestMapping(value = {"/leases"})
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
        vo.setServerVersion(ServerConstant.SERVER_VERSION)
                .setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID)
                .setSignature(ServerConstant.JREBEL_LEASES_HANDLER_SIGNATURE)
                .setServerRandomness(ServerConstant.SERVER_RANDOMNESS)
                .setGroupType(ServerConstant.GROUP_TYPE)
                .setId(1)
                .setLicenseType(1)
                .setEvaluationLicense(false)
                .setSeatPoolType(ServerConstant.SEAT_POOL_TYPE)
                .setStatusCode(ServerConstant.STATUS_CODE)
                .setCompany(username)
                .setLicenseValidFrom(1490544001000L)
                .setLicenseValidUntil(1691839999000L)
                .setOffline(offline)
                .setValidFrom(validFrom)
                .setValidUntil(validUntil)
                .setOrderId(IdUtil.getSnowflakeNextIdStr())
                .setZeroIds(Collections.emptyList());

        String signature = sign(clientRandomness, guid, offline, validFrom, validUntil);

        vo.setSignature(signature);
        return vo;
    }

    @RequestMapping(value = {"/leases/1"})
    public JrebelLeasesOneHandlerVO jrebelLeases1Handler(@RequestParam(value = "username", required = false) String username) {
        JrebelLeasesOneHandlerVO vo = new JrebelLeasesOneHandlerVO();
        vo.setServerVersion(ServerConstant.SERVER_VERSION)
                .setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID)
                .setGroupType(ServerConstant.GROUP_TYPE)
                .setStatusCode(ServerConstant.STATUS_CODE)
                .setCompany(username)
                .setMsg(StrUtil.NULL)
                .setStatusMessage(StrUtil.NULL);
        return vo;
    }

    @RequestMapping(value = {"/validate-connection"})
    public JrebelValidateHandlerVO jrebelValidateHandler() {
        JrebelValidateHandlerVO vo = new JrebelValidateHandlerVO();
        vo.setServerVersion(ServerConstant.SERVER_VERSION)
                .setServerProtocolVersion(ServerConstant.SERVER_PROTOCOL_VERSION)
                .setServerGuid(ServerConstant.SERVER_GUID)
                .setGroupType(ServerConstant.GROUP_TYPE)
                .setStatusCode(ServerConstant.STATUS_CODE)
                .setCompany(ServerConstant.COMPANY)
                .setCanGetLease(true)
                .setLicenseType(1)
                .setEvaluationLicense(false)
                .setSeatPoolType(ServerConstant.SEAT_POOL_TYPE);
        return vo;
    }
}
