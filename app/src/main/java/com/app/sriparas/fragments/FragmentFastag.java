package com.app.sriparas.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.app.sriparas.R;
import com.app.sriparas.activity.ActivityFastag;
import com.app.sriparas.activity.MainActivity;
import com.app.sriparas.config.AppConfig;
import com.app.sriparas.config.AppController;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.HttpsTrustManager;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.FastagViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentFastag extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout rlBack,rlNext;
    EditText editTextMobile;

    private FragmentTransaction ft;
    private Fragment currentFragment;

    ProgressDialog progressDialog;

    FastagViewModel fastagViewModel;

    UserData userData;
    private String remitterId;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_fastag,container,false);
        ((MainActivity) Objects.requireNonNull(getContext())).updateHeader(getResources().getString(R.string.fastag));
        handler=new Handler();
        fastagViewModel = new ViewModelProvider(this).get(FastagViewModel.class);
        userData= PrefManager.getInstance(getActivity()).getUserData();
        init(view);
        rlBack.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        return view;
    }

    private void init(View view) {
        progressDialog=new ProgressDialog(getActivity());
        rlBack=view.findViewById(R.id.rl_back);
        rlNext=view.findViewById(R.id.rl_next);
        editTextMobile=view.findViewById(R.id.ed_mobile);
    }

    @Override
    public void onClick(View v) {
        if (v==rlBack){
            ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
            currentFragment = new FragmentHome();
            ft.replace(R.id.framelayout_main, currentFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        if (v==rlNext){
            String mobile=editTextMobile.getText().toString().trim();
            if (validateMobile(mobile)){
                getUserInfo(userData.getId(),userData.getTxntoken(),mobile);
            }
        }
    }
    private boolean validateMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            editTextMobile.setError(getResources().getString(R.string.enter_mobile));
            editTextMobile.requestFocus();
            SweetToast.error(getActivity(),"Please Enter Phone no");
            return false;
        }
        return true;
    }

    private void getUserInfo(String userId, String txntoken, String mobile) {
        String tag_string_req = "user_info";
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FASTTAG_USERINFO, response -> {
            Log.d(VolleyLog.TAG, "user_info Response: " + response);
            progressDialog.dismiss();
            try {
                JSONObject jsonObject1=new JSONObject(response);
                String status = jsonObject1.getString("status");
                String message=jsonObject1.getString("message");
                Log.d(VolleyLog.TAG,"status:"+status);
                switch (status) {
                    case "0":
                        //success
                        remitterId = jsonObject1.getJSONObject("remitter").getString("remitterId");
                       // handler.post(() -> getBeneficiary(mobile, userId, token));
                        goActivity(remitterId,true,mobile);
                        break;
                    case "2":
                        //doCustomerKyc();
                      /*  scrollView.setVisibility(View.VISIBLE);
                        floatAddNewBeneficiary.hide();
                        rlNoData.setVisibility(View.GONE);
                        recyclerViewBene.setVisibility(View.GONE);
                        rlList.setVisibility(View.GONE);*/
                        goActivity(remitterId,false,mobile);

                        break;
                    case "3":
                        getOtpKyc(mobile, userId, txntoken);
                        break;
                    default:
                        Toast.makeText(getActivity(),
                                message, Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(getActivity(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.REM_MOBILE, mobile);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token", txntoken);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void goActivity(String remitterId, boolean b, String mobile) {
        Intent intent = new Intent(getActivity(), ActivityFastag.class);
        intent.putExtra(Constant.FASTAG_STATUS,b);
        intent.putExtra(Constant.REMITTER_ID,remitterId);
        intent.putExtra(Constant.REMITTER_MOBILE,mobile);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void getOtpKyc(String mobile, String userId, String token) {
        String tag_string_req = "fastag_otp_kyc";
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FASTTAG_OTP_KYC, response -> {
            Log.d(VolleyLog.TAG, "fastag_otp_kyc Response: " + response);
            progressDialog.dismiss();
            try {
                JSONObject jsonObject1=new JSONObject(response);
                String status = jsonObject1.getString("status");
                String message=jsonObject1.getString("message");
                Log.d(VolleyLog.TAG,"status:"+status);
                if  (status.equals("0")) {
                    //success
                    final Dialog dialg=new Dialog(Objects.requireNonNull(getActivity()));
                    dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialg.setContentView(R.layout.layout_otp_bene);
                    dialg.setCanceledOnTouchOutside(false);
                    dialg.setCancelable(false);

                    RelativeLayout imgClose =  dialg.findViewById(R.id.rl_close);
                    ImageView imgFastag = dialg.findViewById(R.id.img_fastag);
                    imgFastag.setVisibility(View.VISIBLE);
                    TextView txtResendOtp=dialg.findViewById(R.id.txt_resend_sender_otp_bene);
                    TextView txtMessage=dialg.findViewById(R.id.txt_otp_sender_bene);
                    final EditText editTextOtp=dialg.findViewById(R.id.edittext_sender_otp_bene);
                    final Button btnVerify=dialg.findViewById(R.id.btn_verify_bene);

                    txtResendOtp.setOnClickListener(v -> {
                        if (Configuration.hasNetworkConnection(getActivity())){
                            resendOtpFastTag(mobile,userId,token);
                        }
                    });
                    btnVerify.setOnClickListener(v -> {
                        String otp=editTextOtp.getText().toString().trim();
                        if (otp.isEmpty()){
                            Configuration.openPopupUpDown(getActivity(),R.style.Dialod_UpDown,"error",
                                    "Enter otp sent to your mobile no");
                        }else {
                            otpKycFastagVerify(userId,mobile,otp,token,dialg);
                        }
                    });

                    imgClose.setOnClickListener(v -> dialg.dismiss());

                    dialg.show();
                    Window window = dialg.getWindow();
                    assert window != null;
                    window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    Toast.makeText(getActivity(),
                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "fastag_otp_kyc Error: " + error.getMessage());
            Toast.makeText(getActivity(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.REM_MOBILE, mobile);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void resendOtpFastTag(String mobile, String userId, String token) {
        String tag_string_req = "fastag_verify_kyc";
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FASTTAG_OTP_KYC, response -> {
            Log.d(VolleyLog.TAG, "fastag_verify_kyc Response: " + response);
            progressDialog.dismiss();
            try {
                JSONObject jsonObject1=new JSONObject(response);
                String status = jsonObject1.getString("status");
                String message=jsonObject1.getString("message");
                Log.d(VolleyLog.TAG,"status:"+status);
                if  (status.equals("0")) {
                    //success
                    SweetToast.success(getActivity(),"New Otp Sent to your mobile number");
                } else {
                    Toast.makeText(getActivity(),
                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(getActivity(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.REM_MOBILE, mobile);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void otpKycFastagVerify(String userId, String mobile, String otp, String token,
                                    Dialog dialg) {
        String tag_string_req = "fastag_verify_kyc";
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FASTTAG_OTP_VERIFY, response -> {
            Log.d(VolleyLog.TAG, "fastag_verify_kyc Response: " + response);
            progressDialog.dismiss();
            try {
                JSONObject jsonObject1=new JSONObject(response);
                String status = jsonObject1.getString("status");
                String message=jsonObject1.getString("message");
                Log.d(VolleyLog.TAG,"status:"+status);
                if  (status.equals("0")) {
                    //success
                    dialg.dismiss();
                    SweetToast.success(getActivity(),"Successfulley done KYC!");
                    getUserInfo(userData.getId(),userData.getTxntoken(),mobile);
                } else {
                    SweetToast.error(getActivity(),"Try again after sometime");
//                    Toast.makeText(getActivity(),
//                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(getActivity(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.REM_MOBILE, mobile);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.REM_OTP,otp);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}
