package com.example.socialmediaapp.model;

import java.util.ArrayList;
import java.util.List;

public class PostModel {
    String post;
    ArrayList<String> likes=new ArrayList<>();
    user name;
    long time;

    public PostModel() {
    }
    public PostModel(String post, user name, long time,ArrayList<String> Likes) {
        this.post = post;
        this.name = name;
        this.time = time;
        this.likes=Likes;
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
}
