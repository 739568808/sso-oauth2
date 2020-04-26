package com.iccgame.ssoserver.vo;

public class User {
    private  String id;
    private String username;
    private String sex;
    private Integer age;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(){}
    public User(String id,String username, String sex, Integer age) {
        this.id = id;
        this.username = username;
        this.sex = sex;
        this.age = age;
    }
}
