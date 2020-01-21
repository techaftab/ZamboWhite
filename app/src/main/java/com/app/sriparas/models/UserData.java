package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("fullName")
    @Expose
    public String fullName;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("mobile")
    @Expose
    public String mobile;

    @SerializedName("usertype")
    @Expose
    public String usertype;

    @SerializedName("txntoken")
    @Expose
    public String txntoken;

    public UserData(String id, String fullName, String email, String mobile, String usertype, String txntoken) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.usertype = usertype;
        this.txntoken = txntoken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getTxntoken() {
        return txntoken;
    }

    public void setTxntoken(String txntoken) {
        this.txntoken = txntoken;
    }
}
