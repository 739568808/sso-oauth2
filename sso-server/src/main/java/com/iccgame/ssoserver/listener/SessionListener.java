package com.iccgame.ssoserver.listener;

import com.alibaba.fastjson.JSON;
import com.iccgame.ssoserver.enums.ECODE;
import com.iccgame.ssoserver.util.RedisUtils;
import com.iccgame.ssoserver.vo.ClientInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.util.List;
@Component
@Slf4j
public class SessionListener implements HttpSessionListener {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("触发了session销毁事件......");
        HttpSession session = se.getSession();
        String code = (String)session.getAttribute("code");
        //删除t_token表中的数据
        //String tokenKey = redisUtils.getSSOKey(ECODE.CODE.getName(), code);
        String tokenClientInfoKey = redisUtils.getSSOKey(ECODE.CODE_CLIENT_INFO.getName(), code);

        String tokenClientInfoStr = redisUtils.get(tokenClientInfoKey);
        List<ClientInfoVo> clientInfoList = JSON.parseArray(tokenClientInfoStr, ClientInfoVo.class);
        if (!CollectionUtils.isEmpty(clientInfoList)){
            //获取出注册的子系统，依次调用子系统的登出方法
            for (ClientInfoVo vo:clientInfoList){
                try {
                    log.info("销毁session:{}", JSON.toJSON(vo));
                    sendHttpRequset(vo.getLogOutUrl(),vo.getSessionid(),vo.getSessionType());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
       // redisUtils.del(tokenKey);
        redisUtils.del(tokenClientInfoKey);
    }

    private void sendHttpRequset(String url,String sessionid,String sessionType) {
        try {
            Connection.Response response = Jsoup.connect(url)
                    //.header("Cookie", "JSESSIONID=" + jsessionid)
                    .header("Cookie", sessionType+"=" + sessionid)
                    .ignoreContentType(true)
                    .method(Connection.Method.GET).execute();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
