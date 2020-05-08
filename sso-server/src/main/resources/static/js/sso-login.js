var passportUrl = "http://www.sso.com:8080";//认证中心地址
var redirectUrl = document.getElementById('login').getAttribute('redirectUrl');
var client_id = document.getElementById('login').getAttribute('client_id');
var response_type = document.getElementById('login').getAttribute('response_type');
var scope = document.getElementById('login').getAttribute('scope');
var state = document.getElementById('login').getAttribute('state');
var htmml = "<div id='login-box' style='width: 220px;height: 150px;background: #e8e8e8;margin: 0 auto;margin-top: 200px;padding: 30px;box-shadow: 10px 10px 5px #888888;'><form id='sso-login-form' method='post' action=\""+passportUrl+"/login2\">\n" +
    "    <input type='hidden' name='redirectUrl' value="+redirectUrl+">\n" +
    "    <input type='hidden' name='client_id' value="+client_id+">\n" +
    "    <input type='hidden' name='response_type' value="+response_type+">\n" +
    "    <input type='hidden' name='scope' value="+scope+">\n" +
    "    <input type='hidden' name='state' value="+state+">\n" +
    "    <div>登录平台：<span id='gameName'></span></div><br/>\n"+
    "    <label>邮箱 </label><input id='email' type='text' name='email'><br/><br/>\n" +
    "    <label>密码 </label><input id='password' type='password' name='password'><br/><br/>\n" +
    "   <div style='margin: 0 auto;text-align: center'> <input type='button' value='登录' onclick='login()'></div>\n" +
    "</form></div>";


var errMsg = getQueryVariable("errMsg");
if(errMsg!=''&&errMsg!=null){
    htmml+="<div>"+errMsg+"</div>";
}

var SSO_Ajax = {
    get: function (url, fn) {
        // XMLHttpRequest对象用于在后台与服务器交换数据
        var xhr = new XMLHttpRequest();
        //每当readyState改变时就会触发onreadystatechange函数
        //0: 请求未初始化
        //1: 服务器连接已建立
        //2: 请求已接收
        //3: 请求处理中
        //4: 请求已完成，且响应已就绪
        xhr.open('GET', url, true)
        xhr.setRequestHeader("dataType", "json");
        //支持跨域发送cookies
        xhr.withCredentials = true;
        xhr.onreadystatechange = function () {
            //readyStatus == 4说明请求已经完成
            if(xhr.readyState == 4 && xhr.status ==200) {
                //从服务器获得数据
                fn.call(this, xhr.responseText);
            }
        };
        //发送数据
        xhr.send();
    },
    // datat应为'a=a1&b=b1'这种字符串格式，在jq里如果data为对象会自动将对象转成这种字符串格式
    post: function (url, data, fn) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        // 添加http头，发送信息至服务器时内容编码类型
        xhr.setRequestHeader("Content-Type", "application/json");
        //支持跨域发送cookies
        xhr.withCredentials = true;

        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
                fn.call(this, xhr.responseText);
            }
        };
        //发送数据
        xhr.send(JSON.stringify(data));
    }
}
/**
 * 游戏id
 */
if (client_id!=''&&client_id!=null){
    SSO_Ajax.get(passportUrl+"/getGameType?clientId="+client_id,function (data) {
        var obj = JSON.parse(data);
        if (obj.code==200){
            document.getElementById("gameName").innerText= obj.data;
        }else{
            document.getElementById("gameName").innerText= "--";
        }
    })
}

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return "";
}


function login(){
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    if (email ==null || email==''){
        alert("邮箱不能为空")
        return;
    }
    if (password ==null || password==''){
        alert("密码不能为空")
        return;
    }
    if (client_id ==null || client_id==''){
        alert("参数异常");
        return;
    }
    var param = {"redirectUrl":redirectUrl,
        "email":email,
        "password":password,
        "client_id":client_id,
        "response_type":response_type,"scope":scope,"state":state}
   // var param = "redirectUrl="+redirectUrl+"&email="+email+"&password="+password+"&client_id="+client_id+"&response_type="+response_type+"&scope="+scope+"&state="+state
    SSO_Ajax.post(passportUrl+"/login",param,function (data) {
        var obj = JSON.parse(data);
        //TODO  请求游戏类型
        if (obj.code==200){
            location.href = redirectUrl+"?"+response_type+"="+obj.data.code
        }else {
            alert(obj.msg);
            return;
        }
    })



}




//显示登录框
document.write(htmml)