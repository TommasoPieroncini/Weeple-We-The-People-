package com.weeple.weeple.weeple;

import android.app.Application;

/**
 * Created by Tommaso on 1/1/2016.
 */
public class MyApplication extends Application {

    private String username;
    private String password;
    private String profile;

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setProfile(String profile){
        this.profile = profile;
    }

    public String getProfile(){
        return this.profile;
    }
}
