package com.app.sriparas.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.sriparas.models.BeneficiaryResponse;
import com.app.sriparas.models.TransferCheckResponse;
import com.app.sriparas.models.TransferResponse;
import com.app.sriparas.network.RestApiService;
import com.app.sriparas.network.RetrofitInstance;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferRepository {

    private static String TAG=TransferRepository.class.getSimpleName();

    public TransferRepository() {
    }

    public LiveData<TransferCheckResponse> checkUserTransfer(String mobile, String userId, String token, String source) {
        MutableLiveData<TransferCheckResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferCheckResponse> call = apiService.checkUserTransfer(mobile, userId, token,source);

        call.enqueue(new Callback<TransferCheckResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferCheckResponse> call,
                                   @NonNull Response<TransferCheckResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferCheckResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;

    }

    public LiveData<TransferResponse> addRemitter(String mobile, String name, String address, String city, String district,
                                                  String state, String pincode, String userId, String token, String app) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.addRemitter(mobile,name,address,city,district,state,pincode, userId, token,app);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<TransferResponse> otpRemitter(String mobile, String userId, String token, String source) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.otpRemitter(mobile, userId, token,source);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<TransferResponse> verifyRemitter(String mobile, String otp, String userId, String token, String source) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.verifyRemitter(mobile,otp, userId, token,source);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<BeneficiaryResponse> getRemitterBeneficiary(String mobile, String userId, String token, String source) {
        MutableLiveData<BeneficiaryResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<BeneficiaryResponse> call = apiService.getRemitterBeneficiary(mobile, userId, token,source);

        call.enqueue(new Callback<BeneficiaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<BeneficiaryResponse> call,
                                   @NonNull Response<BeneficiaryResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<BeneficiaryResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<TransferResponse> addBeneficiary(String userId, String token, String source,
                                                     String mobile,String beneMobile, String bankName,
                                                     String bankAccount, String bankIfsccode, String bankCode) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.addBeneficiary(userId, token,source,mobile,beneMobile,bankName,
                bankAccount,bankIfsccode,bankCode);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<TransferResponse> deleteBeneficiary(String mobile, String userId, String token, String source, String beneficiary_id) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.deleteBeneficiary(userId, token,source,mobile,beneficiary_id);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<TransferResponse> transferMoney(String userId, String token, String source,
                                                    String mobile, String remitterId, String beneAccount,
                                                    String txnType, String ifsccode, String amount,
                                                    String beneficiary_id, String lat, String lon, String pin) {
        MutableLiveData<TransferResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getTransferApiService();

        Call<TransferResponse> call = apiService.transferMoney(userId,token,source,mobile,remitterId,
                beneAccount,txnType,ifsccode,amount,beneficiary_id,lat,lon,pin);

        call.enqueue(new Callback<TransferResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransferResponse> call,
                                   @NonNull Response<TransferResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<TransferResponse> call, @NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }
}
