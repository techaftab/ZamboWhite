package com.app.zambowhite.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.zambowhite.R;
import com.app.zambowhite.config.PrefManager;
import com.app.zambowhite.fragments.FragmentHome;
import com.app.zambowhite.models.UserData;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgNavigation;
    private DrawerLayout drawerLayout;
    private TextView txtUserName,txtPhoneUser;
    UserData userData;
    private FragmentTransaction ft;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userData= PrefManager.getInstance(MainActivity.this).getUserData();
        init();
        loadFragment();
        imgNavigation.setOnClickListener(this);
    }

    private void loadFragment() {
        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new FragmentHome();
        ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
        ft.replace(R.id.framelayout_main, currentFragment);
        ft.commit();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        imgNavigation=findViewById(R.id.img_navigation_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        txtUserName=findViewById(R.id.user_name);
        txtPhoneUser=findViewById(R.id.txt_phone_user);

        txtUserName.setText(userData.getFullName());
        txtPhoneUser.setText(getResources().getString(R.string.app_name)+" ("+userData.getMobile()+")");

    }

    @Override
    public void onClick(View v) {
        if (v==imgNavigation){
            if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
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
}
