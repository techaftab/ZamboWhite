package com.app.sriparas.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryModel {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("userdata")
    @Expose
    public List<DetailsHistory> userdata;

    public List<DetailsHistory> getUserdata() {
        return userdata;
    }

    public void setUserdata(List<DetailsHistory> userdata) {
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

    public class DetailsHistory {

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("userId")
        @Expose
        public String userId;

        @SerializedName("transactionId")
        @Expose
        public String transactionId;

        @SerializedName("amount")
        @Expose
        public String amount;

        @SerializedName("type")
        @Expose
        public String type;

        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("remarks")
        @Expose
        public String remarks;

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("status")
        @Expose
        public String status;

        @SerializedName("oldBalance")
        @Expose
        public String oldBalance;

        @SerializedName("newBalance")
        @Expose
        public String newBalance;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOldBalance() {
            return oldBalance;
        }

        public void setOldBalance(String oldBalance) {
            this.oldBalance = oldBalance;
        }

        public String getNewBalance() {
            return newBalance;
        }

        public void setNewBalance(String newBalance) {
            this.newBalance = newBalance;
        }
    }
}
