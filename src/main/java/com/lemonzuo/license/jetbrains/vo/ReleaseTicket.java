package com.lemonzuo.license.jetbrains.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * ReleaseTicket类表示一个用于释放票据的请求对象。
 * 它包含了请求动作、确认时间戳、租约签名、响应消息等属性。
 * @author LemonZuo
 * @create 2024-05-17 00:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "ReleaseTicketResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReleaseTicket implements Serializable {
    private String action;
    private String confirmationStamp;
    private String leaseSignature;
    private String message;
    private String responseCode;
    private String salt;
    private String serverLease;
    private String serverUid;
    private String validationDeadlinePeriod;
    private String validationPeriod;
}