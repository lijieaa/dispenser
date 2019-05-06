package com.cat.dispenser;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@SpringBootApplication
@Controller
public class DbApp {

    public static void main(String[] args) {
        SpringApplication.run(DbApp.class);
    }


    @RequestMapping("test")
    @ResponseBody
    String test(){
        return "xxxxxx";
    }
}
