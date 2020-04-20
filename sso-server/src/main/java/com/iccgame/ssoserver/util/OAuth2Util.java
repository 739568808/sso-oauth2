package com.iccgame.ssoserver.util;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.util.DigestUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class OAuth2Util {

    public static String getCode(String client_id,String username) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        sb.append(client_id).append(username).append(new Date().getTime());
        String encode = MD5Encoder.encode(sb.toString().getBytes());
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toUpperCase();
    }
}
