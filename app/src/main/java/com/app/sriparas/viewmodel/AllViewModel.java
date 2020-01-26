package com.app.sriparas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.sriparas.models.HistoryModel;
import com.app.sriparas.models.ProfileResponse;
import com.app.sriparas.repositories.AllViewRepository;

public class AllViewModel extends AndroidViewModel {

    private AllViewRepository allViewRepository;

    public AllViewModel(@NonNull Application application) {
        super(application);
        allViewRepository = new AllViewRepository();
    }

    public LiveData<HistoryModel> getHistory(String id, String txntoken, String formattedDate,String source) {
        return allViewRepository.getHistory(id,txntoken,formattedDate,source);
    }

    public LiveData<ProfileResponse> getProfile(String id, String txntoken, String source) {
        return allViewRepository.getProfile(id,txntoken,source);
    }
}
