package com.fevi.fadong.domain;

/**
 * Created by 1000742 on 15. 1. 28..
 */
public class Member {

    private String id = "";
    private String password = "";
    private String passwordRepeat = "";
    private String mdn = "";
    private String mcc = "";
    private String googleId = "";
    private String facebookId = "";
    private String gcm = "";
    private String pushAgree = "";

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
        return "MemberInfo{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", passwordRepeat='" + passwordRepeat + '\'' +
                ", mdn='" + mdn + '\'' +
                ", mcc='" + mcc + '\'' +
                ", googleId='" + googleId + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", gcm='" + gcm + '\'' +
                ", pushAgree='" + pushAgree + '\'' +
                '}';
    }
}
