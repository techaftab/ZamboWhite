package com.app.sriparas.network;

import com.app.sriparas.models.LoginResponse;
import com.app.sriparas.models.UserloginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestApiService {

    @FormUrlEncoded
    @POST("mobileapi/loginmobile")
    Call<LoginResponse> loginUser(@Field("mobile") String phone,@Field("password") String password) ;

    @FormUrlEncoded
    @POST("mobileapi/loginresend")
    Call<LoginResponse> resendOtpLogin(@Field("mobile") String phone);

    @FormUrlEncoded
    @POST("mobileapi/loginverify")
    Call<UserloginResponse> verifyOtpLogin(@Field("mobile") String phone,@Field("otp") String otp);

    @FormUrlEncoded
    @POST("mobileapi/forgetmobile")
    Call<LoginResponse> verifyFgtMobile(@Field("mobile") String phone);

    @FormUrlEncoded
    @POST("mobileapi/forgetmobileotp")
    Call<LoginResponse> verifyOtpForgot(@Field("mobile") String phone,@Field("otp") String otp);
}
