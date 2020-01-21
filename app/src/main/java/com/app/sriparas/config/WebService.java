package com.app.sriparas.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.sriparas.activity.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebService {

    private static String TAG=WebService.class.getSimpleName();
    private updateBalance Balance;

    private Context mContext;
    public ArrayList<String> stateId;
    public ArrayList<String> stateName;
    public static ArrayList<String> operatorId=new ArrayList<>();
    public static ArrayList<String> operatorName=new ArrayList<>();
    public static ArrayList<String> bankCode;
    public static ArrayList<String> bankName;
    public static ArrayList<String> masterIfsc;

    public WebService(Context context) {
        mContext=context;
    }

   /* public WebService(Context context) {
        mContext=context;
    }*/
   public WebService(updateBalance callback) {
       Balance = callback;
   }

    public void updateBalance(final String userId){

        String tag_string_req = "get_balance";
        HttpsTrustManager.allowAllSSL();
        @SuppressLint("SetTextI18n")
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.GET_BALANCE, response -> {
             Log.e(TAG, "BALANCE Response: " + response);

            try {
                JSONObject jsonObject=new JSONObject(response);
                String status=jsonObject.getString("status");
                if (status.equals("1")){
                    JSONObject jObj=jsonObject.getJSONObject("data");
                    SplashActivity.savePreferences(Constant.BALANCE,jObj.getString("walletBalance"));
                  //  if (Balance != null) {
                        Balance.onUpdateBalance(jObj.getString("walletBalance"));
                 //   }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e(TAG, "Register Error: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("uUid",userId);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
