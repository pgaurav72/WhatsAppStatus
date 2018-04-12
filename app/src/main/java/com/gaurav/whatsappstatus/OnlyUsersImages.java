package com.gaurav.whatsappstatus;

public class OnlyUsersImages {

  public OnlyUsersImages(){

  }

  public OnlyUsersImages(String uid, String users_images) {
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
  String users_images;
}
