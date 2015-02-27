package com.fevi.fadong.domain;

import java.io.Serializable;

/**
 * Created by 1000742 on 15. 1. 28..
 */
public class Member implements Serializable {

    private static final long serialVersionUID = -2069886589819128997L;

    private String id = "";
    private String password = "";
    private String passwordRepeat = "";
    private String mdn = "";
    private String mcc = "";
    private String googleId = "";
    private String facebookId = "";
    private String gcm = "";
    private String pushAgree = "";
    private String level = "";
    private String experience = "";
    private String nextExperience = "";
    private String ruby = "";
    private String heart = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public String getPushAgree() {
        return pushAgree;
    }

    public void setPushAgree(String pushAgree) {
        this.pushAgree = pushAgree;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public boolean isValid() {
        if(password.equals(passwordRepeat)) {
            return true;
        }
        return false;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getNextExperience() {
        return nextExperience;
    }

    public void setNextExperience(String nextExperience) {
        this.nextExperience = nextExperience;
    }

    public String getRuby() {
        return ruby;
    }

    public void setRuby(String ruby) {
        this.ruby = ruby;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getParameter() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(getId())
                .append("&password=").append(getPassword())
                .append("&mdn=").append(getMdn())
                .append("&mcc=").append(getMcc())
                .append("&googleID=").append(getGoogleId())
                .append("&facebookID=").append(getFacebookId())
                .append("&gcm=").append(getGcm())
                .append("&push=").append(getPushAgree());
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", passwordRepeat='" + passwordRepeat + '\'' +
                ", mdn='" + mdn + '\'' +
                ", mcc='" + mcc + '\'' +
                ", googleId='" + googleId + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", gcm='" + gcm + '\'' +
                ", pushAgree='" + pushAgree + '\'' +
                ", level='" + level + '\'' +
                ", experience='" + experience + '\'' +
                ", nextExperience='" + nextExperience + '\'' +
                ", ruby='" + ruby + '\'' +
                ", heart='" + heart + '\'' +
                '}';
    }
}
