package com.app.zambowhite.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.zambowhite.models.LoginResponse;
import com.app.zambowhite.models.UserloginResponse;
import com.app.zambowhite.repositories.LoginRepository;

public class LoginViewModel extends AndroidViewModel {

    LoginRepository loginRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new LoginRepository();
    }

    public LiveData<LoginResponse> loginUser(String phone, String password) {
        return loginRepository.loginUser(phone,password);
    }

    public LiveData<LoginResponse> resendOtpLogin(String phone) {
        return loginRepository.resendOtpLogin(phone);
    }

    public LiveData<UserloginResponse> verifyOtpLogin(String phone, String otp) {
        return loginRepository.verifyOtpLogin(phone,otp);
    }

    public LiveData<LoginResponse> verifyFgtMobile(String phone) {
        return loginRepository.verifyFgtMobile(phone);
    }

    public LiveData<LoginResponse> verifyOtpForgot(String phone, String otp) {
        return loginRepository.verifyOtpForgot(phone,otp);
    }
}
