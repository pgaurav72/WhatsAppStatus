package com.gaurav.whatsappstatus;

public class Statuslist {

  public Statuslist(){

  }

  public Statuslist(String uid, String users_images) {
    this.uid = uid;
    this.users_images = users_images;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUsers_images() {
    return users_images;
  }

  public void setUsers_images(String users_images) {
    this.users_images = users_images;
  }

  private String uid;
  private String users_images;

  public Statuslist(String private_images) {
    this.private_images = private_images;
  }

  public String getPrivate_images() {
    return private_images;
  }

  public void setPrivate_images(String private_images) {
    this.private_images = private_images;
  }

  private String private_images;


}