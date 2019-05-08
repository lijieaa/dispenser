package com.cat.dispenser.plat.utils;

import com.cat.dispenser.DbApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
public class WxUtil {

    private static final Logger log = LoggerFactory.getLogger(WxUtil.class);
    public static void main(String[] args) {
        String jsapi_ticket = "jsapi_ticket";

        // 注意 URL 一定要动态获取，不能 hardcode
        String url = "http://example.com";
        Map<String, String> ret = sign(jsapi_ticket, url);
        for (Map.Entry entry : ret.entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
    };

    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        //System.out.println(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    public static Token getToken(String appId ,String appSecret) throws IOException {
        String accessTokenPath = "./access_token.json";
        File accessTokenFile = new File(accessTokenPath);
        WxUtil.Token token = null;
        ObjectMapper objectMapper=new ObjectMapper();
        token = objectMapper.readValue(accessTokenFile, WxUtil.Token.class);
        Calendar nowTime = Calendar.getInstance();
        if (token.getExpires_in() < nowTime.getTime().getTime()) {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appId&secret=$appSecret";
            token = restTemplate.getForObject(tokenUrl.replace("$appId", appId).replace("$appSecret", appSecret), Token.class);
            nowTime.add(Calendar.SECOND, 7000);
            token.setExpires_in(nowTime.getTime().getTime());
            log.info("重新获取 token:{}", token);
            objectMapper.writeValue(accessTokenFile, token); // 写到文件中
        } else {
            log.info("从文件缓存中获取token:{}", token);
        }
        return token;
    };

    public static Ticket getTicket(String accessToken) throws IOException {
        String jsapiTicketPath = "./jsapi_ticket.json";
        File jsapiTicketFile = new File(jsapiTicketPath);
        WxUtil.Ticket ticket = null;
        ObjectMapper objectMapper=new ObjectMapper();
        ticket = objectMapper.readValue(jsapiTicketFile, WxUtil.Ticket.class);
        Calendar nowTime = Calendar.getInstance();
        if (ticket.getExpires_in() < nowTime.getTime().getTime()) {
            RestTemplate restTemplate = new RestTemplate();
            String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=$accessToken";
            ticket = restTemplate.getForObject(ticketUrl.replace("$accessToken", accessToken), Ticket.class);
            nowTime.add(Calendar.SECOND, 7000);
            ticket.setExpires_in(nowTime.getTime().getTime());
            log.info("重新获取 ticket:{}", ticket);
            objectMapper.writeValue(jsapiTicketFile, ticket); // 写到文件中
        }else {
            log.info("从文件缓存中获取ticket:{}", ticket);
        }

        return ticket;
    };


    public static class Token{
        private String access_token;
        private long expires_in;

        public Token() {
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public long getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(long expires_in) {
            this.expires_in = expires_in;
        }


        @Override
        public String toString() {
            return "Token{" +
                    "access_token='" + access_token + '\'' +
                    ", expires_in=" + expires_in +
                    '}';
        }
    }


    public static class Ticket{
        public String getTicket() {
            return ticket;
        }

        @Override
        public String toString() {
            return "Ticket{" +
                    "ticket='" + ticket + '\'' +
                    ", expires_in=" + expires_in +
                    '}';
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public long getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(long expires_in) {
            this.expires_in = expires_in;
        }

        private String ticket;
        private long expires_in;
    }
}
