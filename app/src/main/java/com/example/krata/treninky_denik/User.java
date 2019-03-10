package com.example.krata.treninky_denik;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class User {
    String nick, mail, pass;
    Bitmap profilePic = null;
    boolean trainer;
    ArrayList<Lesson> lessons = new ArrayList<>();

    public User(String nick, String mail, String pass, boolean trainer) {
        this.nick = nick;
        this.mail = mail;
        this.pass = pass;
        this.trainer = trainer;
    }

    public User(String nick, String mail, String pass, boolean trainer, Bitmap profilePic) {
        this.nick = nick;
        this.mail = mail;
        this.pass = pass;
        this.trainer = trainer;
        this.profilePic = profilePic;
    }

    public User(String nick, String pass) {
        this.nick = nick;
        this.pass = pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNick()
    {
        return nick;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }
}
