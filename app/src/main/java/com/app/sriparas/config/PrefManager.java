package com.app.sriparas.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.sriparas.models.UserData;

public class PrefManager {

    private static final String SHARED_PREF_NAME = Constant.PREFS_NAME;
    private static final String KEY_ID="keyid";
    private static final String KEY_FULLNAME = "keyfullname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_MOBILE = "keymobile";
    private static final String KEY_USERTYPE = "keyusertype";
    private static final String KEY_TXN_TOKEN = "keytxntoken";
    private static final String KEY_MEMBERID = "keymemberid";

    private static PrefManager mInstance;
    private static Context mCtx;
    private SharedPreferences prefs;
    public static final String FIREBASE_CLOUD_MESSAGING = "fcm";
    public static final String SET_NOTIFY = "set_notify";
    SharedPreferences pref;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    SharedPreferences.Editor editor;


    public PrefManager(Context context) {
        mCtx = context;
        //  prefs = context.getSharedPreferences(FIREBASE_CLOUD_MESSAGING, Context.MODE_PRIVATE);
    }


    public static synchronized PrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(UserData userData) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID,userData.getId());
        editor.putString(KEY_FULLNAME, userData.getFullName());
        editor.putString(KEY_EMAIL, userData.getEmail());
        editor.putString(KEY_MOBILE, userData.getMobile());
        editor.putString(KEY_USERTYPE, userData.getUsertype());
        editor.putString(KEY_TXN_TOKEN,userData.getTxntoken());
        editor.putString(KEY_MEMBERID,userData.getMemberId());
        editor.apply();
    }


    public UserData getUserData() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserData(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_FULLNAME,null),
                sharedPreferences.getString(KEY_EMAIL,null),
                sharedPreferences.getString(KEY_MOBILE,null),
                sharedPreferences.getString(KEY_USERTYPE,null),
                sharedPreferences.getString(KEY_TXN_TOKEN,null),
                sharedPreferences.getString(KEY_MEMBERID,null));
    }


    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    public void saveNotificationSubscription(boolean value){
        SharedPreferences.Editor edits = prefs.edit();
        edits.putBoolean(SET_NOTIFY, value);
        edits.apply();
    }
    public boolean hasUserSubscribeToNotification(){
        return prefs.getBoolean(SET_NOTIFY, false);
    }
    public void clearAllSubscriptions(){
        prefs.edit().clear().apply();
    }

    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.DEVICE_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(Constant.DEVICE_TOKEN, null);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
}
