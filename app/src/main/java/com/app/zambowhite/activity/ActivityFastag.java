package com.app.zambowhite.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.zambowhite.R;
import com.app.zambowhite.adapter.FastagAdapter;
import com.app.zambowhite.config.AppController;
import com.app.zambowhite.config.Configuration;
import com.app.zambowhite.config.Constant;
import com.app.zambowhite.config.PrefManager;
import com.app.zambowhite.models.FastagList;
import com.app.zambowhite.models.GPSTracker;
import com.app.zambowhite.models.UserData;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.hasnat.sweettoast.SweetToast;

import static com.android.volley.VolleyLog.TAG;

public class ActivityFastag extends AppCompatActivity implements View.OnClickListener,
        FastagAdapter.FastagAdapterListner{

    private static final String TAG = ActivityFastag.class.getSimpleName();
    private ImageView imgBack,imgWallet;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtBalance;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBarBalance;
    ExpandableRelativeLayout expandableRelativeLayout;
    private Button btnKyc,btnAddNew,btnAddBeneficiary;
    private FloatingActionButton floatAddNewBeneficiary;
    private EditText editTextAddress,editTextCity,editTextDistrict,editTextState,editTextPicode,editTextBeneAccount;
    AutoCompleteTextView autoTextBeneBank;
    private RecyclerView recyclerViewBene;
    RelativeLayout rlList;
    private RelativeLayout rlNoData,rlFastagList;
    private ScrollView scrollView;
    ViewGroup hiddenGroup;
    UserData userData;
    boolean fastagStatus;
    private static List<FastagList> listData;
    @SuppressLint("StaticFieldLeak")
    private static FastagAdapter loadListAdapter;
    Handler handler=new Handler();
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    GPSTracker gpsTracker;
    Double lat=null,lon = null;
    String remitterId="",mobile="";

    String paymentAmount="",beneId="",beneAcc="";
    Dialog dialog;


    private ProgressDialog progressDialog;
    private ArrayList<String> bankcode;
    private ArrayList<String> bankname;
    private ArrayList<String> ftg_ifsc;
    private String bkCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastag);

        gpsTracker = new GPSTracker(ActivityFastag.this);
        userData= PrefManager.getInstance(ActivityFastag.this).getUserData();
        Log.e(TAG,"USER TYPE--->"+getIntent().getStringExtra(Constant.USER_TYPE));
        initView();
        if(expandableRelativeLayout.isExpanded()) {
            expandableRelativeLayout.toggle();
        }
        imgWallet.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btnKyc.setOnClickListener(this);
        btnAddNew.setOnClickListener(this);
        btnAddBeneficiary.setOnClickListener(this);
        floatAddNewBeneficiary.setOnClickListener(this);

        if (Configuration.hasNetworkConnection(ActivityFastag.this)) {
         //   handler.post(() -> WebService.getBalance(userData.getId()));
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        editTextMobile=findViewById(R.id.edittext_user_mobile_fastag);
        rlFastagList=findViewById(R.id.rl_fastag_ratail);
        rlList=findViewById(R.id.rl_fastag_list);
        floatAddNewBeneficiary=findViewById(R.id.floating_add_new_beneficiary);
        hiddenGroup=findViewById(R.id.rl_addbene_fastag);
        imgBack=findViewById(R.id.img_back_fasttag);
        imgWallet=findViewById(R.id.wallet_fasttag);
        progressDialog=new ProgressDialog(ActivityFastag.this);
        expandableRelativeLayout=findViewById(R.id.expandablelayout);
        txtBalance=findViewById(R.id.txt_balance_fasttag);
        progressBarBalance=findViewById(R.id.progressbar_fasttag);
        editTextAddress=findViewById(R.id.edittext_address_fastag);
        editTextCity=findViewById(R.id.edittext_city_fastag);
        editTextDistrict=findViewById(R.id.edittext_district_fastag);
        editTextState=findViewById(R.id.edittext_state_fastag);
        editTextPicode=findViewById(R.id.edittext_pincode_fastag);
        editTextBeneAccount=findViewById(R.id.edittext_account_fastag);
        autoTextBeneBank=findViewById(R.id.autotxt_bank_fastag);
        btnAddNew=findViewById(R.id.btn_add_benef_fastag);
        btnAddBeneficiary=findViewById(R.id.btn_add_fastag);
        btnKyc=findViewById(R.id.btn_continue_fastag);
        recyclerViewBene=findViewById(R.id.recylerview_bene_fastag);
        rlNoData=findViewById(R.id.rl_no_beneficiary_fastag);
        scrollView=findViewById(R.id.scrollview_fastag);
        fastagStatus=getIntent().getBooleanExtra(Constant.FASTAG_STATUS,false);

        listData = new ArrayList<>();
        loadListAdapter = new FastagAdapter(ActivityFastag.this, listData, this);
        if (fastagStatus) {
            remitterId=getIntent().getStringExtra(Constant.REMITTER_ID);
            scrollView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
            recyclerViewBene.setVisibility(View.VISIBLE);
            rlList.setVisibility(View.VISIBLE);
            floatAddNewBeneficiary.show();
            handler.post(() -> getBeneficiary(userData.getMobile(),userData.getUserId(),userData.getToken()));
        } else {
            if (userData.getuType().equalsIgnoreCase("CS")){
                scrollView.setVisibility(View.VISIBLE);
                floatAddNewBeneficiary.hide();
                rlNoData.setVisibility(View.GONE);
                recyclerViewBene.setVisibility(View.GONE);
                rlList.setVisibility(View.GONE);
            } else {
                rlFastagList.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                floatAddNewBeneficiary.hide();
                rlNoData.setVisibility(View.GONE);
                recyclerViewBene.setVisibility(View.GONE);
                rlList.setVisibility(View.GONE);
            }

        }
        editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scrollView.setVisibility(View.GONE);
                floatAddNewBeneficiary.hide();
                rlNoData.setVisibility(View.GONE);
                recyclerViewBene.setVisibility(View.GONE);
                rlList.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile =editTextMobile.getText().toString().trim();
                if (mobile.length()==10){
                    checkUserFastag(mobile,userData.getUserId(),userData.getToken(),userData.getuType());
                }
            }
        });

        autoTextBeneBank.setOnItemClickListener((parent, view, position, id) -> {
            String bank =parent.getItemAtPosition(position).toString();
            for (int i = 0; i < bankname.size(); i++) {
                if (bankname.get(i).equals(bank)) {
                    bkCode = bankcode.get(i);
                    //   bkName = bankname.get(i);
                    autoTextBeneBank.setText(bankname.get(i));
                    try{Configuration.hideKeyboardFrom(ActivityFastag.this);}catch (Exception e){e.printStackTrace();}
                }
            }
            System.out.println("Bank code-->" + bank + "code---->" + bkCode);
        });
        autoTextBeneBank.setOnTouchListener((paramView, paramMotionEvent) -> {
            // TODO Auto-generated method stub
            autoTextBeneBank.showDropDown();
            autoTextBeneBank.requestFocus();
            return false;
        });
    }
    @Override
    public void onClick(View v) {
        if (v == imgWallet) {
            if (expandableRelativeLayout.isExpanded())
                expandableRelativeLayout.toggle();
            else
                expandableRelativeLayout.toggle();
        }
        if (v == imgBack) {
            if (isPanelShown()){
                closeGroup();
            }else {
                finish();
            }
        }
        if (v == btnKyc) {
            String address=editTextAddress.getText().toString().trim();
            String city=editTextCity.getText().toString().trim();
            String district=editTextDistrict.getText().toString().trim();
            String state=editTextState.getText().toString().trim();
            String pincode=editTextPicode.getText().toString().trim();
            if (Configuration.hasNetworkConnection(ActivityFastag.this)) {
                if (isValidate(address, city, district, state, pincode)) {
                    if (userData.getuType().equalsIgnoreCase("CS")) {
                        handler.post(() -> doKyc(userData.getMobile(), userData.getName(), address, city, district, state, pincode,
                                userData.getUserId(), userData.getToken()));
                    }else {
                        if (!TextUtils.isEmpty(editTextMobile.getText().toString())) {
                            handler.post(() -> doKyc(mobile, userData.getName(), address, city, district, state, pincode,
                                    userData.getUserId(), userData.getToken()));
                        }else {
                            SweetToast.error(ActivityFastag.this,"Enter mobile number");
                        }
                    }
                }
            }else {
                Configuration.openPopupUpDown(ActivityFastag.this,R.style.Dialod_UpDown,
                        "internetError",getResources().getString(R.string.no_internet));
            }
        }
        if (v==btnAddNew){
            showGroup();
        }
        if (v==btnAddBeneficiary){
            String account=editTextBeneAccount.getText().toString();
            String bank=autoTextBeneBank.getText().toString();
            if (Configuration.hasNetworkConnection(ActivityFastag.this)) {
                if (validateBene(account, bank)) {
                    if (userData.getuType().equalsIgnoreCase("CS")) {
                        handler.post(() -> addBeneficiary(userData.getMobile(), account, bkCode, userData.getUserId(), userData.getToken()));
                    }else {
                        if (!TextUtils.isEmpty(editTextMobile.getText().toString())) {
                            handler.post(() -> addBeneficiary(mobile, account, bkCode, userData.getUserId(), userData.getToken()));
                        }else {
                            SweetToast.error(ActivityFastag.this,"Enter mobile number");
                        }
                    }
                }
            } else {
                Configuration.openPopupUpDown(ActivityFastag.this,R.style.Dialod_UpDown,
                        "internetError",getResources().getString(R.string.no_internet));
            }
        }
        if (v==floatAddNewBeneficiary){
            showGroup();
        }
    }

    private void closeGroup() {
        editTextBeneAccount.setText("");
        autoTextBeneBank.setText("");
        Animation bottomDown = AnimationUtils.loadAnimation(ActivityFastag.this,
                R.anim.slide_bottom_dialog);
        hiddenGroup.startAnimation(bottomDown);
        hiddenGroup.setVisibility(View.GONE);
        // scrollView.setVisibility(View.VISIBLE);
        if (userData.getuType().equalsIgnoreCase("CS")) {
            getBeneficiary(userData.getMobile(), userData.getUserId(), userData.getToken());
        }else {
            if (!editTextMobile.getText().toString().isEmpty()) {
                getBeneficiary(editTextMobile.getText().toString(), userData.getUserId(), userData.getToken());
            }
        }
    }




    private void showGroup() {
        loadBankList();
        if (!isPanelShown()) {
            Animation bottomUp = AnimationUtils.loadAnimation(ActivityFastag.this,
                    R.anim.slide_up_dialog);
            hiddenGroup.startAnimation(bottomUp);
            hiddenGroup.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            rlList.setVisibility(View.GONE);
        }
    }

    private boolean isPanelShown() {
        return hiddenGroup.getVisibility() == View.VISIBLE;
    }

    private boolean validateBene(String account, String bank) {
        if (TextUtils.isEmpty(account)) {
            editTextBeneAccount.setError(getResources().getString(R.string.enter_account));
            editTextBeneAccount.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter account number");
            return false;
        }
        if (TextUtils.isEmpty(bank)) {
            autoTextBeneBank.setError(getResources().getString(R.string.enter_bank));
            autoTextBeneBank.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter bank name");
            return false;
        }
        if (!bankname.contains(bank)){
            autoTextBeneBank.setError(getResources().getString(R.string.enter_correct_bank));
            autoTextBeneBank.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter valid bank name");
            return false;
        }
        return true;
    }

    private boolean isValidate(String address, String city, String district, String state, String pincode) {
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError(getResources().getString(R.string.enter_address));
            editTextAddress.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter address");
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            editTextCity.setError(getResources().getString(R.string.enter_city));
            editTextCity.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter city");
            return false;
        }
        if (TextUtils.isEmpty(district)) {
            editTextDistrict.setError(getResources().getString(R.string.enter_address));
            editTextDistrict.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter district");
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            editTextState.setError(getResources().getString(R.string.enter_state));
            editTextState.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter state");
            return false;
        }
        if (TextUtils.isEmpty(pincode)) {
            editTextPicode.setError(getResources().getString(R.string.enter_pincode));
            editTextPicode.requestFocus();
            SweetToast.error(ActivityFastag.this,"Please enter pincode");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isPanelShown()){
            closeGroup();
        }else {
            finish();
        }
    }

    private void checkUserFastag(String mobile, String userId, String token, String userType) {
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
                        handler.post(() -> getBeneficiary(mobile, userId, token));
                        break;
                    case "2":
                        //doCustomerKyc();
                        scrollView.setVisibility(View.VISIBLE);
                        floatAddNewBeneficiary.hide();
                        rlNoData.setVisibility(View.GONE);
                        recyclerViewBene.setVisibility(View.GONE);
                        rlList.setVisibility(View.GONE);

                        break;
                    case "3":
                        getOtpKyc(mobile, userId, token);
                        break;
                    default:
                        Toast.makeText(ActivityFastag.this,
                                message, Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(ActivityFastag.this,
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

    private void loadBankList() {
        Configuration.showDialog("Please wait...", progressDialog);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.FASTAG_BANK,
                response -> {

                    try {

                        System.out.println("Fastag response--->" + response);
                        JSONArray jsonArray=new JSONArray(response);

                        bankcode = new ArrayList<>();
                        bankname = new ArrayList<>();
                        ftg_ifsc = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            bankcode.add(jsonObject1.getString("bankcode"));
                            bankname.add(jsonObject1.getString("bankname"));
                            ftg_ifsc.add(jsonObject1.getString("ftg_ifsc"));
                        }
                        ArrayAdapter<String> counryAdapter = new ArrayAdapter<>(ActivityFastag.this, R.layout.spinner_item,
                                R.id.spinner_text,bankname);
                        autoTextBeneBank.setAdapter(counryAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }

                }, error -> {

            progressDialog.dismiss();
            Toast.makeText(ActivityFastag.this, "Try again", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue= Volley.newRequestQueue(ActivityFastag.this);
        requestQueue.add(stringRequest);
    }

    private void doKyc(String mobile, String name, String address, String city, String district,
                       String state, String pincode, String userId, String token) {
        String tag_string_req = "kyc_fastag";
        Configuration.showDialog("Please wait...",progressDialog);
        try{Configuration.hideKeyboardFrom(ActivityFastag.this);}catch (Exception e){e.printStackTrace();}

        HttpsTrustManager.allowAllSSL();
        Log.e(TAG,"MOBILE--->"+mobile);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.KYC_FASTAG,
                response -> {
                    Log.d(TAG,"kyc_fastag RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            SweetToast.success(ActivityFastag.this,getResources().getString(R.string.enter_otp));
                            getOtpKyc(mobile,userId,token);
                        }else {
                            SweetToast.error(ActivityFastag.this,"Try after sometime");
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
                params.put(Constant.REM_MOBILE,mobile);
                params.put(Constant.REM_NAME,name);
                params.put(Constant.ADDRESS,address);
                params.put(Constant.CIT,city);
                params.put(Constant.DISTRICT,district);
                params.put(Constant.STAT,state);
                params.put(Constant.PINCOD,pincode);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    private void addBeneficiary(String mobile, String account, String bank, String userId, String token) {
        String tag_string_req = "add_bene";
        Configuration.showDialog("Please wait...",progressDialog);
        try{Configuration.hideKeyboardFrom(ActivityFastag.this);}catch (Exception e){e.printStackTrace();}

        HttpsTrustManager.allowAllSSL();
        Log.e(TAG,"MOBILE--->"+mobile);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.ADD_BENEFICIARY_FASTAG,
                response -> {
                    Log.d(TAG,"add_bene RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            SweetToast.success(ActivityFastag.this,"Successfully added new beneficiary!");
                            getBeneficiary(mobile,userId,token);
                        }else {
                            SweetToast.error(ActivityFastag.this,"Try after sometime");
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
                params.put(Constant.REM_MOBILE,mobile);
                params.put(Constant.BENE_ACC,account);
                params.put(Constant.BANK_CODE,bank);
                params.put(Constant.USER_ID,userId);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
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
                    final Dialog dialg=new Dialog(ActivityFastag.this);
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
                        if (Configuration.hasNetworkConnection(ActivityFastag.this)){
                            resendOtpFastTag(mobile,userId,token);
                        }
                    });
                    btnVerify.setOnClickListener(v -> {
                        String otp=editTextOtp.getText().toString().trim();
                        if (otp.isEmpty()){
                            Configuration.openPopupUpDown(ActivityFastag.this,R.style.Dialod_UpDown,"error",
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
                    Toast.makeText(ActivityFastag.this,
                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "fastag_otp_kyc Error: " + error.getMessage());
            Toast.makeText(ActivityFastag.this,
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
                    SweetToast.success(ActivityFastag.this,"New Otp Sent to your mobile number");
                } else {
                    Toast.makeText(ActivityFastag.this,
                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(ActivityFastag.this,
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
                    fastagStatus=true;
                    SweetToast.success(ActivityFastag.this,"Successfulley done KYC!");
                    checkUserFastag(mobile,userId,token,userData.getuType());
                } else {
                    SweetToast.error(ActivityFastag.this,"Try again after sometime");
//                    Toast.makeText(getActivity(),
//                            message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e(VolleyLog.TAG, "user_info Error: " + error.getMessage());
            Toast.makeText(ActivityFastag.this,
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

    private void getBeneficiary(String mobile, String userId, String token) {
        String tag_string_req = "get_beneficiary";
        Configuration.showDialog("Please wait...",progressDialog);
        try{Configuration.hideKeyboardFrom(ActivityFastag.this);}catch (Exception e){e.printStackTrace();}
        hiddenGroup.setVisibility(View.GONE);
        HttpsTrustManager.allowAllSSL();
        Log.e(TAG,"MOBILE--->"+mobile);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.GET_BENE_FASTAG,
                response -> {
                    Log.d(TAG,"VIEW RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            // JSONObject jsonObje=jsonObject.getJSONObject("data");
                            //  SweetToast.success(ActivityFastag.this,"Beneficiary Found!");
                            JSONArray jsonArray=jsonObject.getJSONArray("beneficiary");
                            rlList.setVisibility(View.VISIBLE);
                            recyclerViewBene.setVisibility(View.VISIBLE);
                            floatAddNewBeneficiary.show();
                            scrollView.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.GONE);
                            recyclerViewBene.setVisibility(View.VISIBLE);

                            List<FastagList> items = new Gson().fromJson(jsonArray.toString(),
                                    new TypeToken<List<FastagList>>(){}.getType());

                            listData.clear();
                            listData.addAll(items);
                            loadListAdapter.notifyDataSetChanged();

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityFastag.this);
                            recyclerViewBene.setLayoutManager(mLayoutManager);
                            recyclerViewBene.setItemAnimator(new DefaultItemAnimator());
                            recyclerViewBene.setAdapter(loadListAdapter);
                        }else{
                            rlList.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                            floatAddNewBeneficiary.hide();
                            recyclerViewBene.setVisibility(View.GONE);
                            scrollView.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    assert progressDialog != null;
                    progressDialog.dismiss();
                    Log.e("PASS ERROR","ERROR--->"+error.toString());
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Constant.USER_ID,userId);
                params.put(Constant.REM_MOBILE,mobile);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }


    @Override
    public void onFastagAdapterSelcted(FastagList fastagList) {

    }

    @Override
    public void rechargeFastag(int pos, String beneficiary_id, String beneAccount, String amount, Dialog dialg) {
        beneId=beneficiary_id;beneAcc=beneAccount;
        dialog=dialg;
        if (ActivityCompat.checkSelfPermission(ActivityFastag.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityFastag.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityFastag.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
//            lat=null;lon=null;
            final LocationManager manager = (LocationManager) ActivityFastag.this.getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(ActivityFastag.this)) {
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
                        if (userData.getuType().equalsIgnoreCase("CS")) {
                            doRechargeFastag(pos, userData.getMobile(), remitterId, beneficiary_id, beneAccount, amount,
                                    lat, lon, userData.getUserId(), userData.getToken(), dialg);
                        }else {
                            doRechargeFastag(pos, mobile, remitterId, beneficiary_id, beneAccount, amount,
                                    lat, lon, userData.getUserId(), userData.getToken(), dialg);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void lowBalanceRecharge(int pos, String beneficiary_id, String beneAccount,String payAmount, String txnAmount,
                                   String pgcharge, Dialog dialg) {
        beneId=beneficiary_id;beneAcc=beneAccount;
        dialog=dialg;
        if (ActivityCompat.checkSelfPermission(ActivityFastag.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityFastag.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityFastag.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            final LocationManager manager = (LocationManager) ActivityFastag.this.getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(ActivityFastag.this)) {
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
                        if (userData.getuType().equalsIgnoreCase("CS")) {
                            lowBalRechargeFastag(pos, userData.getMobile(), remitterId, beneficiary_id, beneAccount, payAmount, txnAmount, pgcharge,
                                    lat, lon, userData.getUserId(), userData.getToken(), dialg);
                        }else {
                            if (!TextUtils.isEmpty(editTextMobile.getText().toString())) {
                                lowBalRechargeFastag(pos, mobile, remitterId, beneficiary_id, beneAccount, payAmount, txnAmount, pgcharge,
                                        lat, lon, userData.getUserId(), userData.getToken(), dialg);
                            }else {
                                SweetToast.error(ActivityFastag.this,"Enter mobile number");
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean validateRecharge(Double lat, Double lon) {
        if (lat==null||lon==null){
            gpsCheck();
            // SweetToast.error(ActivityFastag.this,"NO Location");
            return false;
        }
        return true;
    }

    private void gpsCheck() {
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) ActivityFastag.this.getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(ActivityFastag.this)) {
            Log.e("TAG","Gps already enabled");
            enableLoc();
            // lat=null;lon=null;
        }else{
            Log.e("TAG","Gps already enabled");
        }
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
            googleApiClient = new GoogleApiClient.Builder(ActivityFastag.this)
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
                        status.startResolutionForResult(ActivityFastag.this, REQUEST_LOCATION);
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

    private void doRechargeFastag(int pos, String mobile, String remitterId, String beneficiary_id,
                                  String beneAccount, String amount, Double lat, Double lon,
                                  String userId, String token, Dialog dialg) {
        float balance= Float.valueOf(SplashActivity.getPreferences(Constant.BALANCE,""));
        if (!TextUtils.isEmpty(remitterId)){
            if (userData.getuType().equalsIgnoreCase("CS")){
                recharge(mobile,remitterId,beneficiary_id,beneAccount,amount,lat,lon,userId,token,dialg);
            }else {
                if (Float.valueOf(amount)>balance){
                    SweetToast.error(ActivityFastag.this,"Your wallet balance is low!");
                } else {
                    recharge(mobile,remitterId,beneficiary_id,beneAccount,amount,lat,lon,userId,token,dialg);
                }
            }
        }
    }
    private void lowBalRechargeFastag(int pos, String mobile, String remitterId, String beneficiary_id,
                                      String beneAccount,String payAmt, String txnAmount, String pgcharge, Double lat, Double lon,
                                      String userId, String token, Dialog dialg) {
        paymentAmount=payAmt;
        float balance= Float.valueOf(SplashActivity.getPreferences(Constant.BALANCE,""));
        float paymentTotal=Float.valueOf(txnAmount)+Float.valueOf(pgcharge);
        float availableBalance=balance-100;
        startPayment(userData.getName(),userData.getEmail(),mobile, String.valueOf(paymentTotal));
//        if (paymentTotal > availableBalance) {
//            float amt;
//            if (availableBalance < paymentTotal) {
//                amt = paymentTotal - availableBalance;
//                SweetToast.success(ActivityFastag.this, "1  "+String.valueOf(amt));
//
//            }
//        }else {
//            SweetToast.success(ActivityFastag.this,"2  "+ String.valueOf(payAmt));
//           // SweetToast.success(ActivityFastag.this, String.valueOf(txnAmount));
//           // recharge(mobile,remitterId,beneficiary_id,beneAccount,txnAmount,lat,lon,userId,token,dialg);
//        }

    }




    private void recharge(String mobile, String remitterId, String beneficiary_id, String beneAccount,
                          String amount, Double lat, Double lon, String userId, String token, Dialog dialg) {
        String tag_string_req = "recharge";
        Configuration.showDialog("Please wait...",progressDialog);
        try{Configuration.hideKeyboardFrom(ActivityFastag.this);}catch (Exception e){e.printStackTrace();}
        HttpsTrustManager.allowAllSSL();
        Log.e(TAG,"MOBILE--->"+mobile);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.RECHARGE_FASTAG,
                response -> {
                    Log.d(TAG,"recharge RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            handler.post(() -> WebService.getBalance(userId));
                            SweetToast.success(ActivityFastag.this,"Recharge successfully done!");
                            if (dialg!=null){
                                dialg.dismiss();
                            }
                            openPopup(jsonObject.getString("message"),"S",jsonObject.getString("tid"),amount);

                        }else{
                            openPopup(jsonObject.getString("message"),"F","Not Available",amount);
                            SweetToast.error(ActivityFastag.this,"Try again after sometime!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    assert progressDialog != null;
                    progressDialog.dismiss();
                    Log.e("recharge ERROR","ERROR--->"+error.toString());
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Constant.REM_MOBILE,mobile);
                params.put(Constant.REMITTER_ID,remitterId);
                params.put(Constant.BENE_ID,beneficiary_id);
                params.put(Constant.BENE_ACC,beneAccount);
                params.put(Constant.AMOUNT,amount);
                params.put(Constant.LATTITUDE,String.valueOf(lat));
                params.put(Constant.LONGITUDE,String.valueOf(lon));
                params.put(Constant.USER_ID,userId);
                params.put(Constant.TXT_PIN,Constant.PIN);
                params.put(Constant.SOURCE,"APP");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    @SuppressLint("SetTextI18n")
    private void openPopup( String message, final String type, String txtid,String amount) {
        final Dialog dialg=new Dialog(ActivityFastag.this);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.popup_recharge);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);
        ImageView imageView =  dialg.findViewById(R.id.img_status_recharge);
        TextView txtStatus=dialg.findViewById(R.id.txt_status);
        if (type.equalsIgnoreCase("S")){
            imageView.setImageResource(R.drawable.success);
            txtStatus.setText("Status : Success");

        }else if (type.equalsIgnoreCase("P")){
            imageView.setImageResource(R.drawable.pending);
            txtStatus.setText("Status : Pending");
        }else {
            imageView.setImageResource(R.drawable.failed);
            txtStatus.setText("Status : Failed");
        }
        TextView txtTransactionId=dialg.findViewById(R.id.txt_status_recharge);
        Button btnOk= dialg.findViewById(R.id.btn_okay);
        txtTransactionId.setText(message);

        btnOk.setOnClickListener(v -> {
            dialg.dismiss();
//            if(userData.getuType().equals("CS")) {
//                cashbackupdate(txtid, userData.getUid(),amount);
//            }
        });

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

}
