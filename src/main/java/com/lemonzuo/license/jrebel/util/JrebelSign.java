package com.lemonzuo.license.jrebel.util;

import com.lemonzuo.constant.ServerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
/**
 * @author LemonZuo
 */
@Slf4j
public class JrebelSign {
    private String signature;

    public void toLeaseCreateJson(String clientRandomness, String guid, boolean offline, Long validFrom, Long validUntil) {
        String serverRandomness = ServerConstant.SERVER_RANDOMNESS;
        String s2= "";
        if(offline){
            s2 = StringUtils.join(new String[]{clientRandomness, serverRandomness, guid, String.valueOf(offline), String.valueOf(validFrom), String.valueOf(validUntil)}, ';');
        }else{
            s2 = StringUtils.join(new String[]{clientRandomness, serverRandomness, guid, String.valueOf(offline)}, ';');
        }
        log.info(s2);
        final byte[] a2 =LicenseServer2ToJRebelPrivateKey.a(s2.getBytes());
        this.signature = ByteUtil.a(a2);
    }

    public String getSignature() {
        return signature;
    }

}
