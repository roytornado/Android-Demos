package com.redso.mvvmdemo.data;

public class Post {
  public String postId;
  public String title;
  public String desc;
  public String imageUrl;

  public Post(String postId, String title, String desc, String imageUrl) {
    this.postId = postId;
    this.title = title;
    this.desc = desc;
    this.imageUrl = imageUrl;
  }
}
