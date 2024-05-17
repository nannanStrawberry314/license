package com.lemonzuo.license.jetbrains.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * ObtainTicket类表示一个用于获取票据的请求对象。
 * 它包含了请求动作、确认时间戳、租约签名、响应消息等属性。
 * @author LemonZuo
 * @create 2024-05-17 00:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "ObtainTicketResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ObtainTicket implements Serializable {

    /** 请求动作 */
    private String action;

    /** 确认时间戳 */
    private String confirmationStamp;

    /** 租约签名 */
    private String leaseSignature;

    /** 响应消息 */
    private String message;

    /** 延长期限 */
    private String prolongationPeriod;

    /** 响应代码 */
    private String responseCode;

    /** 盐值 */
    private String salt;

    /** 服务器租约 */
    private String serverLease;

    /** 服务器唯一标识 */
    private String serverUid;

    /** 凭证ID */
    private String ticketId;

    /** 凭证属性 */
    private String ticketProperties;

    /** 验证截止期限 */
    private String validationDeadlinePeriod;

    /** 验证周期 */
    private String validationPeriod;
}