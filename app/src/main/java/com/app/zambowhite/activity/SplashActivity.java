package com.app.zambowhite.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.app.zambowhite.R;
import com.app.zambowhite.config.SessionManager;

public class SplashActivity extends AppCompatActivity {
    SessionManager session;
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session=new SessionManager(SplashActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        makesplash();
    }
    private void makesplash() {
       Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (session.isLoggedIn()) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public static void savePreferences(String key, String value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.clear();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getPreferences(String key, String val) {
        return sharedPreferences.getString(key, val);
    }
}
