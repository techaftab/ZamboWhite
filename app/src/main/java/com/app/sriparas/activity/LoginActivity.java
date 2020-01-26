package com.app.sriparas.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.app.sriparas.R;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.config.Constant;
import com.app.sriparas.config.PrefManager;
import com.app.sriparas.config.SessionManager;
import com.app.sriparas.fragments.FragmentSignup;
import com.app.sriparas.models.UserData;
import com.app.sriparas.viewmodel.LoginViewModel;

import xyz.hasnat.sweettoast.SweetToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextPhone,editTextPassword,editTextForgotPassMobile,editTextOtp;
    private Button btnLogin,btnVerifyMobile;
    private ProgressBar progressBar,progressBarForgot;
    private CheckBox checkBox;
    private TextView txtForgotPassword,txtLogin,txtResendForgot;
    private CardView cardViewlogin,cardViewForgot;
    private LinearLayoutCompat lnOtpForgot;
    LinearLayout lnSignUp;
    SessionManager session;
    FrameLayout frameLayout;
    ScrollView scrollView;

    LoginViewModel loginViewModel;
    private FragmentTransaction ft;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        session=new SessionManager(LoginActivity.this);
        init();
        cardViewlogin.setVisibility(View.VISIBLE);
        cardViewForgot.setVisibility(View.GONE);
        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        btnVerifyMobile.setOnClickListener(this);
        txtLogin.setOnClickListener(this);
        txtResendForgot.setOnClickListener(this);
        lnSignUp.setOnClickListener(this);
    }

    private void init() {
        scrollView=findViewById(R.id.scrollview_login);
        frameLayout=findViewById(R.id.framelayout_login);
        lnSignUp=findViewById(R.id.ln_signup_now);
        cardViewlogin=findViewById(R.id.cardView_login);
        cardViewForgot=findViewById(R.id.cardView_forgot);
        editTextPhone=findViewById(R.id.ed_phone);
        editTextPassword=findViewById(R.id.ed_password);
        editTextForgotPassMobile=findViewById(R.id.ed_phone_forgot);
        btnVerifyMobile=findViewById(R.id.forgot_button);
        progressBarForgot=findViewById(R.id.forgot_progressBar);
        txtResendForgot=findViewById(R.id.txt_resend_forgot);
        lnOtpForgot=findViewById(R.id.lay_otp_forgot);
        editTextOtp=findViewById(R.id.ed_otp_forgot);
        btnLogin=findViewById(R.id.login_button);
        progressBar=findViewById(R.id.login_progressBar);
        checkBox=findViewById(R.id.remember_me);
        txtForgotPassword=findViewById(R.id.forget_password);
        txtLogin=findViewById(R.id.login_user);
        SplashActivity.getPreferences(Constant.PHONE, "");
        scrollView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(SplashActivity.getPreferences(Constant.PHONE,""))){
            editTextPhone.setText(SplashActivity.getPreferences(Constant.PHONE,""));
            editTextPassword.setText(SplashActivity.getPreferences(Constant.PASSWORD,""));
            checkBox.setChecked(true);
        }
        editTextForgotPassMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lnOtpForgot.setVisibility(View.GONE);
                editTextOtp.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==btnLogin){
            String phone=editTextPhone.getText().toString();
            String password=editTextPassword.getText().toString();
            if (setValidation(phone, password)) {
                getLoginResponse(phone, password);
            }
            if (checkBox.isChecked()) {
                SplashActivity.savePreferences(Constant.PHONE,phone);
                SplashActivity.savePreferences(Constant.PASSWORD,phone);
            } else {
                SplashActivity.savePreferences(Constant.PHONE,phone);
                SplashActivity.savePreferences(Constant.PASSWORD,phone);
            }
        }
        if (v==txtForgotPassword){
            cardViewlogin.setVisibility(View.GONE);
            cardViewForgot.setVisibility(View.VISIBLE);
            lnOtpForgot.setVisibility(View.GONE);
            editTextOtp.setText("");
        }
        if (v==txtLogin){
            cardViewlogin.setVisibility(View.VISIBLE);
            cardViewForgot.setVisibility(View.GONE);
            lnOtpForgot.setVisibility(View.GONE);
            editTextOtp.setText("");
            editTextForgotPassMobile.setText("");
        }
        if (v==btnVerifyMobile){
            String phone=editTextForgotPassMobile.getText().toString();
            if (lnOtpForgot.isShown()) {
                String otp=editTextOtp.getText().toString();
                if (isMobileOtp(phone,otp)) {
                    verifyOtpForgot(phone,otp);
                }
            }else {
                if (isMobile(phone)) {
                    verifyFgtMobile(phone);
                }
            }
        }
        if (v==txtResendForgot){
            String phone=editTextForgotPassMobile.getText().toString();
            if (isMobile(phone)) {
                resendOtpLogin(phone, progressBarForgot);
            }
        }
        if (v==lnSignUp){
            frameLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.right_in, R.anim.left_out);
            currentFragment = new FragmentSignup();
            ft.replace(R.id.framelayout_login, currentFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private boolean isMobileOtp(String phone, String otp) {
        if (phone.isEmpty()) {
            editTextForgotPassMobile.setError(getResources().getString(R.string.enter_mobile));
            editTextForgotPassMobile.requestFocus();
            SweetToast.error(LoginActivity.this,getResources().getString(R.string.enter_mobile));
            return false;
        }
        if (otp.isEmpty()) {
            editTextOtp.setError(getResources().getString(R.string.enter_otp));
            editTextOtp.requestFocus();
            SweetToast.error(LoginActivity.this,getResources().getString(R.string.enter_otp));
            return false;
        }
        return true;
    }

    private boolean isMobile(String phone) {
        if (phone.isEmpty()) {
            editTextForgotPassMobile.setError(getResources().getString(R.string.enter_mobile));
            editTextForgotPassMobile.requestFocus();
            SweetToast.error(LoginActivity.this,getResources().getString(R.string.enter_mobile));
            return false;
        }
        return true;
    }

    private boolean setValidation(String phone, String password) {
        if (phone.isEmpty()) {
            editTextPhone.setError(getResources().getString(R.string.enter_mobile));
            editTextPhone.requestFocus();
            SweetToast.error(LoginActivity.this,getResources().getString(R.string.enter_mobile));
            return false;
        } else if (password.isEmpty()) {
            editTextPassword.setError(getResources().getString(R.string.enter_password));
            editTextPassword.requestFocus();
            SweetToast.error(LoginActivity.this,getResources().getString(R.string.enter_password));
            return false;
        }
        return true;
    }
    private void openDialogforOtp(String phone) {
        final Dialog dialg=new Dialog(LoginActivity.this);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.layout_otp_bene);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);

        RelativeLayout imgClose =  dialg.findViewById(R.id.rl_close);
        TextView txtResendOtp=dialg.findViewById(R.id.txt_resend_sender_otp_bene);
        TextView txtMessage=dialg.findViewById(R.id.txt_otp_sender_bene);
        final EditText editTextOtp=dialg.findViewById(R.id.edittext_sender_otp_bene);
        final Button btnVerify=dialg.findViewById(R.id.btn_verify_bene);
        ProgressBar progressBarOtp=dialg.findViewById(R.id.progress_otp);

        txtResendOtp.setOnClickListener(v -> {
            if (Configuration.hasNetworkConnection(LoginActivity.this)){
                resendOtpLogin(phone,progressBarOtp);
            }
        });
        btnVerify.setOnClickListener(v -> {
            String otp=editTextOtp.getText().toString().trim();
            if (otp.isEmpty()){
                Configuration.openPopupUpDown(LoginActivity.this,R.style.Dialod_UpDown,"error",
                        "Enter otp sent to your mobile no");
            }else {
                verifyOtpLogin(phone,otp,progressBarOtp,dialg);
            }
        });

        imgClose.setOnClickListener(v -> dialg.dismiss());

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getLoginResponse(String phone, String password) {
        try {
            Configuration.hideKeyboardFrom(LoginActivity.this);
        }catch(Exception e){e.printStackTrace();}
        progressBar.setVisibility(View.VISIBLE);
        loginViewModel.loginUser(phone,password).observe(this, loginResponse -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (loginResponse.status.equalsIgnoreCase("2")) {
                SweetToast.success(LoginActivity.this,loginResponse.message);
                openDialogforOtp(phone);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                SweetToast.error(LoginActivity.this,loginResponse.message);
            }
        });
    }


    private void resendOtpLogin(String phone, ProgressBar progressBarOtp) {
        Configuration.hideKeyboardFrom(LoginActivity.this);
        progressBarOtp.setVisibility(View.VISIBLE);
        loginViewModel.resendOtpLogin(phone).observe(this, loginResponse -> {
            progressBarOtp.setVisibility(View.INVISIBLE);
            if (loginResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(LoginActivity.this,loginResponse.message);
            } else {
                progressBarOtp.setVisibility(View.INVISIBLE);
                SweetToast.error(LoginActivity.this,loginResponse.message);
            }
        });
    }

    private void verifyOtpLogin(String phone, String otp, ProgressBar progressBarOtp, Dialog dialg) {
        Configuration.hideKeyboardFrom(LoginActivity.this);
        progressBarOtp.setVisibility(View.VISIBLE);
        loginViewModel.verifyOtpLogin(phone,otp).observe(this, userloginResponse -> {
            progressBarOtp.setVisibility(View.INVISIBLE);
            if (userloginResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(LoginActivity.this,userloginResponse.message);
                UserData userData = new UserData(
                        userloginResponse.userdata.getId(),
                        userloginResponse.userdata.getFullName(),
                        userloginResponse.userdata.getEmail(),
                        userloginResponse.userdata.getMobile(),
                        userloginResponse.userdata.getUsertype(),
                        userloginResponse.userdata.getTxntoken(),
                        userloginResponse.userdata.getMemberId());
                PrefManager.getInstance(LoginActivity.this).userLogin(userData);
                session.setLogin(true);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            } else {
                progressBarOtp.setVisibility(View.INVISIBLE);
                SweetToast.error(LoginActivity.this,userloginResponse.message);
            }
        });
    }

    private void verifyFgtMobile(String phone) {
        progressBarForgot.setVisibility(View.VISIBLE);
        loginViewModel.verifyFgtMobile(phone).observe(this, loginResponse -> {
            progressBarForgot.setVisibility(View.INVISIBLE);
            if (loginResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(LoginActivity.this,loginResponse.message);
                lnOtpForgot.setVisibility(View.VISIBLE);
            } else {
                progressBarForgot.setVisibility(View.INVISIBLE);
                SweetToast.error(LoginActivity.this,loginResponse.message);
            }
        });
    }

    private void verifyOtpForgot(String phone, String otp) {
        progressBarForgot.setVisibility(View.VISIBLE);
        loginViewModel.verifyOtpForgot(phone,otp).observe(this, loginResponse -> {
            progressBarForgot.setVisibility(View.INVISIBLE);
            if (loginResponse.status.equalsIgnoreCase("0")) {
                SweetToast.success(LoginActivity.this,loginResponse.message);
                cardViewlogin.setVisibility(View.VISIBLE);
                cardViewForgot.setVisibility(View.GONE);
               // lnOtpForgot.setVisibility(View.VISIBLE);
            } else {
                progressBarForgot.setVisibility(View.INVISIBLE);
               //SweetToast.error(LoginActivity.this,loginResponse.message);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (frameLayout.isShown()){
            frameLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }else
        if (cardViewForgot.isShown()){
            cardViewForgot.setVisibility(View.GONE);
            cardViewlogin.setVisibility(View.VISIBLE);
        }else {
            new AlertDialog.Builder(LoginActivity.this)
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
