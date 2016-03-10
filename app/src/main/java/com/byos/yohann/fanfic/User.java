package com.byos.yohann.fanfic;

import java.util.HashMap;

/**
 * Created by Yohann on 08/03/2016.
 */
public class User {

    public static final Integer ERROR = -1;
    private int id;
    private String name;
    private String email;
    private String pass;
    private HashMap<Integer, String> status;

    public User(int id, String email, String name, HashMap<Integer, String> status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.status = status;
    }

    public HashMap<Integer, String> getStatus() {
        return status;
    }

    public void setStatus(HashMap<Integer, String> status) {
        this.status = status;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
