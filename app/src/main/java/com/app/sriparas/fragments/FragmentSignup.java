package com.app.sriparas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.sriparas.R;
import com.app.sriparas.activity.LoginActivity;
import com.app.sriparas.config.Configuration;

import java.util.Objects;

public class FragmentSignup extends Fragment implements View.OnClickListener {

    View view;

    private EditText editTextFullName,editTextPhone,editTextEmail,editTextPan,
            editTextAadhaar,editTextCity,editTextState,editTextCompany,editTextPin,editTextDob,editTextReferral;
    private Button btnSignup;
    private ProgressBar progressBar;
    private RelativeLayout rlLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_signup,container,false);
        init(view);
        rlLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        return view;
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
            String state=editTextState.getText().toString();
            String company=editTextCompany.getText().toString();
            String pin=editTextPin.getText().toString();
            String dob=editTextDob.getText().toString();
            if (isValidated(referralCode,fullName,phone,email,pan,aadhaar,city,state,company,pin,dob)){

            }
        }
    }

    private boolean isValidated(String referralCode, String fullName, String phone, String email,
                                String pan, String aadhaar, String city, String state, String company, String pin, String dob) {
        return false;
    }
}
