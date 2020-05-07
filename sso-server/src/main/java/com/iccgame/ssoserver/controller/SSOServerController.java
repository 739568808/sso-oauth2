package com.iccgame.ssoserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iccgame.ssoserver.domain.entity.TbOauth2;
import com.iccgame.ssoserver.enums.ECODE;
import com.iccgame.ssoserver.service.TbOauth2Service;
import com.iccgame.ssoserver.util.*;
import com.iccgame.ssoserver.vo.*;
import net.sf.jsqlparser.expression.JsonExpression;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class SSOServerController {


    @Value("${refresh_token_timeout}")
    private String refresh_token_timeout;
    @Value("${access_token_timeout}")
    private String access_token_timeout;

    @Autowired
    private TbOauth2Service oauth2Service;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 全局登录检查
     * @param redirectUrl
     * @param session
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/checkLogin")
    public String checklogin(String success_redirectUrl,String fail_redirectUrl, HttpSession session,RedirectAttributes redirectAttributes,HttpServletRequest request){

        //1、判断是否有全局的会话
        String code = (String) session.getAttribute("code");
        if (StringUtils.isEmpty(code)){
            //没有全局会话
            return "redirect:"+fail_redirectUrl;
        } else {
            //有全局会话
            redirectAttributes.addAttribute("code",code);
            return "redirect:"+success_redirectUrl;
        }
    }

    /**
     * 授权登录
     * @param username 账号
     * @param password 密码
     * @param gameid 游戏id
     * @param redirectUrl 回调地址
     * @param session
     * @param redirectAttributes
     * @param request
     * @return
     */
    /*@RequestMapping("/login")
    public String login(String username, String password,String gameid, String redirectUrl, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request){
        //如果回调地址是空，则取referer
        redirectUrl = StringUtils.isEmpty(redirectUrl)?request.getHeader("referer"):redirectUrl;

        //TODO 查询数据库用户信息
        //this.getUserInfo(username,password,gameid);
        if ("admin".equals(username) && "123456".equals(password)){
            //登录验证成功
            //1、创建令牌信息
            String token = UUID.randomUUID().toString();
            //2、创建全局会话，将令牌放入会话中
            //System.out.println("login token:"+token);
            session.setAttribute("token",token);

            //3、将令牌信息放入数据库中（redis中）
            String key = redisUtils.getSSOKey(ECODE.CODE.getName(), token);
            redisUtils.set(key,token,Long.valueOf(refresh_token_timeout), TimeUnit.DAYS);
            //MockDatabaseUtil.T_TOKEN.add(token);
            //4、重定向到redirectUrl，并且把令牌信息带上
            redirectAttributes.addAttribute("token",token);
            //redirectAttributes.addAttribute("username",username);
            return "redirect:"+redirectUrl;
        }
        //登录失败
        //redirectAttributes.addAttribute("redirectUrl",redirectUrl);

        //return "login";
        redirectAttributes.addAttribute("errMsg","Wrong account or password");
        String referer = request.getHeader("referer");
        return "redirect:"+redirectUrl;
    }*/

    /**
     * 登录授权
     * @param login 登录信息
     * @param session
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(Login login, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request){

        TbOauth2  oauth2 = oauth2Service.getOne(new QueryWrapper<TbOauth2>().eq("client_id", login.getClient_id()).last(" limit 1"));
        if (null == oauth2){
            return ResultUtil.error("未授权的客户端");
        }

        //TODO user信息填充
        User user = new User("1","admin","男",26);
        if ("admin".equals(login.getUsername()) && "123456".equals(login.getPassword())){
            //1、创建授权码
            String code = UUID.randomUUID().toString();
            //String code = MI.encoder(JSON.toJSONString(user));

            //2、创建全局会话，将令牌放入会话中

            session.setAttribute("code",code);

            //3、将令牌信息放入数据库中（redis中）
            String key = redisUtils.getSSOKey(ECODE.CODE.getName(), code+IpUtil.getIpAddress(request));
           //授权码code默认保存15分钟,保存用户登录信息
            redisUtils.set(key,code,Long.valueOf(refresh_token_timeout), TimeUnit.MINUTES);

            return ResultUtil.success(code);
        }
        return ResultUtil.error("账户名或密码错误");
    }

    
    /**
     * 获取token
     * @param oAuthToken
     * @return
     */
    @RequestMapping("/oauth/token")
    @ResponseBody
    public String verifyToken(OAuthToken oAuthToken,HttpSession session){
        if (!this.cheakParam(oAuthToken)){
            return ResultUtil.error(1000,"缺少请求参数");
        }
        TbOauth2  oauth2 = oauth2Service.getOne(new QueryWrapper<TbOauth2>().eq("client_id", oAuthToken.getClient_id()).last(" limit 1"));
        if (null == oauth2 ){
            return ResultUtil.error(1001,"client_id错误");
        }
        if (!oauth2.getClientSecret().equals(oauth2.getClientSecret())){
            return ResultUtil.error(1002,"client_secret错误");
        }
        if (StringUtils.isEmpty(oAuthToken.getSign())){
            return ResultUtil.error(3001,"签名校验失败");
        }
        if (!oAuthToken.getSign().equals(getSign(oAuthToken,oauth2.getClientSecret()))){
            return ResultUtil.error(3001,"签名校验失败");
        }

        String codeKey = redisUtils.getSSOKey(ECODE.CODE.getName(), oAuthToken.getCode()+oAuthToken.getIp());
        String code = redisUtils.get(codeKey);
        if (StringUtils.isEmpty(code)){
            return ResultUtil.error(2001,"code失效或不存在");
        }

        if (!addClientInfo(oAuthToken)){
            return ResultUtil.error(9001,"系统异常");
        }
        //refresh_token
        String refresh_token = UUID.randomUUID().toString();

        StringBuilder refresh_token_sb = new StringBuilder();
        refresh_token_sb.append("refresh_token").append(":").append(oAuthToken.getClient_id()).append(":").append(refresh_token);
        //给access_token重新赋值
        redisUtils.set(refresh_token_sb.toString(),refresh_token,Long.valueOf(refresh_token_timeout),TimeUnit.DAYS);
        //User user = JSONObject.toJavaObject(JSON.parseObject(userStr), User.class);
        String accessTokenStr = this.refresh_access_token(oAuthToken.getClient_id(), refresh_token);
        JSONObject accessTokenObj = JSONObject.parseObject(accessTokenStr);
        //
        //if (redisUtils.hasKey(codeKey)){redisUtils.del(codeKey);}

        return ResultUtil.success(new Token(accessTokenObj.getString("data"),refresh_token,oAuthToken.getClient_id(),null));
    }

    /**
     * 请求参数非空校验
     * @param oAuthToken
     * @return
     */
    private boolean cheakParam(OAuthToken oAuthToken) {
        if (StringUtils.isEmpty(oAuthToken.getIp())
        ||StringUtils.isEmpty(oAuthToken.getClient_id())
        ||StringUtils.isEmpty(oAuthToken.getCode())
        ||StringUtils.isEmpty(oAuthToken.getLog_out_url())
        ||StringUtils.isEmpty(oAuthToken.getSession_type())
        ||StringUtils.isEmpty(oAuthToken.getSession_id())
        ||StringUtils.isEmpty(oAuthToken.getSign())
        ||StringUtils.isEmpty(oAuthToken.getRedirect_uri())
        ||StringUtils.isEmpty(oAuthToken.getResponse_type())
        ){
            return false;
        }
        return true;
    }

    /**
     * 刷新access_token
     * @param client_id
     * @param refresh_token
     * @return
     */
    @PostMapping("/oauth/refresh_access_token")
    @ResponseBody
    public String refresh_access_token(String client_id,String refresh_token){
        if (StringUtils.isEmpty(client_id)||StringUtils.isEmpty(refresh_token)){
            return ResultUtil.error(1000,"缺少请求参数");
        }

        StringBuilder refresh_token_sb = new StringBuilder();
        refresh_token_sb.append("refresh_token").append(":").append(client_id).append(":").append(refresh_token);
        String userStr = redisUtils.get(refresh_token_sb.toString());
        if (StringUtils.isEmpty(userStr)){
            return ResultUtil.error(4002,"refresh_token过期,请重新授权登录");
        }
        String access_token = UUID.randomUUID().toString();
        StringBuilder access_token_sb = new StringBuilder();
        access_token_sb.append("access_token").append(":").append(client_id).append(":").append(access_token);
        //给access_token重新赋值
        redisUtils.set(access_token_sb.toString(),userStr,Long.valueOf(access_token_timeout),TimeUnit.SECONDS);

        return ResultUtil.success(access_token);
    }

    /**
     * 单点退出
     * @param redirectUrl
     * @param session
     * @param request
     * @return
     */
    @RequestMapping("/logOut")
    public String logOut(String redirectUrl,HttpSession session,HttpServletRequest request){


        //删除缓存中的code
        String code = (String)session.getAttribute("code");

        String key = redisUtils.getSSOKey(ECODE.CODE.getName(), code+IpUtil.getIpAddress(request));
        //授权码code默认保存15分钟,保存用户登录信息
        redisUtils.del(key);

        //销毁全局会话
        session.invalidate();

        return "redirect:"+redirectUrl;
    }


    /**
     * 获取用户信息
     * @param client_id
     * @param access_token
     * @return
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    public String user(String client_id,String access_token){
        if (StringUtils.isEmpty(client_id)||StringUtils.isEmpty(access_token)){
            return ResultUtil.error(1000,"缺少请求参数");
        }

        StringBuilder access_token_sb = new StringBuilder();
        access_token_sb.append("access_token").append(":").append(client_id).append(":").append(access_token);
        String res = redisUtils.get(access_token_sb.toString());
        if (StringUtils.isEmpty(res)){
            return ResultUtil.error(4001,"access_token过期，请刷新refresh_token或重新登录");
        }
        JSONObject jsonObject = JSONObject.parseObject(res);
        return ResultUtil.success(jsonObject);
    }

    /**
     * 获取游戏类型
     * @param gameid
     * @return
     */
    @RequestMapping("/getGameType")
    @ResponseBody
    public String getGameType(String clientId){
        if (StringUtils.isEmpty(clientId)){
            return ResultUtil.error("平台类型不允许为空");
        }
        TbOauth2  oauth2 = oauth2Service.getOne(new QueryWrapper<TbOauth2>().eq("client_id", clientId).last(" limit 1"));
        if (null==oauth2){
            return ResultUtil.error("没有获取到游戏类型");
        }
        return ResultUtil.success(oauth2.getClientName());
    }

    /**
     * 签名生成
     * @param oAuthToken
     * @param client_secret
     * @return
     */
    private String getSign(OAuthToken oAuthToken,String client_secret){
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("client_id", oAuthToken.getClient_id());
        params.put("response_type", oAuthToken.getResponse_type());
        params.put("redirect_uri", oAuthToken.getRedirect_uri());
        params.put("code", oAuthToken.getCode());
        params.put("session_type", oAuthToken.getSession_type());
        params.put("session_id", oAuthToken.getSession_id());
        params.put("log_out_url", oAuthToken.getLog_out_url());
        params.put("client_secret",client_secret);
        params.put("ip",oAuthToken.getIp());
        String sign = SignUtil.sign(params);
        return  sign;
    }


    /**
     * 记录客户端session信息，单点退出要用到
     * @param oAuthToken
     * @return
     */
    private  boolean addClientInfo(OAuthToken oAuthToken){
        String codeClientInfoKey = redisUtils.getSSOKey(ECODE.CODE_CLIENT_INFO.getName(),oAuthToken.getCode());
        //把客户端退出地址记录起来，单点退出用
        String tokenClientInfoStr = redisUtils.get(codeClientInfoKey);
        List<ClientInfoVo> clientInfoList = JSON.parseArray(tokenClientInfoStr, ClientInfoVo.class);
        if (CollectionUtils.isEmpty(clientInfoList)){
            clientInfoList = new ArrayList<ClientInfoVo>();
        }
        ClientInfoVo vo = new ClientInfoVo();
        vo.setLogOutUrl(oAuthToken.getLog_out_url());
        vo.setSessionid(oAuthToken.getSession_id());
        vo.setSessionType(oAuthToken.getSession_type());
        clientInfoList.add(vo);
        boolean boo = redisUtils.set(codeClientInfoKey,JSON.toJSONString(clientInfoList),Long.valueOf(refresh_token_timeout), TimeUnit.DAYS);
        return boo;
    }
}
