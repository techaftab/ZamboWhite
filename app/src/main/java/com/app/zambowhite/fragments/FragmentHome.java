package com.app.zambowhite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.zambowhite.R;
import com.app.zambowhite.activity.MainActivity;

import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private LinearLayout lnMobileRecharge,lnFastag,lnMoneyTransfer,lnAeps;
    private View view;

    FragmentTransaction ft;
    Fragment currentFragment;

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
        if (v==lnFastag){
           // SweetToast.success(getActivity(),"Available soon");
            if (getActivity()!=null) {
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_in, R.anim.left_out);
                currentFragment = new FragmentFastag();
                ft.replace(R.id.framelayout_main, currentFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
        if (v==lnMoneyTransfer){
            SweetToast.success(getActivity(),"Available soon");
        }
        if (v==lnAeps){
            SweetToast.success(getActivity(),"Available soon");
        }
    }
}
