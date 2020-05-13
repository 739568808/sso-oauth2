var passportUrl = "http://www.sso.com:8091";//dev认证中心地址
//var passportUrl = "http://192.168.0.221:8091";//test认证中心地址
var redirectUrl = document.getElementById('logOut').getAttribute('redirectUrl');
var url = passportUrl+'/logOut?redirectUrl='+redirectUrl;
var htmml = "<a href="+url+">退出系统</a>";
document.write(htmml)