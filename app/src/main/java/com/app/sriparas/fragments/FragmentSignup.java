package com.app.sriparas.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.sriparas.R;
import com.app.sriparas.activity.ActivityFastag;
import com.app.sriparas.activity.LoginActivity;
import com.app.sriparas.config.AppConfig;
import com.app.sriparas.config.AppController;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.HttpsTrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

import static com.android.volley.VolleyLog.TAG;

public class FragmentSignup extends Fragment implements View.OnClickListener {

    View view;

    private EditText editTextFullName,editTextPhone,editTextEmail,editTextPan,
            editTextAadhaar,editTextCity,editTextCompany,editTextPin,editTextDob,editTextReferral;
    private Spinner editTextState;
    private Button btnSignup;
    private ProgressBar progressBar;
    private RelativeLayout rlLogin;
    private ProgressDialog progressDialog;
    private ArrayList<String> stateName;
    private String state;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_signup,container,false);
        progressDialog=new ProgressDialog(getActivity());
        stateName=new ArrayList<>();
        init(view);
        rlLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        getState();
        return view;
    }

    private void getState() {
        Configuration.showDialog("Please wait...", progressDialog);
        progressDialog.setCanceledOnTouchOutside(false);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.GET_STATE,
                response -> {
                    progressDialog.dismiss();
                    try {
                        stateName.clear();
                        System.out.println("Operator response--->" + response);
                        JSONArray jsonArray = new JSONArray(response);
                        stateName.add(0, "Select State");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            stateName.add(jsonObject1.getString("stateName"));
                        }
                        ArrayAdapter<String> counryAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                                R.layout.spinner_item,R.id.spinner_text, stateName);
                        editTextState.setAdapter(counryAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SweetToast.error(getActivity(),"Try after somertime");
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    } finally {
                        progressDialog.dismiss();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    SweetToast.error(getActivity(),"Try after somertime");
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                });
        RequestQueue requestQueue= Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }

    private void init(View view) {
        editTextReferral=view.findViewById(R.id.ed_referral_code);
        editTextFullName=view.findViewById(R.id.ed_full_name);
        editTextPhone=view.findViewById(R.id.ed_phone_no);
        editTextEmail=view.findViewById(R.id.ed_email_id);
        editTextPan=view.findViewById(R.id.ed_pan);
        editTextAadhaar=view.findViewById(R.id.ed_aadhaar);
        editTextCity=view.findViewById(R.id.ed_city);
        editTextState=view.findViewById(R.id.ed_state);
        editTextCompany=view.findViewById(R.id.ed_company);
        editTextPin=view.findViewById(R.id.ed_pin);
        editTextDob=view.findViewById(R.id.ed_dob);
        btnSignup=view.findViewById(R.id.signup_button);
        progressBar=view.findViewById(R.id.signup_progressBar);
        rlLogin=view.findViewById(R.id.rl_login);
        editTextDob.setOnClickListener(this);

        editTextState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String operatorNa = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < stateName.size(); i++) {
                    if (stateName.get(i).equals(operatorNa)) {
                        state = stateName.get(i);
                    }
                }
                System.out.println(" Operator Name---->" + operatorNa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==editTextDob){
            Configuration.showcalendar(editTextDob,getActivity());
        }
        if (v==rlLogin){
            Intent intent=new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        if (v==btnSignup){
            String referralCode=editTextReferral.getText().toString();
            String fullName=editTextFullName.getText().toString();
            String phone=editTextPhone.getText().toString();
            String email=editTextEmail.getText().toString();
            String pan=editTextPan.getText().toString();
            String aadhaar=editTextAadhaar.getText().toString();
            String city=editTextCity.getText().toString();
           // String state=editTextState.getText().toString();
            String company=editTextCompany.getText().toString();
            String pin=editTextPin.getText().toString();
            String dob=editTextDob.getText().toString();
            if (isValidated(referralCode,fullName,phone,email,pan,aadhaar,city,state,company,pin,dob)){
                signup(referralCode,fullName,phone,email,pan,aadhaar,city,state,company,pin,dob);
            }
        }
    }

    private boolean
    isValidated(String referralCode, String fullName, String phone, String email,
                                String pan, String aadhaar, String city, String state,
                                String company, String pin, String dob) {
        if (referralCode.isEmpty()) {
            editTextReferral.setError(getResources().getString(R.string.enter_referral_code));
            editTextReferral.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_referral_code));
            return false;
        }
        if (fullName.isEmpty()) {
            editTextFullName.setError(getResources().getString(R.string.user_full_name));
            editTextFullName.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.user_full_name));
            return false;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError(getResources().getString(R.string.enter_mobile));
            editTextPhone.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_mobile));
            return false;
        }
        if (pan.isEmpty()) {
            editTextPan.setError(getResources().getString(R.string.enter_pan));
            editTextPan.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_pan));
            return false;
        }
        if (aadhaar.isEmpty()) {
            editTextAadhaar.setError(getResources().getString(R.string.enter_aadhaar));
            editTextAadhaar.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_aadhaar));
            return false;
        }
        if (dob.isEmpty()) {
            editTextDob.setError(getResources().getString(R.string.enter_dob));
            editTextDob.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_dob));
            return false;
        }
        if (city.isEmpty()) {
            editTextCity.setError(getResources().getString(R.string.enter_city));
            editTextCity.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_city));
            return false;
        }
        if (state.isEmpty()) {
            SweetToast.error(getActivity(),getResources().getString(R.string.select_state));
            return false;
        }
        if (company.isEmpty()) {
            editTextCompany.setError(getResources().getString(R.string.enter_company));
            editTextCompany.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_company));
            return false;
        }
        if (pin.isEmpty()) {
            editTextPin.setError(getResources().getString(R.string.enter_pincode));
            editTextPin.requestFocus();
            SweetToast.error(getActivity(),getResources().getString(R.string.enter_pincode));
            return false;
        }
        return true;
    }
    private void signup(String referralCode, String fullName, String phone,
                        String email, String pan, String aadhaar, String city,
                        String state, String company, String pin, String dob) {
        String tag_string_req = "add_bene";
        Configuration.showDialog("Please wait...",progressDialog);
        try{Configuration.hideKeyboardFrom(getActivity());}catch (Exception e){e.printStackTrace();}

        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.SIGNUP,
                response -> {
                    Log.d(TAG,"add_bene RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            SweetToast.success(getActivity(),"Successfully account created");
                            Intent intent=new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }else {
                            SweetToast.error(getActivity(),"Try after sometime");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    assert progressDialog != null;
                    progressDialog.dismiss();
                    Log.e("kyc_fastag ERROR","ERROR--->"+error.toString());
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Constant.REFERRAL_CODE,referralCode);
                params.put(Constant.FULLNAME,fullName);
                params.put(Constant.EMAIL,email);
                params.put(Constant.PHONE_NO,phone);
                params.put(Constant.PAN,pan);
                params.put(Constant.AADHAAR,aadhaar);
                params.put(Constant.CITY,city);
                params.put(Constant.STAT,state);
                params.put(Constant.COMPANY,company);
                params.put(Constant.PIN_NO,pin);
                params.put(Constant.DOB,dob);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }
}
