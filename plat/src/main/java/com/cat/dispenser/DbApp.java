package com.cat.dispenser;


import com.cat.dispenser.plat.utils.WxUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


@SpringBootApplication
@Controller
@RequestMapping("f")
public class DbApp {

    private static final Logger log = LoggerFactory.getLogger(DbApp.class);

    public static void main(String[] args) {
        SpringApplication.run(DbApp.class);
    }


    @RequestMapping(value = "wx/access_url", method = RequestMethod.GET)
    @ResponseBody
    String wxAccessUrl(String signature, String timestamp, String nonce, String echostr) {
        log.info("signature:{},timestamp:{},nonce:{},echostr:{}", signature, timestamp, nonce, echostr);
        return echostr;
    }


    @RequestMapping(value = "goods", method = RequestMethod.GET)
    String goods(Model model) {

        return "f/goods";
    }

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "pay", method = RequestMethod.GET)
    String pay(HttpServletRequest request, Model model) {


        String userAgent = request.getHeader("user-agent");
        if (userAgent != null && userAgent.contains("AlipayClient")) {
            model.addAttribute("flag", true);
            return "/f/pay";
        } else if (userAgent != null && userAgent.contains("MicroMessenger")) {



            model.addAttribute("flag", false);
            //model.addAttribute("sign", sign);


            return "/f/pay";
        } else {
            return "/f/pay_error";
        }
    }



    @RequestMapping(value = "ticket", method = RequestMethod.GET)
    @ResponseBody
    Map<String, String> getTicket(HttpServletRequest request) {
        String appId = "wx02d7d72eafde98d8";
        String appSecret = "36344d4d30eaf5bbd63128a160c6d0c9";


        String url = request.getParameter("targetUrl");

        WxUtil.Token token = null;
        WxUtil.Ticket ticket=null;
        try {
            token = WxUtil.getToken(appId, appSecret);
            ticket = WxUtil.getTicket(token.getAccess_token());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<String, String> sign = null;
        try {
            sign = WxUtil.sign(ticket.getTicket(), URLDecoder.decode(url,"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sign.put("appId", appId);
        log.info("sign:{}", sign);

        return sign;
    }


}
