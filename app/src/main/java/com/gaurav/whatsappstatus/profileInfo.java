package com.gaurav.whatsappstatus;

public class profileInfo {
  public String uid;
  public String profile_image;

  public profileInfo(){

  }

  public String getProfile_image() {
    return profile_image;
  }

  public void setProfile_image(String profile_image) {
    this.profile_image = profile_image;
  }


  public profileInfo(String profile_image,String uid) {
    this.profile_image = profile_image;
    this.uid = uid;
  }





}


