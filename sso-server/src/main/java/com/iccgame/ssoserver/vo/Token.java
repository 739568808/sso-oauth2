package com.iccgame.ssoserver.vo;

import org.springframework.util.StringUtils;

public class Token {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
    private String client_id;
    private Object info;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        if (StringUtils.isEmpty(token_type)){
            token_type = "Bearer";
        }
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Integer getExpires_in() {
        if (null == expires_in){
            return 7200;//秒
        }
        return expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        if (StringUtils.isEmpty(scope)){
            return "default";
        }
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public Token(){}
    public Token(String access_token, String refresh_token, String client_id, Object info) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.client_id = client_id;
        this.info = info;
    }
}
