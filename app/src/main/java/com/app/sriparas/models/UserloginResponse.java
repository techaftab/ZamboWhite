package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserloginResponse {

    public UserloginResponse(){}

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("userdata")
    @Expose
    public UserdataResponse userdata;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserdataResponse getUserdata() {
        return userdata;
    }

    public void setUserdata(UserdataResponse userdata) {
        this.userdata = userdata;
    }

    public class UserdataResponse {

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

        @SerializedName("memberId")
        @Expose
        public String memberId;

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
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
}
