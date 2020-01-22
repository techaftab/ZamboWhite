package com.app.sriparas.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.sriparas.R;
import com.app.sriparas.activity.MainActivity;
import com.app.sriparas.activity.SplashActivity;
import com.app.sriparas.config.AppConfig;
import com.app.sriparas.config.AppController;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.HttpsTrustManager;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.config.WebService;
import com.app.sriparas.config.updateBalance;
import com.app.sriparas.models.GPSTracker;
import com.app.sriparas.models.UserData;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

public class FragmentRecharge extends Fragment implements View.OnClickListener,updateBalance{

    private static final int REQUEST_LOCATION = 32;
    private static final String TAG = FragmentRecharge.class.getSimpleName();
    GoogleApiClient googleApiClient;
    private View view;
    private RelativeLayout rlBack,rlNext;
    private EditText editTextMobile, editTextAmount;
    private Spinner spinnerOperatorList;

    private FragmentTransaction ft;
    private Fragment currentFragment;

    ProgressDialog progressDialog;

    private UserData userData;
    private ArrayList<String> operatorName;
    private ArrayList<String> operatorCode;
    private ArrayList<String> operatorType;

    private WebService webService;

    private GPSTracker gpsTracker;

    private String code="",service="",operator;
    private Double lat,lon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recharge,container,false);
        ((MainActivity) Objects.requireNonNull(getContext())).updateHeader(getResources().getString(R.string.mobile_recharge));
        userData= PrefManager.getInstance(getActivity()).getUserData();
        gpsTracker = new GPSTracker(getActivity());
        init(view);
        rlBack.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        return view;
    }

    private void init(View view) {
        webService=new WebService((updateBalance)this);
        progressDialog=new ProgressDialog(getActivity());
        rlBack=view.findViewById(R.id.rl_back);
        rlNext=view.findViewById(R.id.rl_next);
        editTextMobile=view.findViewById(R.id.ed_mobile);
        spinnerOperatorList=view.findViewById(R.id.operator_list_recharge);
        editTextAmount =view.findViewById(R.id.ed_amount_recharge);
        operatorName =new ArrayList<>();
        operatorCode=new ArrayList<>();
        operatorType=new ArrayList<>();
        if (Configuration.hasNetworkConnection(Objects.requireNonNull(getActivity()))){
            getOperator();
        }
        spinnerOperatorList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String operatorNa = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < operatorName.size(); i++) {
                    if (operatorName.get(i).equals(operatorNa)){
                        code = operatorCode.get(i);
                        service = operatorType.get(i);
                        operator = operatorName.get(i);
                    }
                }
                System.out.println("Operator code-->" + code + ", Operator Name---->" + operatorNa);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v==rlBack){
            back();
        }
        if (v==rlNext){
            String mobile=editTextMobile.getText().toString().trim();
            String amount= editTextAmount.getText().toString().trim();
            if (validateMobile(code,mobile,amount)){
                rechargeMobile(mobile,amount,userData.getId(),userData.getTxntoken(),code,service,operator);
            }
        }
    }

    private void back() {
        ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
        currentFragment = new FragmentHome();
        ft.replace(R.id.framelayout_main, currentFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean validateMobile(String code, String mobile, String amount) {
        if (TextUtils.isEmpty(code)){
            SweetToast.error(getActivity(),"Please select operator");
            return false;
        }
        if (TextUtils.isEmpty(mobile)) {
            editTextMobile.setError(getResources().getString(R.string.enter_mobile));
            editTextMobile.requestFocus();
            SweetToast.error(getActivity(),"Please Enter Phone no");
            return false;
        }
        if (TextUtils.isEmpty(amount)) {
            editTextAmount.setError(getResources().getString(R.string.enter_amount));
            editTextAmount.requestFocus();
            SweetToast.error(getActivity(),"Please Enter amount");
            return false;
        }
        if (TextUtils.isEmpty(SplashActivity.getPreferences(Constant.BALANCE,""))
                ||Float.valueOf(SplashActivity.getPreferences(Constant.BALANCE,""))<Float.valueOf(amount)) {
            SweetToast.error(getActivity(),"Your Available balance is low");
            return false;
        }
        return true;
    }
    private void getOperator() {
        Configuration.showDialog("Please wait...", progressDialog);
        progressDialog.setCanceledOnTouchOutside(false);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.RECHARGE_OPOERATOR_LIST,
                response -> {
                    progressDialog.dismiss();
                    try {
                        operatorCode.clear();
                        operatorName.clear();
                        operatorType.clear();
                        System.out.println("Operator response--->" + response);
                        JSONArray jsonArray = new JSONArray(response);
                        operatorName.add(0, "Select Operator");
                        operatorCode.add(0, "0");
                        operatorType.add(0, "NA");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            operatorName.add(jsonObject1.getString("operator"));
                            operatorCode.add(jsonObject1.getString("operatorCode"));
                            operatorType.add(jsonObject1.getString("type"));
                        }
                        ArrayAdapter<String> counryAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                                R.layout.spinner_item,R.id.spinner_text, operatorName);
                        spinnerOperatorList.setAdapter(counryAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SweetToast.error(getActivity(),"Try after somertime");
                        back();
                    } finally {
                        progressDialog.dismiss();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    SweetToast.error(getActivity(),"Try after somertime");
                    back();
                });
        RequestQueue requestQueue= Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }

    private void rechargeMobile(String mobile, String amount, String id, String txntoken,String code,
                                String service,String optName) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
//            lat=null;lon=null;
            final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (manager != null) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
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
                        openDialog(mobile,amount,id,txntoken,code,service,optName,lat,lon);
                    }
                }
            }
        }
    }

    private void openDialog(String mobile, String amount, String id, String txntoken,
                            String code, String service, String operator, Double lat, Double lon) {
        final Dialog dialog=new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_payment);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Button btnCancel,btnContinue;
        btnCancel=dialog.findViewById(R.id.btn_cancel);
        btnContinue=dialog.findViewById(R.id.btn_continue);
        TextView txtMsg=dialog.findViewById(R.id.txt_msg);

        txtMsg.setText(Html.fromHtml("Do you want to proceed for payment of" +
                " Rs."+amount + " for "+
                mobile + " ?"));

        btnContinue.setOnClickListener(v ->
                recharge(mobile,amount,id,txntoken,code,service,operator,lat,lon));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

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
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getActivity())) {
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
            googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                        status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                }
            });
        }
    }
    private void recharge(String mobile, String amount, String id, String txntoken, String code,
                          String service, String operator, Double lat, Double lon) {
        String tag_string_req = "recharge_process";
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        HttpsTrustManager.allowAllSSL();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.MOBILE_RECHARGE, response -> {
            progressDialog.dismiss();

            Log.e(TAG,"RECHARGE RESPONSE-->"+response);
            try {
                JSONObject jsonObject1=new JSONObject(response);
                String status = jsonObject1.getString("status");
                String message=jsonObject1.getString("message");
                Log.d("TAG","status:"+status);
                if (status.equals("S")){
                    String txtid=jsonObject1.getString("txnId");
                    openPopup(message,"S",txtid,amount);
                }else if (status.equalsIgnoreCase("P")){
                    String txtid="NA";
                    openPopup(message,"P",txtid,amount);
                }else {
                    Configuration.openPopupUpDown(getActivity(),R.style.Dialod_UpDown,"error",
                            message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("TAG", "RECHARGE_PROCESS3 Error: " + error.getMessage());
            SweetToast.error(getActivity(),"Try after sometime");
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.MOBILE, mobile);
                params.put(Constant.OPERATOR, code);
                params.put(Constant.AMOUNT, amount);
                params.put(Constant.OPT_NAME, operator);
                params.put(Constant.SERVICE, service);
                params.put(Constant.LATTITUDE, String.valueOf(lat));
                params.put(Constant.LONGITUDE, String.valueOf(lon));
                params.put(Constant.USER_ID, id);
                params.put(Constant.SOURCE, Constant.APP);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("token",txntoken);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @SuppressLint("SetTextI18n")
    private void openPopup(String message, final String type, String txtid, String amount) {
        final Dialog dialg=new Dialog(Objects.requireNonNull(getActivity()));
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
            editTextAmount.setText("");
            editTextAmount.setText("");
            code="";
            spinnerOperatorList.setSelection(0);
            webService.updateBalance(userData.getId(), userData.getTxntoken());
        });

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onUpdateBalance(String balance) {
        ((MainActivity) Objects.requireNonNull(getContext())).onUpdateBalance(balance);
    }
}
