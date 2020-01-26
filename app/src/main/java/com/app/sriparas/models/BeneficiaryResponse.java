package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeneficiaryResponse {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("beneficiary")
    @Expose
    public List<Beneficiary> beneficiary;

    public List<Beneficiary> getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(List<Beneficiary> beneficiary) {
        this.beneficiary = beneficiary;
    }
    public BeneficiaryResponse(){

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


    @Override
    public String toString()
    {
        return "ClassPojo [status = "+status+"]";
    }

    public class Beneficiary {
        @SerializedName("beneficiary_id")
        @Expose
        private String beneficiary_id;

        @SerializedName("beneName")
        @Expose
        private String beneName;

        @SerializedName("beneMobile")
        @Expose
        private String beneMobile;

        @SerializedName("beneAccount")
        @Expose
        private String beneAccount;

        @SerializedName("bank")
        @Expose
        private String bank;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("ifsccode")
        @Expose
        private String ifsccode;

        @SerializedName("is_verified")
        @Expose
        private String is_verified;

        public Beneficiary (){

        }

        public String getBeneficiary_id() {
            return beneficiary_id;
        }

        public void setBeneficiary_id(String beneficiary_id) {
            this.beneficiary_id = beneficiary_id;
        }

        public String getBeneName() {
            return beneName;
        }

        public void setBeneName(String beneName) {
            this.beneName = beneName;
        }

        public String getBeneMobile() {
            return beneMobile;
        }

        public void setBeneMobile(String beneMobile) {
            this.beneMobile = beneMobile;
        }

        public String getBeneAccount() {
            return beneAccount;
        }

        public void setBeneAccount(String beneAccount) {
            this.beneAccount = beneAccount;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIfsccode() {
            return ifsccode;
        }

        public void setIfsccode(String ifsccode) {
            this.ifsccode = ifsccode;
        }

        public String getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(String is_verified) {
            this.is_verified = is_verified;
        }
    }
}
