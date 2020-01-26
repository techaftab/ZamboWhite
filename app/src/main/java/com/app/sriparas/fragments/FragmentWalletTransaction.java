package com.app.sriparas.fragments;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sriparas.R;
import com.app.sriparas.activity.MainActivity;
import com.app.sriparas.adapter.WalletHistoryAdapter;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.models.HistoryModel;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.AllViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentWalletTransaction extends Fragment implements View.OnClickListener, WalletHistoryAdapter.WalletHistoryAdapterListener {

    private View view;
    private RecyclerView recyclerViewList;
    private RelativeLayout rlNoData;
    private ProgressBar progressBar;
    private EditText editTextDate;
    private Button btnDate;
    private UserData userData;

    private List<HistoryModel.DetailsHistory> listData;
    private WalletHistoryAdapter loadListAdapter;

    private AllViewModel allViewModel;

    public FragmentWalletTransaction(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_transaction,container,false);
        ((MainActivity)getActivity()).updateHeaderForBottom(getResources().getString(R.string.transaction));
        userData= PrefManager.getInstance(getActivity()).getUserData();
        allViewModel = new ViewModelProvider(this).get(AllViewModel.class);
        listData = new ArrayList<>();
        loadListAdapter = new WalletHistoryAdapter(getActivity(), listData, this);
        init(view);
        btnDate.setOnClickListener(this);
        editTextDate.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewList.setLayoutManager(mLayoutManager);
        recyclerViewList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewList.setAdapter(loadListAdapter);
        return view;
    }
    @Override
    public void onClick(View v) {
        if (v==btnDate||v==editTextDate){
            Configuration.showcalendar(editTextDate,getActivity());
            getHistory(userData.getId(),userData.getTxntoken(),editTextDate.getText().toString());
        }
    }

    private void init(View view) {
        recyclerViewList=view.findViewById(R.id.recyclerview_customer_history);
        rlNoData=view.findViewById(R.id.rl_no_transaction);
        progressBar=view.findViewById(R.id.progressbar_history);
        editTextDate=view.findViewById(R.id.edittext_select_date);
        btnDate=view.findViewById(R.id.btn_select_date);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        getHistory(userData.getId(),userData.getTxntoken(),formattedDate);
    }

    private void getHistory(String id, String txntoken, String formattedDate) {
        progressBar.setVisibility(View.VISIBLE);
        allViewModel.getHistory(id,txntoken,formattedDate,"APP").observe(this, historyResponse -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (historyResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(getActivity(),historyResponse.message);
                if (historyResponse.userdata.size()>0){
                    rlNoData.setVisibility(View.GONE);
                    recyclerViewList.setVisibility(View.VISIBLE);
                    listData.clear();
                    listData.addAll(historyResponse.userdata);
                    loadListAdapter.notifyDataSetChanged();
                }else {
                    rlNoData.setVisibility(View.VISIBLE);
                    recyclerViewList.setVisibility(View.GONE);
                }
            } else {
                rlNoData.setVisibility(View.VISIBLE);
                recyclerViewList.setVisibility(View.GONE);
                SweetToast.error(getActivity(),historyResponse.message);
            }
        });
    }

    @Override
    public void onContactSelected(HistoryModel.DetailsHistory contact) {

    }
}
