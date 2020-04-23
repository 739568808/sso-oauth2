package com.iccgame.ssoserver.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class MI {

    //private static final String DATA = "com.base64.demo";

    /**
     * 加密
     * @param data
     * @return
     */
    public static String  encoder(String data){
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.getBytes());
    }

    /**
     *解密
     * @param data
     * @return
     */
    public static  String decoder(String data){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = new byte[0];
        try {
            bytes = decoder.decodeBuffer(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  new String(bytes);
    }

    public static void main( String[] args ) {
        String encoder = encoder("1234567889");
        System.out.println(encoder);
        System.out.println(decoder(encoder));
    }
}

