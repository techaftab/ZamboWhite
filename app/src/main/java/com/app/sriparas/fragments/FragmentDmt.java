package com.app.sriparas.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.app.sriparas.R;
import com.app.sriparas.activity.MainActivity;
import com.app.sriparas.activity.MoneyTransfer;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.TransferViewModel;

import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentDmt extends Fragment implements View.OnClickListener {

    private View view;

    UserData userData;
    private RelativeLayout rlBack,rlNext;
    EditText editTextMobile;
    private FragmentTransaction ft;
    private Fragment currentFragment;
    private ProgressBar progressBar;

    TransferViewModel transferViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_dmt,container,false);
        ((MainActivity) Objects.requireNonNull(getContext())).updateHeader(getResources().getString(R.string.money_transfer));
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        init();
        userData= PrefManager.getInstance(getActivity()).getUserData();
        rlBack.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        return view;
    }

    private void init() {
        rlBack=view.findViewById(R.id.rl_back_dmt);
        rlNext=view.findViewById(R.id.rl_next_dmt);
        editTextMobile=view.findViewById(R.id.ed_mobile_dmt);
        progressBar=view.findViewById(R.id.progressbar_dmt);
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
                getUserInfo(mobile,userData.getId(),userData.getTxntoken());
            }
        }
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        Configuration.hideKeyboardFrom(Objects.requireNonNull(getActivity()));
        if (getActivity()!=null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
    private void dismissLoading(){
        progressBar.setVisibility(View.INVISIBLE);
        if (getActivity()!=null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    private void getUserInfo(String mobile, String userId, String token) {
        Configuration.hideKeyboardFrom(Objects.requireNonNull(getActivity()));
        showLoading();
        transferViewModel.checkUserTransfer(mobile,userId,token,"APP").observe(this, transferCheckResponse -> {
            dismissLoading();
            if (transferCheckResponse.status.equalsIgnoreCase("0")) {
              //  lnRemitterDetail.setVisibility(View.GONE);
                String remitterId=transferCheckResponse.remitter.remitterId;
              //  getRemitterBeneficiary(mobile,userId,token);
                goActivity(remitterId,mobile,"Allready");
            } else if (transferCheckResponse.status.equalsIgnoreCase("3")){
               // lnRemitterDetail.setVisibility(View.GONE);
                String remitterId=transferCheckResponse.remitter.remitterId;
               // getOtpRemitter(mobile, userId, token);
                goActivity(remitterId,mobile,"verify");
            } else if (transferCheckResponse.status.equalsIgnoreCase("2")){
               // lnRemitterDetail.setVisibility(View.VISIBLE);
                goActivity("",mobile,"new");
            } else {
                SweetToast.error(getActivity(),transferCheckResponse.message);
            }
        });
    }

    private void goActivity(String remitterId,String mobile, String status) {
        Intent intent=new Intent(getActivity(), MoneyTransfer.class);
        intent.putExtra(Constant.REMITTER_ID,remitterId);
        intent.putExtra(Constant.REMITTER_MOBILE,mobile);
        intent.putExtra(Constant.USER_STATUS,status);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

}
