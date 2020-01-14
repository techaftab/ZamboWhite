package com.app.zambowhite.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.app.zambowhite.R;
import com.app.zambowhite.config.AppController;
import com.app.zambowhite.config.Configuration;
import com.app.zambowhite.config.Constant;
import com.app.zambowhite.config.PrefManager;
import com.app.zambowhite.models.FastagList;
import com.app.zambowhite.models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.hasnat.sweettoast.SweetToast;

public class FastagAdapter extends RecyclerView.Adapter<FastagAdapter.MyViewHolder>
        implements Filterable {

    String TAG=FastagAdapter.class.getSimpleName();
    private Context context;
    private List<FastagList> loadList;
    private List<FastagList> loadListFiltered;
    private FastagAdapterListner listener;
    ProgressDialog progressDialog;
    private String type;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName,txtAccount;
        private Button btnRecharge;
        public MyViewHolder(View view) {
            super(view);
            txtName=view.findViewById(R.id.txt_name_send);
            txtAccount=view.findViewById(R.id.txt_account);
            btnRecharge=view.findViewById(R.id.btn_recharge);
            btnRecharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                   // SweetToast.error(context,"Service under process");
                    openDialogRecharge(pos,loadListFiltered.get(pos).getBeneficiary_id(),loadListFiltered.get(pos).getBeneAccount());
                }
            });
            view.setOnClickListener(v -> {
                int pos=getAdapterPosition();
               // SweetToast.error(context,"Service under process");
                openDialogRecharge(pos,loadListFiltered.get(pos).getBeneficiary_id(),loadListFiltered.get(pos).getBeneAccount());
            });
        }
    }

    private void openDialogRecharge(int pos, String beneficiary_id, String beneAccount) {
        progressDialog=new ProgressDialog(context);
        UserData userData = PrefManager.getInstance(context).getUserData();
        final Dialog dialg=new Dialog(context);
        dialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialg.setContentView(R.layout.layout_fastag_recharge);
        dialg.setCanceledOnTouchOutside(false);
        dialg.setCancelable(false);

        RelativeLayout imgClose =  dialg.findViewById(R.id.rl_close_fastag);
        EditText editTextAmount=dialg.findViewById(R.id.edittext_amount_fastag);
        Button btnRecharge=dialg.findViewById(R.id.btn_recharge_fastag);

        btnRecharge.setOnClickListener(v -> {
            String amount=editTextAmount.getText().toString().trim();

            if (TextUtils.isEmpty(amount)){
                SweetToast.error(context,"Please enter amount!");
            } else if (Float.valueOf(amount)<=0){
                SweetToast.error(context,"Please enter valid amount!");
            }else if (userData.getUsertype().equalsIgnoreCase("CS")){
                calculateCharges(pos,beneficiary_id,beneAccount,amount,dialg,userData.getId(),userData.getTxntoken());
            }else {
                listener.rechargeFastag(pos,beneficiary_id,beneAccount,amount,dialg);
            }
        });

        imgClose.setOnClickListener(v -> dialg.dismiss());

        dialg.show();
        Window window = dialg.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    private void calculateCharges(int pos, String beneficiary_id, String beneAccount, String amount,
                                  Dialog dialg, String userId, String token) {
        String tag_string_req = "calculateCharges";
        Configuration.showDialog("Please wait...",progressDialog);
      //  try{Configuration.hideKeyboardFrom(context);}catch (Exception e){e.printStackTrace();}
      //  HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AppConfig.CHARGES_FASTAG,
                response -> {
                    Log.d(TAG,"calculateCharges RESPONSED"+response);
                    assert progressDialog != null;
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String responseCode=jsonObject.getString("status");
                        if (responseCode.equalsIgnoreCase("0")) {
                            String txnAmount=jsonObject.getString("txnamount");
                            listener.rechargeFastag(pos,beneficiary_id,beneAccount,amount,dialg);
                        } else if (responseCode.equalsIgnoreCase("2")){
                            String txnAmount=jsonObject.getString("txnamount");
                            String pgcharge=jsonObject.getString("pgcharge");
                            listener.lowBalanceRecharge(pos,beneficiary_id,beneAccount,amount,txnAmount,pgcharge,dialg);
                        } else{
                            SweetToast.error(context,"Try again after sometime!");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    assert progressDialog != null;
                    progressDialog.dismiss();
                    Log.e("calculateCharges ERROR","ERROR--->"+error.toString());
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(Constant.AMOUNT,amount);
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


    public FastagAdapter(Context context, List<FastagList> contactList, FastagAdapterListner listener) {
        this.context = context;
        this.listener = listener;
        this.loadList = contactList;
        this.loadListFiltered = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_fastag_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final FastagList contact = loadListFiltered.get(position);
        holder.txtName.setText(contact.getBeneName());
        holder.txtAccount.setText(contact.getBeneAccount());
    }
    @Override
    public int getItemCount() {
        return loadListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    loadListFiltered = loadList;
                } else {
                    List<FastagList> filteredList = new ArrayList<>();
                    for (FastagList row : loadList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBeneName().toLowerCase().contains(charString.toLowerCase())
                                || row.getBeneAccount().toLowerCase().contains(charString.toLowerCase())
                                || row.getBeneMobile().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    loadListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = loadListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                loadListFiltered = (ArrayList<FastagList>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface FastagAdapterListner {
        void onFastagAdapterSelcted(FastagList fastagList);
        void rechargeFastag(int pos, String beneficiary_id, String beneAccount, String amount, Dialog dialg);

        void lowBalanceRecharge(int pos, String beneficiary_id, String beneAccount, String payAmount, String txnAmount, String pgcharge, Dialog dialg);
    }
}
