package com.example.socialmediaapp.model;

import java.util.ArrayList;
import java.util.List;

public class PostModel {
    String post;
    int textColor;
    int backgroundColor;
    ArrayList<String> likes=new ArrayList<>();
    user name;
    long time;
    String image_url;
    String video_url;
    public PostModel() {
    }
    public PostModel(String post, ArrayList<String> likes, user name, long time, String image_url, String video_url,int textColor,int backgroundColor) {
        this.post = post;
        this.likes = likes;
        this.name = name;
        this.time = time;
        this.image_url = image_url;
        this.video_url = video_url;
        this.textColor=textColor;
        this.backgroundColor=backgroundColor;
    }
    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public user getName() {
        return name;
    }

    public void setName(user name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
