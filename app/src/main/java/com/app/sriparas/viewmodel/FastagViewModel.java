package com.app.sriparas.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.app.sriparas.repositories.FastagRepository;

public class FastagViewModel extends AndroidViewModel {

    private FastagRepository fastagRepository;

    public FastagViewModel(@NonNull Application application) {
        super(application);
        fastagRepository = new FastagRepository();
    }
}
