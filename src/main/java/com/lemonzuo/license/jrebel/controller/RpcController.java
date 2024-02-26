package com.lemonzuo.license.jrebel.controller;

import com.lemonzuo.util.RsaSign;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LemonZuo
 * @create 2022-12-10 21:58
 */
@RestController
public class RpcController {

    @RequestMapping(value = {"/rpc/ping.action"})
    public String pingHandler(@RequestParam(name = "salt") String salt) {
        String xmlContent = "<PingResponse><message></message><responseCode>OK</responseCode><salt>%s</salt></PingResponse>";
        xmlContent = String.format(xmlContent, salt);
        String xmlSignature = RsaSign.Sign(xmlContent);
        return String.format("<!-- %s -->\n%s", xmlSignature, xmlContent);
    }

    @RequestMapping(value = {"/rpc/obtainTicket.action"})
    public String obtainTicketHandler(@RequestParam(name = "salt") String salt,
                                       @RequestParam(name = "username") String username) {
        String prolongationPeriod = "607875500";
        String xmlContent = "<ObtainTicketResponse><message></message><prolongationPeriod>" + prolongationPeriod + "</prolongationPeriod><responseCode>OK</responseCode><salt>" + salt + "</salt><ticketId>1</ticketId><ticketProperties>licensee=" + username + "\tlicenseType=0\t</ticketProperties></ObtainTicketResponse>";
        String xmlSignature = RsaSign.Sign(xmlContent);
        return "<!-- " + xmlSignature + " -->\n" + xmlContent;
    }

    @RequestMapping(value = {"/rpc/releaseTicket.action"})
    public String releaseTicketHandler(@RequestParam(name = "salt") String salt) {
        String xmlContent = "<ReleaseTicketResponse><message></message><responseCode>OK</responseCode><salt>" + salt + "</salt></ReleaseTicketResponse>";
        String xmlSignature = RsaSign.Sign(xmlContent);
        return "<!-- " + xmlSignature + " -->\n" + xmlContent;
    }
}
