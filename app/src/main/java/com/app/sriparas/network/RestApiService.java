package com.app.sriparas.network;

import com.app.sriparas.models.BeneficiaryResponse;
import com.app.sriparas.models.HistoryModel;
import com.app.sriparas.models.LoginResponse;
import com.app.sriparas.models.ProfileResponse;
import com.app.sriparas.models.TransferCheckResponse;
import com.app.sriparas.models.TransferResponse;
import com.app.sriparas.models.UserloginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
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

    @FormUrlEncoded
    @POST("mobileapi/walletTransaction")
    Call<HistoryModel> getHistory(@Field("userId") String id,@Header("token") String txntoken,
                                  @Field("date") String formattedDate,@Field("source") String source);

    @FormUrlEncoded
    @POST("mobileapi/getProfile")
    Call<ProfileResponse> getProfile(@Field("userId") String id, @Header("token") String txntoken,@Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/getSenderInfo")
    Call<TransferCheckResponse> checkUserTransfer(@Field("remMobile") String mobile, @Field("userId") String userId,
                                                  @Header("token") String token, @Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/createSender")
    Call<TransferResponse> addRemitter(@Field("remMobile") String mobile, @Field("remName") String name,
                                       @Field("address") String address, @Field("city") String city,
                                       @Field("district") String district, @Field("state") String state,
                                       @Field("pincode") String pincode, @Field("userId") String userId,
                                       @Header("token") String token, @Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/remOtp")
    Call<TransferResponse> otpRemitter(@Field("remMobile") String mobile,@Field("userId") String userId,
                                       @Header("token") String token,@Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/verifyRemitter")
    Call<TransferResponse> verifyRemitter(@Field("remMobile") String mobile,@Field("remOtp") String otp,
                                          @Field("userId") String userId,@Header("token") String token,@Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/getBeneficiary")
    Call<BeneficiaryResponse> getRemitterBeneficiary(@Field("remMobile") String mobile, @Field("userId") String userId,
                                                     @Header("token") String token, @Field("source") String source);

    @FormUrlEncoded
    @POST("dmt/addBeneficiary")
    Call<TransferResponse> addBeneficiary(@Field("userId") String userId,@Header("token") String token,@Field("source") String source,
                                          @Field("remMobile")String mobile,@Field("beneMobile")String beneMobile,@Field("beneName") String beneName,
                                          @Field("beneAccount") String bankAccount,@Field("ifsccode") String bankIfsccode,
                                          @Field("bankcode") String bankCode);

    @FormUrlEncoded
    @POST("dmt/deleteBeneficiary")
    Call<TransferResponse> deleteBeneficiary(@Field("userId") String userId,@Header("token") String token,@Field("source") String source,
                                             @Field("remMobile") String mobile,@Field("beneficiary_id") String beneficiary_id);

    @FormUrlEncoded
    @POST("dmt/sendMoney")
    Call<TransferResponse> transferMoney(@Field("userId") String userId, @Header("token") String token, @Field("source") String source,
                                         @Field("remMobile") String mobile, @Field("remitterId") String remitterId,
                                         @Field("beneAccount") String beneAccount, @Field("txntype") String txnType,
                                         @Field("ifsccode") String ifsccode, @Field("amount") String amount,
                                         @Field("beneficiary_id") String beneficiary_id,
                                         @Field("latitude") String lat, @Field("longitude") String lon,@Field("txnpin") String pin);

}
