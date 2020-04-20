package com.iccgame.ssoserver.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;

public class JwtUtil {


    /**
     * 过期时间为一天
     * TODO 正式上线更换为15分钟
     */
    private static final long EXPIRE_TIME = 15*60*1000;
    //private static final long EXPIRE_TIME = 24*60*60*1000;


    /**
     * 生成签名,15分钟后过期
     * @param username
     * @return
     */
    public static String sign(String client_id,String client_secret){
        //过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //私钥及加密算法
        Algorithm algorithm = Algorithm.HMAC256(client_secret);
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        //生成签名
        return JWT.create().withHeader(header)
                .withClaim("client_id",client_id)
                .withClaim("client_secret",client_secret)
                .withExpiresAt(date).sign(algorithm);
    }


    public static boolean verity(String token,String client_secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(client_secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (JWTVerificationException e) {
            return false;
        }

    }

    public static void main(String[] args) {
        //System.out.println(sign("1111","222"));
        //System.out.println(verity("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbk5hbWUiOiIxMTExIiwiZXhwIjoxNTg3NDYwOTk4LCJ1c2VySWQiOiIyMjIifQ.yUEgXF4Fdsc1JdxbwM7A76Crgmr2E0PMzheTSkmktHU"));
    }
}

