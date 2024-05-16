package com.crazer.mjcas.pojo.server;

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
@XmlRootElement(name = "ProlongTicketResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProlongTicketResponse implements Serializable {
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