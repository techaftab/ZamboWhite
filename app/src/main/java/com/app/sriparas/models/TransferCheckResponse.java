package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferCheckResponse {

    public TransferCheckResponse(){}

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("remitter")
    @Expose
    public RemitterResponse remitter;

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

    public RemitterResponse getRemitter() {
        return remitter;
    }

    public void setRemitter(RemitterResponse remitter) {
        this.remitter = remitter;
    }

    public class RemitterResponse {

        @SerializedName("remitterId")
        @Expose
        public String remitterId;

        @SerializedName("remName")
        @Expose
        public String remName;

        @SerializedName("remMobile")
        @Expose
        public String remMobile;

        @SerializedName("kycstatus")
        @Expose
        public String kycstatus;

        @SerializedName("consumedlimit")
        @Expose
        public String consumedlimit;

        @SerializedName("remaininglimit")
        @Expose
        public String remaininglimit;

        @SerializedName("remitter_limit")
        @Expose
        public String remitter_limit;

        @SerializedName("is_verified")
        @Expose
        public String is_verified;

        public String getRemitterId() {
            return remitterId;
        }

        public void setRemitterId(String remitterId) {
            this.remitterId = remitterId;
        }

        public String getRemName() {
            return remName;
        }

        public void setRemName(String remName) {
            this.remName = remName;
        }

        public String getRemMobile() {
            return remMobile;
        }

        public void setRemMobile(String remMobile) {
            this.remMobile = remMobile;
        }

        public String getKycstatus() {
            return kycstatus;
        }

        public void setKycstatus(String kycstatus) {
            this.kycstatus = kycstatus;
        }

        public String getConsumedlimit() {
            return consumedlimit;
        }

        public void setConsumedlimit(String consumedlimit) {
            this.consumedlimit = consumedlimit;
        }

        public String getRemaininglimit() {
            return remaininglimit;
        }

        public void setRemaininglimit(String remaininglimit) {
            this.remaininglimit = remaininglimit;
        }

        public String getRemitter_limit() {
            return remitter_limit;
        }

        public void setRemitter_limit(String remitter_limit) {
            this.remitter_limit = remitter_limit;
        }

        public String getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(String is_verified) {
            this.is_verified = is_verified;
        }
    }
}
