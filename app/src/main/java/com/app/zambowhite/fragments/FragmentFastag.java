package com.app.zambowhite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.zambowhite.R;
import com.app.zambowhite.activity.MainActivity;

import java.util.Objects;

public class FragmentFastag extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout rlBack,rlNext;

    FragmentTransaction ft;
    Fragment currentFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_fastag,container,false);
        ((MainActivity) Objects.requireNonNull(getContext())).updateHeader(getResources().getString(R.string.fastag));
        init(view);
        rlBack.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        return view;
    }

    private void init(View view) {
        rlBack=view.findViewById(R.id.rl_back);
        rlNext=view.findViewById(R.id.rl_next);
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
        if (v==rlNext){}
    }
}
