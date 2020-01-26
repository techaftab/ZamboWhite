package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileResponse {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("userdata")
    @Expose
    public UserDetails userdata;

    public UserDetails getUserdata() {
        return userdata;
    }

    public void setUserdata(UserDetails userdata) {
        this.userdata = userdata;
    }

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

    public class UserDetails {
        @SerializedName("mobile")
        @Expose
        public String mobile;

        @SerializedName("fullName")
        @Expose
        public String fullName;

        @SerializedName("email")
        @Expose
        public String email;

        @SerializedName("memberId")
        @Expose
        public String memberId;

        @SerializedName("city")
        @Expose
        public String city;

        @SerializedName("address")
        @Expose
        public String address;

        @SerializedName("state")
        @Expose
        public String state;

        @SerializedName("company")
        @Expose
        public String company;

        @SerializedName("dob")
        @Expose
        public String dob;

        @SerializedName("usertype")
        @Expose
        public String usertype;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
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

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }
    }
}
