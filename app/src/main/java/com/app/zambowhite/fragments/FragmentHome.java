package com.app.zambowhite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.zambowhite.R;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private LinearLayout lnMobileRecharge,lnFastag,lnMoneyTransfer,lnAeps;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_home,container,false);
        init(view);
        lnMobileRecharge.setOnClickListener(this);
        lnFastag.setOnClickListener(this);
        lnMoneyTransfer.setOnClickListener(this);
        lnAeps.setOnClickListener(this);
        return view;
    }

    private void init(View view) {
        lnMobileRecharge=view.findViewById(R.id.ln_mobile_recharge);
        lnFastag=view.findViewById(R.id.ln_fastag);
        lnMoneyTransfer=view.findViewById(R.id.ln_money_transfer);
        lnAeps=view.findViewById(R.id.ln_aeps);
    }

    @Override
    public void onClick(View v) {
        if (v==lnMobileRecharge){
            SweetToast.success(getActivity(),"Available soon");
        }
        if (v==lnMobileRecharge){
            SweetToast.success(getActivity(),"Available soon");
        }
        if (v==lnMobileRecharge){
            SweetToast.success(getActivity(),"Available soon");
        }
        if (v==lnMobileRecharge){
            SweetToast.success(getActivity(),"Available soon");
        }
    }
}
