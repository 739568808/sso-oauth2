package com.iccgame.ssoserver.vo;

public class RedirectUrl {

    private String success_redirectUrl;
    private String fail_redirectUrl;

    public String getSuccess_redirectUrl() {
        return success_redirectUrl;
    }

    public void setSuccess_redirectUrl(String success_redirectUrl) {
        this.success_redirectUrl = success_redirectUrl;
    }

    public String getFail_redirectUrl() {
        return fail_redirectUrl;
    }

    public void setFail_redirectUrl(String fail_redirectUrl) {
        this.fail_redirectUrl = fail_redirectUrl;
    }
}
