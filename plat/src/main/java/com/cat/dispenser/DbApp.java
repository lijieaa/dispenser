package com.cat.dispenser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@SpringBootApplication
@Controller
@RequestMapping("f")
public class DbApp {

    private static final Logger log = LoggerFactory.getLogger(DbApp.class);

    public static void main(String[] args) {
        SpringApplication.run(DbApp.class);
    }


    @RequestMapping(value = "wx/access_url",method = RequestMethod.GET)
    @ResponseBody
    String wxAccessUrl(String signature,String timestamp,String nonce,String echostr){
        log.info("signature:{},timestamp:{},nonce:{},echostr:{}",signature,timestamp,nonce,echostr);
        return echostr;
    }


    @RequestMapping(value = "goods",method = RequestMethod.GET)
    String goods(Model model){

        return "f/goods";
    }



    @RequestMapping(value = "pay",method = RequestMethod.GET)
    String pay(HttpServletRequest request,Model model){
        String userAgent = request.getHeader("user-agent");
        if (userAgent != null && userAgent.contains("AlipayClient")) {
            model.addAttribute("flag",true);
            return "/f/pay";
        }else if (userAgent != null && userAgent.contains("MicroMessenger")) {
            model.addAttribute("flag",false);
            return "/f/pay";
        }else{
            return "/f/pay_error";
        }
    }


}
