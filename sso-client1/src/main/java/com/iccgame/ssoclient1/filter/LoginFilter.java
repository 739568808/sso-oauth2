package com.iccgame.ssoclient1.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iccgame.ssoclient1.util.IpUtil;
import com.iccgame.ssoclient1.util.SignUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@WebFilter(urlPatterns="/**",filterName="loginFilter")
public class LoginFilter implements Filter{

    @Value("${sso.url.prefix}")
    private String SSO_URL_PREFIX;

    @Value("${client.host.url}")
    private String CLIENT_HOST_URL;

    //排除不拦截的url
    private static final String[] excludePathPatterns = { "/logOut","/login","/favicon.ico"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        // 获取请求url地址，不拦截excludePathPatterns中的url
        String url = req.getRequestURI();
        if (Arrays.asList(excludePathPatterns).contains(url)) {
            //放行，相当于第一种方法中LoginInterceptor返回值为true
            reqLogOut(req);
            chain.doFilter(request, response);
            return;
        }
        System.out.println("开始拦截了................");
        //1、判断请求地址是否存在
            //TODO 不存在自行挑战
        //1、判断是否有局部的会话
        String ip = IpUtil.getIpAddress(req);
        System.out.println("ip:"+ip);
        HttpSession session = req.getSession();
        Boolean isLogin = (Boolean)session.getAttribute("isLogin");
        if (null!=isLogin && isLogin){
            reqLogOut(req);
            chain.doFilter(request, response);
            return;
        }
        //2、判断地址栏中是否有携带token参数。
        String code = req.getParameter("code");
        if (!StringUtils.isEmpty(code)){
            //token地址不为空 说明地址栏中包含了token，拥有令牌
            //判断token是否有认证中心生成的

            SortedMap<String, String> params = new TreeMap<String, String>();
            params.put("client_id", "123456789");
            params.put("response_type", "code");
            params.put("redirect_uri", CLIENT_HOST_URL);
            params.put("code", code);
            params.put("session_type", "JSESSIONID");
            params.put("session_id", session.getId());
            params.put("log_out_url", CLIENT_HOST_URL+"/logOut");
            params.put("client_secret","123456789123456789");
            params.put("ip",ip);
            String sign = SignUtil.sign(params);


            Connection.Response  resp = Jsoup.connect(SSO_URL_PREFIX + "/oauth/token")
                    .data("client_id", "123456789")
                    .data("sign", sign)
                    .data("response_type", "code")
                    .data("redirect_uri", CLIENT_HOST_URL)
                    .data("code", code)
                    .data("log_out_url",CLIENT_HOST_URL+"/logOut")
                    .data("session_id",session.getId())
                    .data("session_type","JSESSIONID")
                    .data("ip",ip)
                    .method(Connection.Method.POST).execute();
            String result = resp.body();
            JSONObject object = JSONObject.fromObject(result);
            if (object.getInt("code")==200){
                //说明token是有统一认证中心产生的，可以创建局部的会话
                session.setAttribute("isLogin",true);
                JSONObject info = object.getJSONObject("info");
                //放行该次请求
                reqLogOut(req);
                chain.doFilter(request, response);
                return;
            }

        }
        //2、没有局部会话，重定向到统一认证中心，检查是否其他系统已经登录过
        //http://localhost:8080/checkLogin?redirectUrl=http://localhost:8081/
        redirectToSSO(req,res);
        //request.getRequestDispatcher("/login").forward(request,response);
        //res.sendRedirect("/login");
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    private void redirectToSSO(HttpServletRequest req,HttpServletResponse res){
        try {
            String redirectUrl = CLIENT_HOST_URL+req.getServletPath();
            StringBuilder url = new StringBuilder();
            url.append(SSO_URL_PREFIX)
                    .append("/checkLogin?success_redirectUrl=").append(redirectUrl)
                    .append("&fail_redirectUrl=").append(redirectUrl+"login");
            res.sendRedirect(url.toString());
        }catch (Exception e){
            System.out.println("跳转失败");
            e.printStackTrace();
        }

    }

    private void reqLogOut(HttpServletRequest req){
        req.setAttribute("serverLogOutUrl",SSO_URL_PREFIX+"/logOut");
    }
}

