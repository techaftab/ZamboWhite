package com.app.sriparas.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.sriparas.R;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.config.SessionManager;
import com.app.sriparas.config.WebService;
import com.app.sriparas.config.updateBalance;
import com.app.sriparas.fragments.FragmentHome;
import com.app.sriparas.fragments.FragmentProfile;
import com.app.sriparas.fragments.FragmentWalletTransaction;
import com.app.sriparas.models.UserData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import xyz.hasnat.sweettoast.SweetToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,updateBalance {

    private ImageView imgNavigation,imgBackMain;
    private DrawerLayout drawerLayout;
    private TextView txtUserName,txtPhoneUser,txtBalance;
    private LinearLayout balanceLayout;
    private ProgressBar progressBar;
    UserData userData;
    private FragmentTransaction ft;
    private Fragment currentFragment;
    BottomNavigationView bottomNavigationView;
    private RelativeLayout rlLogout,rlReferral;
    SessionManager session;
    Handler handler;

    WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userData= PrefManager.getInstance(MainActivity.this).getUserData();
        session=new SessionManager(MainActivity.this);
        handler=new Handler();
        init();
        loadFragment("1");
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        imgNavigation.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        imgBackMain.setOnClickListener(this);
        rlReferral.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onUpdateBalance(String balance) {
        txtBalance.setText("\u20B9" + balance);
        SplashActivity.savePreferences(Constant.BALANCE, balance);
    }

    private void loadFragment(String anim) {
        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new FragmentHome();
        if (anim.equals("1")){
            ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
        }
        ft.replace(R.id.framelayout_main, currentFragment);
        ft.commit();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        webService=new WebService((updateBalance)this);
        txtBalance=findViewById(R.id.txt_wallet_balance);
        imgNavigation=findViewById(R.id.img_navigation_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        txtUserName=findViewById(R.id.user_name);
        txtPhoneUser=findViewById(R.id.txt_phone_user);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        progressBar=findViewById(R.id.progressbar_main);
        imgBackMain=findViewById(R.id.img_back_main);
        rlLogout=findViewById(R.id.rl_logout);
        balanceLayout=findViewById(R.id.balance_layout);
        rlReferral=findViewById(R.id.refer);

        txtUserName.setText(userData.getFullName());
        txtPhoneUser.setText(getResources().getString(R.string.app_name)+" ("+userData.getMobile()+")");

        webService.updateBalance(userData.getId(), userData.getTxntoken());
    }

    @Override
    public void onClick(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.framelayout_main);
        if (v==imgNavigation){
            if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else drawerLayout.closeDrawer(GravityCompat.END);
        }
        if (v==rlLogout){
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            logout();
        }
        if (v==imgBackMain){
            if (!(f instanceof FragmentHome)) {
                loadFragment("1");
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
            }else {
                onBackPressed();
            }
        }
        if (v==rlReferral){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, "Woohoo, this is my Sri Paras App please install this app from this link: " +
                    "https://play.google.com/store/apps/details?id=com.app.sriparas&hl=en"+" and use my referral code for registraion. "+userData.getMemberId());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share"));
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure want to logout?");
        builder.setPositiveButton("No", (dialog, id) -> dialog.cancel());
        builder.setNegativeButton("Yes", (dialog, which) -> {
            progressBar.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                session.setLogin(false);
                PrefManager.getInstance(MainActivity.this).logout();
                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent1);
                overridePendingTransition(0,0);
                finish();
            },300);
        });
        builder.create();
        builder.show();
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadFragmentBottom(new FragmentHome());
                return true;
            case R.id.navigation_profile:
                loadFragmentBottom(new FragmentProfile());
                return true;
            case R.id.navigation_transaction:
                loadFragmentBottom(new FragmentWalletTransaction());
                return true;
        }
        return false;
    };


    private void loadFragmentBottom(Fragment fragment) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout_main, fragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.framelayout_main);
        if (!(f instanceof FragmentHome)) {
            loadFragment("1");
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        }else if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                        Intent launchNextActivity = new Intent(Intent.ACTION_MAIN);
                        launchNextActivity.addCategory(Intent.CATEGORY_HOME);
                        launchNextActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchNextActivity);
                        finish();
                    }).create().show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateHeader(String string) {
        bottomNavigationView.setVisibility(View.VISIBLE);
        txtPhoneUser.setText(string+" ("+userData.getMobile()+")");
        balanceLayout.setVisibility(View.VISIBLE);
        imgNavigation.setVisibility(View.GONE);
        imgBackMain.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void updateHeaderHome() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        txtPhoneUser.setText(getResources().getString(R.string.app_name)+" ("+userData.getMobile()+")");
        balanceLayout.setVisibility(View.VISIBLE);
        imgNavigation.setVisibility(View.VISIBLE);
        imgBackMain.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public void updateHeaderForBottom(String string) {
        bottomNavigationView.setVisibility(View.VISIBLE);
        txtPhoneUser.setText(string+" ("+userData.getMobile()+")");
        balanceLayout.setVisibility(View.GONE);
        imgNavigation.setVisibility(View.GONE);
        imgBackMain.setVisibility(View.VISIBLE);

    }
}
