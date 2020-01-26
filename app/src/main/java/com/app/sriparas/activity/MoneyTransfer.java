package com.app.sriparas.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.sriparas.R;
import com.app.sriparas.adapter.BeneMoneyAdapter;
import com.app.sriparas.config.AppConfig;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.config.WebService;
import com.app.sriparas.config.updateBalance;
import com.app.sriparas.models.BeneficiaryResponse;
import com.app.sriparas.models.GPSTracker;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.TransferViewModel;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class MoneyTransfer extends AppCompatActivity implements updateBalance,
        View.OnClickListener, BeneMoneyAdapter.BeneMoneyAdapterListner{

    private static final int REQUEST_LOCATION = 100;
    private ImageView imgBack,imgWallet;
    public TextView txtBalance,txtUserMobile;
    public ProgressBar progressBarBalance;
    ExpandableRelativeLayout expandableRelativeLayout;
    private LinearLayout lnRemitterDetail;
    ProgressBar linearProgress;
    private EditText editTextName;
    private EditText editTextAddress,editTextCity,editTextDistrict,editTextState,editTextPicode;
    private Button btnAddRemitter,btnAddNewBeneficiary,btnAddBeneIfNo;
    private RelativeLayout rlNoData,relativeLayoutrequestList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView requestList;
    //  private TextView txtResendOtp,txtVerify;
    WebService webService;
    UserData userData;
    //  private RelativeLayout rlOtpNewUser;

    List<BeneficiaryResponse.Beneficiary> listData;
    @SuppressLint("StaticFieldLeak")
    BeneMoneyAdapter loadListAdapter;

    TransferViewModel transferViewModel;

    String mobile="",remitterId="",userStatus="",bIfsc,bCode,bName;
    String bankNam,bankAccount="",bankIfsccode,bankBeneName;
    private ArrayList<String> bankcode;
    private ArrayList<String> bankname;
    private ArrayList<String> masterifsc;

    private GoogleApiClient googleApiClient;
    Double lat=null,lon = null;
    GPSTracker gpsTracker;

    Handler handler=new Handler();
    private String bankId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        initView();
        gpsTracker = new GPSTracker(MoneyTransfer.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MoneyTransfer.this);
        requestList.setLayoutManager(mLayoutManager);
        requestList.setItemAnimator(new DefaultItemAnimator());
        requestList.setAdapter(loadListAdapter);
        if(expandableRelativeLayout.isExpanded()) {
            expandableRelativeLayout.toggle();
        }
        imgBack.setOnClickListener(this);
        imgWallet.setOnClickListener(this);
        btnAddRemitter.setOnClickListener(this);
        btnAddBeneIfNo.setOnClickListener(this);
        btnAddNewBeneficiary.setOnClickListener(this);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateBalance(String walletBalance) {
        txtBalance.setText("Bal. \u20B9" + walletBalance);
        progressBarBalance.setVisibility(View.GONE);
        SplashActivity.savePreferences(Constant.BALANCE, walletBalance);
    }

    private void initView() {
        listData=new ArrayList<>();
        loadListAdapter = new BeneMoneyAdapter(MoneyTransfer.this, listData,this);
        mobile=getIntent().getStringExtra(Constant.REMITTER_MOBILE);
        remitterId=getIntent().getStringExtra(Constant.REMITTER_ID);
        userStatus=getIntent().getStringExtra(Constant.USER_STATUS);
        userData= PrefManager.getInstance(MoneyTransfer.this).getUserData();
        webService=new WebService((updateBalance)this);
        imgBack=findViewById(R.id.img_back_money_transfer);
        imgWallet=findViewById(R.id.img_wallet_money_transfer);
        txtUserMobile=findViewById(R.id.txt_user_mobile_dmt);
        txtBalance=findViewById(R.id.txt_balance_money_transfer);
        progressBarBalance=findViewById(R.id.progressbar_money_transfer);
        expandableRelativeLayout=findViewById(R.id.expandablelayout_money_transfer);
        linearProgress=findViewById(R.id.progressbar_dmt);
        lnRemitterDetail=findViewById(R.id.ln_remitter_details);
        txtUserMobile.setText("Mobile: "+mobile);

        rlNoData=findViewById(R.id.rl_no_beneficiary);
        requestList=findViewById(R.id.recyclerview_money_transfer);
        swipeRefreshLayout=findViewById(R.id.swiperefresh_money_transfer);
        btnAddNewBeneficiary=findViewById(R.id.btn_add_new_beneficiary);
        relativeLayoutrequestList=findViewById(R.id.relative_request_list);
        btnAddBeneIfNo=findViewById(R.id.btn_add_benef);

        btnAddRemitter=findViewById(R.id.btn_continue_remitter);

        editTextName=findViewById(R.id.edittext_name_remitter);
        editTextAddress=findViewById(R.id.edittext_address_fastag);
        editTextCity=findViewById(R.id.edittext_city_fastag);
        editTextDistrict=findViewById(R.id.edittext_district_fastag);
        editTextState=findViewById(R.id.edittext_state_fastag);
        editTextPicode=findViewById(R.id.edittext_pincode_fastag);

        webService.updateBalance(userData.getId(),userData.getTxntoken());

        loadView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
                    if (mobile.length()==10){
                        getRemitterBeneficiary(mobile,userData.getId(),userData.getTxntoken());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }
    @Override
    public void onClick(View v) {
        if (v == imgWallet) {
            if (expandableRelativeLayout.isExpanded())
                expandableRelativeLayout.toggle();
            else
                expandableRelativeLayout.toggle();
        }
        if (v==imgBack){
            finish();
        }
        if (v==btnAddRemitter){
            String name=editTextName.getText().toString().trim();
            String address=editTextAddress.getText().toString().trim();
            String city=editTextCity.getText().toString().trim();
            String district=editTextDistrict.getText().toString().trim();
            String state=editTextState.getText().toString().trim();
            String pincode=editTextPicode.getText().toString().trim();
            if (Configuration.hasNetworkConnection(MoneyTransfer.this)) {
                if (isValidate(mobile,name,address, city, district, state, pincode)) {
                    addRemitter(mobile,name,address,city,district,state,pincode,userData.getTxntoken(),userData.getId());
                }
            }else {
                Configuration.openPopupUpDown(MoneyTransfer.this,R.style.Dialod_UpDown,
                        "internetError",getResources().getString(R.string.no_internet));
            }
        }
        if (v==btnAddBeneIfNo||v==btnAddNewBeneficiary){
            openDialogForBeneficiary();

        }
    }

    private void loadView() {
        if (userStatus.equalsIgnoreCase("Allready")){
            lnRemitterDetail.setVisibility(View.GONE);
            getRemitterBeneficiary(mobile,userData.getId(),userData.getTxntoken());
        }else if (userStatus.equalsIgnoreCase("verify")){
            lnRemitterDetail.setVisibility(View.GONE);
            getOtpRemitter(mobile, userData.getId(),userData.getTxntoken());
        }else {
            lnRemitterDetail.setVisibility(View.VISIBLE);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void openDialogForBeneficiary() {
        final Dialog dialg=new Dialog(MoneyTransfer.this);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.layout_add_benef);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);

        TextView txtClose =  dialg.findViewById(R.id.txt_close);

        EditText editTextAccount=dialg.findViewById(R.id.edit_acc_benef);
        EditText ediBankIfsc=dialg.findViewById(R.id.edit_ifsc_benef);
        EditText editBeneName=dialg.findViewById(R.id.edit_name_benef);
        ProgressBar progressBar=dialg.findViewById(R.id.progress_bene);

        Button btnAdd=dialg.findViewById(R.id.btn_add_ben);
        AutoCompleteTextView bankName=dialg.findViewById(R.id.edit_benf_bank);
        TextView txtVerifyBene=dialg.findViewById(R.id.txt_verify);

        handler.post(() -> loadBankList(bankName,dialg));

        bankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ediBankIfsc.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        bankName.setOnItemClickListener((parent, view, position, id) -> {
            String bank =parent.getItemAtPosition(position).toString();
            for (int i = 0; i < bankname.size(); i++) {
                if (bankname.get(i).equals(bank)) {
                    bCode = bankcode.get(i);
                    bIfsc=masterifsc.get(i);
                    bankName.setText(bankname.get(i));
                    Log.d("TAG","autocomplete"+bankId);
                    if (!masterifsc.get(i).equalsIgnoreCase("NULL")){
                        ediBankIfsc.setText(masterifsc.get(i));
                    }else {
                        ediBankIfsc.setText("");
                    }
                    try{Configuration.hideKeyboardFrom(MoneyTransfer.this);}catch (Exception e){e.printStackTrace();}
                }
            }
            System.out.println("Bank code-->" + bank + "code---->" + bankId+" ifsc:>"+bIfsc);
        });
        bankName.setOnTouchListener((paramView, paramMotionEvent) -> {
            bankName.showDropDown();
            bankName.requestFocus();
            return false;
        });


        txtVerifyBene.setVisibility(View.GONE);

      /*  txtVerifyBene.setOnClickListener(v16 -> {
            bankAccount=editTextAccount.getText().toString();
            bankIfsccode=ediBankIfsc.getText().toString();
            bankNam=bankName.getText().toString();
            mobile=editTextMobile.getText().toString();
            String bankBeneName=editBeneName.getText().toString();
            if (bankNam.isEmpty()){
                bankName.setError("Enter bank name");
                SweetToast.error(MoneyTransfer.this,"Enter bank name");
            } else if (!bankname.contains(bankNam)){
                bankName.setError("Enter valid bank name");
                SweetToast.error(MoneyTransfer.this,"Enter valid bank name");
            } else if (bankAccount.isEmpty()){
                editTextAccount.setError("Enter account number");
                SweetToast.error(MoneyTransfer.this,"Enter account number");
            } else if (bankIfsccode.isEmpty()){
                ediBankIfsc.setError("Enter IFSC");
                SweetToast.error(MoneyTransfer.this,"Enter IFSC");
            } else if (Configuration.hasNetworkConnection(MoneyTransfer.this)){
                verifyBenefifiary(userData.getUserId(),bankBeneName,bankAccount,bankIfsccode,
                        mobile,bankId,editBeneName,dialg,progressBar);
            } else {
                SweetToast.error(MoneyTransfer.this,"Try after sometime");
            }
        });*/
        btnAdd.setOnClickListener(v15 -> {
            bankAccount=editTextAccount.getText().toString();
            bankIfsccode=ediBankIfsc.getText().toString();
            bankBeneName=editBeneName.getText().toString();
            bankNam=bankName.getText().toString();
            if (bankNam.isEmpty()){
                bankName.setError("Enter bank name");
                SweetToast.error(MoneyTransfer.this,"Enter bank name");
            }else if (!bankname.contains(bankNam)){
                bankName.setError("Enter valid bank name");
                SweetToast.error(MoneyTransfer.this,"Enter valid bank name");
            } else if (bankAccount.isEmpty()){
                editTextAccount.setError("Enter account number");
                SweetToast.error(MoneyTransfer.this,"Enter account number");
            } else if (bankIfsccode.isEmpty()){
                ediBankIfsc.setError("Enter IFSC");
                SweetToast.error(MoneyTransfer.this,"Enter IFSC");
            } else if (bankBeneName.isEmpty()){
                editBeneName.setError("Enter Benificiary Name");
                SweetToast.error(MoneyTransfer.this,"Enter Bene Name");
            } else if (Configuration.hasNetworkConnection(MoneyTransfer.this)){
                addNewBeneficiary(userData.getId(),bankBeneName,bankAccount,bankIfsccode,mobile,bankNam,bCode,dialg,progressBar);
            } else {
                SweetToast.error(MoneyTransfer.this,"Try after sometime");
            }
        });
        txtClose.setOnClickListener(v13 -> dialg.dismiss());
        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }
    private void loadBankList(AutoCompleteTextView bankName, Dialog dialg) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.MONEY_BANK,
                response -> {
                    try {
                        System.out.println("Bank response--->" + response);
                        JSONArray jsonArray=new JSONArray(response);
                        bankcode = new ArrayList<>();
                        bankname = new ArrayList<>();
                        masterifsc = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            bankcode.add(jsonObject1.getString("bankcode"));
                            bankname.add(jsonObject1.getString("bankname"));
                            masterifsc.add(jsonObject1.getString("masterifsc"));
                        }
                        ArrayAdapter<String> counryAdapter = new ArrayAdapter<>(MoneyTransfer.this, R.layout.spinner_item,
                                R.id.spinner_text,bankname);
                        bankName.setAdapter(counryAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        SweetToast.error(MoneyTransfer.this,"Try after sometime!");
                        dialg.dismiss();
                    } finally {
                    }

                }, error -> {
            dialg.dismiss();
            SweetToast.error(MoneyTransfer.this,"Try after sometime!");
        });

        RequestQueue requestQueue= Volley.newRequestQueue(MoneyTransfer.this);
        requestQueue.add(stringRequest);
    }

    private boolean isValidate(String mobile,String name, String address, String city, String district, String state, String pincode) {
        if (TextUtils.isEmpty(name)) {
            editTextName.setError(getResources().getString(R.string.enter_name));
            editTextName.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter name");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError(getResources().getString(R.string.enter_address));
            editTextAddress.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter address");
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            editTextCity.setError(getResources().getString(R.string.enter_city));
            editTextCity.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter city");
            return false;
        }
        if (TextUtils.isEmpty(district)) {
            editTextDistrict.setError(getResources().getString(R.string.enter_address));
            editTextDistrict.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter district");
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            editTextState.setError(getResources().getString(R.string.enter_state));
            editTextState.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(pincode)) {
            editTextPicode.setError(getResources().getString(R.string.enter_pincode));
            editTextPicode.requestFocus();
            SweetToast.error(MoneyTransfer.this,"Please enter pincode");
            return false;
        }
        return true;
    }


    private void showLoading(){
        linearProgress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void dismissLoading(){
        linearProgress.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
    private void addRemitter(String mobile,String name, String address, String city, String district, String state,
                             String pincode, String token, String userId) {
        showLoading();
        transferViewModel.addRemitter(mobile,name,address,city,district,state,pincode,userId,token,"APP").observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
                getOtpRemitter(mobile, userId, token);
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }

    private void getRemitterBeneficiary(String mobile, String userId, String token) {
        showLoading();
        transferViewModel.getRemitterBeneficiary(mobile,userId,token,"APP").observe(this, beneficiaryResponse -> {
            dismissLoading();
            if (beneficiaryResponse.status.equalsIgnoreCase("0")) {
                rlNoData.setVisibility(View.GONE);
                btnAddNewBeneficiary.setVisibility(View.VISIBLE);
                relativeLayoutrequestList.setVisibility(View.VISIBLE);
                requestList.setVisibility(View.VISIBLE);
                listData.clear();
                listData.addAll(beneficiaryResponse.beneficiary);
                loadListAdapter.notifyDataSetChanged();
            } else {
                relativeLayoutrequestList.setVisibility(View.VISIBLE);
                requestList.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
                btnAddNewBeneficiary.setVisibility(View.GONE);
                SweetToast.error(MoneyTransfer.this,beneficiaryResponse.message);
            }
        });
    }

    private void getOtpRemitter(String mobile, String userId, String token) {
        showLoading();
        transferViewModel.otpRemitter(mobile,userId,token,"APP").observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
                openDialogOtp(mobile,userId,token);
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }
    private void resendOtpFastTag(String mobile, String userId, String token) {
        showLoading();
        transferViewModel.otpRemitter(mobile,userId,token,"APP").observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }

    private void openDialogOtp(String mobile, String userId, String token) {
        final Dialog dialg=new Dialog(MoneyTransfer.this);
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
            if (Configuration.hasNetworkConnection(MoneyTransfer.this)){
                resendOtpFastTag(mobile,userId,token);
            }
        });
        btnVerify.setOnClickListener(v -> {
            String otp=editTextOtp.getText().toString().trim();
            if (otp.isEmpty()){
                Configuration.openPopupUpDown(MoneyTransfer.this,R.style.Dialod_UpDown,"error",
                        "Enter otp sent to your mobile no");
            }else {
                verifyRemitter(userId,mobile,otp,token,dialg);
            }
        });

        imgClose.setOnClickListener(v -> dialg.dismiss());

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    private void checkUser(String mobile, String userId, String token) {
        Configuration.hideKeyboardFrom(MoneyTransfer.this);
        showLoading();
        transferViewModel.checkUserTransfer(mobile,userId,token,"APP").observe(this, transferCheckResponse -> {
           dismissLoading();
            if (transferCheckResponse.status.equalsIgnoreCase("0")) {
                lnRemitterDetail.setVisibility(View.GONE);
                remitterId=transferCheckResponse.remitter.remitterId;
                getRemitterBeneficiary(mobile,userId,token);
            } else if (transferCheckResponse.status.equalsIgnoreCase("3")){
                lnRemitterDetail.setVisibility(View.GONE);
                remitterId=transferCheckResponse.remitter.remitterId;
                getOtpRemitter(mobile, userId, token);
            } else if (transferCheckResponse.status.equalsIgnoreCase("2")){
                lnRemitterDetail.setVisibility(View.VISIBLE);
            } else {
                SweetToast.error(MoneyTransfer.this,transferCheckResponse.message);
            }
        });
    }

    private void verifyRemitter(String userId, String mobile, String otp, String token, Dialog dialg) {
        showLoading();
        transferViewModel.verifyRemitter(mobile,otp,userId,token,"APP").observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
                checkUser(mobile,userId,token);
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }


    @Override
    public void onBeneficiarySelected(BeneficiaryResponse.Beneficiary beneficiary) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void sendMoney(String beneAccount, String beneName, String ifsccode,
                          String bank, String beneficiary_id, String amount,String txnType,
                          Dialog dialog,ProgressBar progressBar) {
        final Dialog dialg=new Dialog(MoneyTransfer.this);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.layout_dialog_confirmation);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);

        TextView txtMessage=dialg.findViewById(R.id.txt_dialog);
        TextView btnCancel=dialg.findViewById(R.id.btn_cancel_dialog);
        TextView btnContinue=dialg.findViewById(R.id.btn_continue_dialog);
        txtMessage.setText("Do you want to Transfer amount of amount to "+ beneName +" (A/C No." +beneAccount+")."+"?"+
                "please continue for transfer otherwise cancel.");
        btnCancel.setOnClickListener(v12 -> dialg.dismiss());
        btnContinue.setOnClickListener(v1 -> {
            // dialg.dismiss();
            Log.d("TAG","DIALOGSHOW");
            checkLatLong(dialg,beneAccount,beneName,ifsccode,amount,beneficiary_id,dialog,txnType,progressBar);
            // transferMoney(userData.getUserId(),mobile,beneAccount,beneName,ifsccode,amount,bank,beneficiary_id,dialog);
        });

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    private void checkLatLong(Dialog dialg, String beneAccount, String beneName, String ifsccode,
                              String amount,String beneficiary_id, Dialog dialog,
                              String txnType, ProgressBar progressBar) {
        if (ActivityCompat.checkSelfPermission(MoneyTransfer.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MoneyTransfer.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MoneyTransfer.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            final LocationManager manager = (LocationManager) MoneyTransfer.this.getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MoneyTransfer.this)) {
                    Log.e("TAG","Gps already enabled");
                    enableLoc();
                    lat=null;lon=null;
                }else{
                    Log.e("TAG","Gps already enabled");
                    if (gpsTracker.canGetLocation) {
                        lat = gpsTracker.getLatitude();
                        lon = gpsTracker.getLongitude();
                    }
                    if (validateRecharge(lat,lon)){
                        dialg.dismiss();
                        transferMoney(userData.getId(),mobile,beneAccount,beneName,
                                ifsccode,amount,beneficiary_id,lat,lon,dialog,progressBar,txnType);
                    }
                }
            }
        }
    }
    private void gpsCheck() {
        final LocationManager manager = (LocationManager) MoneyTransfer.this.getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MoneyTransfer.this)) {
            Log.e("TAG","Gps already enabled");
            enableLoc();
        }else{
            Log.e("TAG","Gps already enabled");
        }
    }

    private boolean validateRecharge(Double lat, Double lon) {
        if (lat==null||lon==null){
            gpsCheck();
            return false;
        }
        return true;
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(MoneyTransfer.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }
                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener((ConnectionResult connectionResult) -> {
                        Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                    }).build();

            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback((LocationSettingsResult result1) -> {
                final Status status = result1.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        status.startResolutionForResult(MoneyTransfer.this, REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_LOCATION&&resultCode==RESULT_OK){
            if (gpsTracker.canGetLocation) {
                lat = gpsTracker.getLatitude();
                lon = gpsTracker.getLongitude();
            }
        }
    }

    @Override
    public void deleteBeneficiary(String beneficiary_id) {
        showLoading();
        transferViewModel.deleteBeneficiary(userData.getId(),userData.getTxntoken(),"APP",mobile,beneficiary_id)
                .observe(this, transferResponse -> {
                    dismissLoading();
                    if (transferResponse.status.equalsIgnoreCase("0")) {
                        SweetToast.success(MoneyTransfer.this,transferResponse.message);
                        checkUser(mobile,userData.getId(),userData.getTxntoken());
                    } else {
                        SweetToast.error(MoneyTransfer.this,transferResponse.message);
                    }
                });
    }

    private void addNewBeneficiary(String userId, String bankBeneName, String bankAccount,
                                   String bankIfsccode, String mobile, String bankNam, String bankCode,
                                   Dialog dialg, ProgressBar progressBar) {
       showLoading();
        transferViewModel.addBeneficiary(userId,userData.getTxntoken(),"APP",mobile, mobile,bankBeneName,bankAccount,
                bankIfsccode,bankCode).observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
                dialg.dismiss();
                checkUser(mobile,userId,userData.getTxntoken());
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }

    private void transferMoney(String userId, String mobile, String beneAccount, String beneName,
                               String ifsccode, String amount, String beneficiary_id,
                               Double lat, Double lon, Dialog dialog, ProgressBar progressBar, String txnType) {
        showLoading();
        transferViewModel.transferMoney(userId,userData.getTxntoken(),"APP",mobile,remitterId,
                beneAccount,txnType,ifsccode,amount,beneficiary_id,
                String.valueOf(lat),String.valueOf(lon),Constant.PIN).observe(this, transferResponse -> {
            dismissLoading();
            if (transferResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(MoneyTransfer.this,transferResponse.message);
                dialog.dismiss();
                checkUser(mobile,userId,userData.getTxntoken());
            } else {
                SweetToast.error(MoneyTransfer.this,transferResponse.message);
            }
        });
    }


}
