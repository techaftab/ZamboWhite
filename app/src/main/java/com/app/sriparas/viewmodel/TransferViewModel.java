package com.app.sriparas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.sriparas.models.BeneficiaryResponse;
import com.app.sriparas.models.TransferCheckResponse;
import com.app.sriparas.models.TransferResponse;
import com.app.sriparas.repositories.TransferRepository;

public class TransferViewModel extends AndroidViewModel {
    private TransferRepository transferRepository;

    public TransferViewModel(@NonNull Application application) {
        super(application);
        transferRepository = new TransferRepository();
    }

    public LiveData<TransferCheckResponse> checkUserTransfer(String mobile, String userId, String token, String source) {
        return transferRepository.checkUserTransfer(mobile, userId, token,source);
    }

    public LiveData<TransferResponse> addRemitter(String mobile, String name, String address, String city, String district,
                                                  String state, String pincode, String userId, String token, String app) {
        return transferRepository.addRemitter(mobile,name, address, city,district,state,pincode,userId,token,app);
    }

    public LiveData<TransferResponse> otpRemitter(String mobile, String userId, String token, String source) {
        return transferRepository.otpRemitter(mobile, userId, token,source);
    }

    public LiveData<TransferResponse> verifyRemitter(String mobile,String otp, String userId, String token, String source) {
        return transferRepository.verifyRemitter(mobile,otp, userId, token,source);
    }

    public LiveData<BeneficiaryResponse> getRemitterBeneficiary(String mobile, String userId, String token, String source) {
        return transferRepository.getRemitterBeneficiary(mobile, userId, token,source);
    }

    public LiveData<TransferResponse> addBeneficiary(String userId, String token, String source, String mobile,String beneMobile,
                                                     String beneName,String bankAccount, String bankIfsccode, String bankCode) {
        return transferRepository.addBeneficiary(userId, token,source,mobile,beneMobile,beneName,bankAccount,bankIfsccode,bankCode);
    }

    public LiveData<TransferResponse> deleteBeneficiary(String userId, String token, String source, String mobile, String beneficiary_id) {
        return transferRepository.deleteBeneficiary(mobile, userId, token,source,beneficiary_id);
    }

    public LiveData<TransferResponse> transferMoney(String userId, String token, String source, String mobile,
                                                    String remitterId, String beneAccount, String txnType,
                                                    String ifsccode, String amount, String beneficiary_id,
                                                    String lat, String lon, String pin) {
        return transferRepository.transferMoney(userId,token,source,mobile,remitterId,
                beneAccount,txnType,ifsccode,amount,beneficiary_id,lat,lon,pin);
    }
}
