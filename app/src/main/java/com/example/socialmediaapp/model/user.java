package com.example.socialmediaapp.model;

import androidx.room.Entity;

@Entity(tableName = "user")
public class user {
   String userui;
   String userName;
   String imageUrl;
    public  user()
   {
   }
   public user(String userui,String username,String imageUrl)
   {
       this.userui=userui;
       this.imageUrl=imageUrl;
       this.userName=username;
   }
    public String getUserui() {
        return userui;
    }
    public void setUserui(String userui) {
        this.userui = userui;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Override
    public String toString() {
        return "user{" +
                "userui='" + userui + '\'' +
                ", userName='" + userName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
