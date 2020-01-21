package com.app.sriparas.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.sriparas.models.LoginResponse;
import com.app.sriparas.models.UserloginResponse;
import com.app.sriparas.network.RestApiService;
import com.app.sriparas.network.RetrofitInstance;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private String TAG=LoginRepository.class.getSimpleName();
    public LoginRepository() {
    }

    public LiveData<LoginResponse> loginUser(String phone, String password) {
        MutableLiveData<LoginResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<LoginResponse> call = apiService.loginUser(phone,password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<LoginResponse> resendOtpLogin(String phone) {
        MutableLiveData<LoginResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<LoginResponse> call = apiService.resendOtpLogin(phone);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<UserloginResponse> verifyOtpLogin(String phone, String otp) {
        MutableLiveData<UserloginResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<UserloginResponse> call = apiService.verifyOtpLogin(phone,otp);

        call.enqueue(new Callback<UserloginResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserloginResponse> call,
                                   @NonNull Response<UserloginResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<UserloginResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<LoginResponse> verifyFgtMobile(String phone) {
        MutableLiveData<LoginResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<LoginResponse> call = apiService.verifyFgtMobile(phone);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<LoginResponse> verifyOtpForgot(String phone, String otp) {
        MutableLiveData<LoginResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<LoginResponse> call = apiService.verifyOtpForgot(phone,otp);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call,
                                   @NonNull Response<LoginResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }
}
