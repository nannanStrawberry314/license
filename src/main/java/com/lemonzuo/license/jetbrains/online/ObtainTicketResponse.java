package com.lemonzuo.license.jetbrains.online;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "ObtainTicketResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObtainTicketResponse implements Serializable {
    // 请求动作
    private String action;
    // 确认时间戳
    private String confirmationStamp;
    // 租约签名
    private String leaseSignature;
    // 响应消息
    private String message;
    // 延长期限
    private String prolongationPeriod;
    // 响应代码
    private String responseCode;
    // 盐值
    private String salt;
    // 服务器租约
    private String serverLease;
    // 服务器唯一标识
    private String serverUid;
    // 凭证ID
    private String ticketId;
    // 凭证属性
    private String ticketProperties;
    // 验证截止期限
    private String validationDeadlinePeriod;
    // 验证周期
    private String validationPeriod;
}