package com.example.demo.model;

public class Profile {
  String id;
  String username;
  String email;
  String nickName;
  String surName;
  String name;
  String picture;
  String provider;
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  @Override
  public String toString() {
    return "Profile [email=" + email + ", id=" + id + ", name=" + name + ", nickName=" + nickName + ", picture="
        + picture + ", provider=" + provider + ", surName=" + surName + ", username=" + username + "]";
  }

  
  
}