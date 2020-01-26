package com.app.sriparas.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.sriparas.models.HistoryModel;
import com.app.sriparas.models.LoginResponse;
import com.app.sriparas.models.ProfileResponse;
import com.app.sriparas.network.RestApiService;
import com.app.sriparas.network.RetrofitInstance;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllViewRepository {

    private String TAG=AllViewRepository.class.getSimpleName();

    public AllViewRepository() {
    }

    public LiveData<HistoryModel> getHistory(String id, String txntoken, String formattedDate,String source) {
        MutableLiveData<HistoryModel> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<HistoryModel> call = apiService.getHistory(id,txntoken,formattedDate,source);

        call.enqueue(new Callback<HistoryModel>() {
            @Override
            public void onResponse(@NonNull Call<HistoryModel> call,
                                   @NonNull Response<HistoryModel> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<HistoryModel> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }

    public LiveData<ProfileResponse> getProfile(String id, String txntoken, String source) {
        MutableLiveData<ProfileResponse> mutableLiveData = new MutableLiveData<>();

        RestApiService apiService = RetrofitInstance.getApiService();

        Call<ProfileResponse> call = apiService.getProfile(id,txntoken,source);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call,
                                   @NonNull Response<ProfileResponse> response) {
                Log.e(TAG, new Gson().toJson(response.body()));
                mutableLiveData.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call,@NonNull Throwable t) {

            }
        });
        return mutableLiveData;
    }
}
