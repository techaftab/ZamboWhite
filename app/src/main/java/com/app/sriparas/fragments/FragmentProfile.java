package com.app.sriparas.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.sriparas.R;
import com.app.sriparas.activity.MainActivity;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.models.ProfileResponse;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.AllViewModel;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class FragmentProfile extends Fragment {

    private View view;
    public FragmentProfile(){}
    UserData userData;

    private CircleImageView imgProfile;
    private TextView txtName,txtEmail,txtMobile,txtUserType,txtKycStatus;
    private ProgressBar progressBar;
    AllViewModel allViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_profile,container,false);
       userData= PrefManager.getInstance(getActivity()).getUserData();
        allViewModel = new ViewModelProvider(this).get(AllViewModel.class);
        ((MainActivity)getActivity()).updateHeaderForBottom(getResources().getString(R.string.profile));
       init();
       return view;
    }

    private void init() {
        imgProfile=view.findViewById(R.id.img_profile);
        txtName=view.findViewById(R.id.txt_name_profile);
        txtEmail=view.findViewById(R.id.txt_email_profile);
        txtMobile=view.findViewById(R.id.txt_mobile_profile);
        txtUserType=view.findViewById(R.id.txt_user_type);
        txtKycStatus=view.findViewById(R.id.txt_kyc_status);
        progressBar=view.findViewById(R.id.progressbar_profile);
        getProfile(userData.getId(),userData.getTxntoken());
    }

    @SuppressLint("SetTextI18n")
    private void getProfile(String id, String txntoken) {
        progressBar.setVisibility(View.VISIBLE);
        allViewModel.getProfile(id,txntoken,"APP").observe(this, profileResponse -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (profileResponse.status.equalsIgnoreCase("0")) {
               // SweetToast.success(getActivity(),profileResponse.message);
                loadData(profileResponse);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                SweetToast.error(getActivity(),profileResponse.message);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadData(ProfileResponse profileResponse) {
        txtName.setText(profileResponse.getUserdata().getFullName());
        txtEmail.setText(profileResponse.getUserdata().getEmail());
        txtMobile.setText(profileResponse.getUserdata().getMobile());
        if (profileResponse.getUserdata().getUsertype().equalsIgnoreCase("R")) {
            txtUserType.setText("Retailer");
        }else if (profileResponse.getUserdata().getUsertype().equalsIgnoreCase("D")) {
            txtUserType.setText("Distribitor");
        }
    }
}
