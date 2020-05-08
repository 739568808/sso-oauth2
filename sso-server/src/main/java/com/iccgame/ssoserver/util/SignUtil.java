package com.iccgame.ssoserver.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 在每次请求接口时需根据参数、apikey、token临时构建出signature签名，最大程度保障接口安全性，构建规则如下；
 * 对所有参数(除signature外)按照字段名的ASCII 码从小到大排序(字典排序)后，使用URL键值对的格式（即key1=value1&key2=value2… + token=xxx）拼接成字符串，最后再进行md5加密。字段名和字段值都采用原始值，请勿行URL转义。
 * 举例说明:
 * 接口A请求参数为： name、age、address
 */
public class SignUtil {

    public static void main(String[] args) throws UnsupportedEncodingException {
//        //sign();
//        SortedMap<String, String> params = new TreeMap<String, String>();
//        params.put("client_id", "123456789");
//        params.put("response_type", "code");
//        params.put("redirect_uri", "");
//        params.put("code", "code");
//        params.put("session_type", "JSESSIONID");
//        params.put("sessionid", "sessionid");
//        params.put("log_out_url", "/logOut");
//        params.put("client_secret","123456789123456789");
//        String sign = sign(params);
//        System.out.println(md5("123456"));
        System.out.println(URLEncoder.encode("http://192.168.0.141:8080/#/view","UTF-8"));
    }

    /**
     * @param input 输入
     * @return 返回16个字节
     * @throws Exception
     */

    public static byte[] originMD5(byte[] input) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] out = md5.digest(input);
        return out;
    }

    /**
     * @param input 输入
     * @return 返回16个字节
     * @throws Exception
     */
    public static String MD5(byte[] input) throws Exception {
        String str = new String(input, 0, input.length);
        //创建MD5加密对象
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 进行加密
        md5.update(str.getBytes());
        //获取加密后的字节数组
        byte[] md5Bytes = md5.digest();
        String res = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            int temp = md5Bytes[i] & 0xFF;
            // 转化成十六进制不够两位，前面加零
            if (temp <= 0XF) {
                res += "0";
            }
            res += Integer.toHexString(temp);
        }
        String strMd5Key = new String(res.getBytes(), 0, res.getBytes().length);
        return strMd5Key;
    }



    public static String md5(String str) {
        try {
            return MD5(str.getBytes());
            //加密后的字符串
            //return DigestUtils.md5Hex(str);
        }catch (Exception E){
            return null;
        }

    }

    public static String sign(SortedMap<String,String> map) {


            StringBuffer sb = new StringBuffer();
            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String k = (String) entry.getKey();
                Object v = entry.getValue();
                //空值不传递，不参与签名组串
                if (null != v && !"".equals(v)) {
                    sb.append(k + "=" + v + "&");
                }
            }

            String param = sb.toString().substring(0, sb.toString().length() - 1);
            //编码
            System.out.println("加密前：" + param);
            System.out.println("加密后：" + md5(param));
            return md5(param);


    }
}