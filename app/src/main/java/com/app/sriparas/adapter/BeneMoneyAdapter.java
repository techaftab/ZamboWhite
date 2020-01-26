package com.app.sriparas.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.sriparas.R;
import com.app.sriparas.config.Configuration;
import com.app.sriparas.models.BeneficiaryResponse;

import java.util.ArrayList;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class BeneMoneyAdapter extends RecyclerView.Adapter<BeneMoneyAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private List<BeneficiaryResponse.Beneficiary> loadList;
    private List<BeneficiaryResponse.Beneficiary> loadListFiltered;
    private BeneMoneyAdapterListner listener;

    String txnType="";

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,txtAccount,txtBank,txtIfsc;
        Button btnTransfer, btnDelete;
        CardView cardView;

        MyViewHolder(View view) {
            super(view);
            txtName=view.findViewById(R.id.txt_name_send);
            txtAccount=view.findViewById(R.id.txt_account);
            txtBank=view.findViewById(R.id.txt_bank);
            txtIfsc=view.findViewById(R.id.txt_ifsc);
            btnTransfer=view.findViewById(R.id.btn_transfer);
            btnDelete =view.findViewById(R.id.btn_delete_beneficiary);
            cardView=view.findViewById(R.id.cardview_u);

            btnDelete.setOnClickListener(v -> {
                int pos=getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                builder.setMessage("Are you sure you want to delete beneficiary?")
                        .setTitle("Delete Beneficiary")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            listener.deleteBeneficiary(loadListFiltered.get(pos).getBeneficiary_id());
                            dialog.cancel();
                        })
                        .setNegativeButton(" No ", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
            });

            btnTransfer.setOnClickListener(v -> {
                int pos=getAdapterPosition();
                openDialogForPayment(pos,loadListFiltered.get(pos));
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void openDialogForPayment(int pos, BeneficiaryResponse.Beneficiary beneficiary) {
        final Dialog dialog=new Dialog(context);
        dialog.setTitle("Send to "+beneficiary.getBeneName());
        dialog.setContentView(R.layout.layout_amount_tranfer);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        final RadioGroup radioGroup;
        radioGroup=dialog.findViewById(R.id.rg_type);
        RadioButton rbImps=dialog.findViewById(R.id.radio_imps);
        RadioButton rbNeft=dialog.findViewById(R.id.radio_neft);
        TextView txtAcc=dialog.findViewById(R.id.txt_acc);
        TextView txtDialog=dialog.findViewById(R.id.dialog);
        // txtDialog.setVisibility(View.GONE);
        txtAcc.setText("A/C No: "+beneficiary.getBeneAccount());
        final TextView txtCancel=dialog.findViewById(R.id.txt_cancel);
        final EditText editAmount=dialog.findViewById(R.id.edittext_amount_view);
        Button btnSend1=dialog.findViewById(R.id.btn_send);
        ProgressBar progressBar=dialog.findViewById(R.id.progress_money);
//        radioGroup.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (rbImps.isChecked()){
                txnType="PA";
            }
            if (rbNeft.isChecked()){
                txnType="NE";
            }
        });

        txtCancel.setOnClickListener(v1 -> dialog.dismiss());
        btnSend1.setOnClickListener(v12 -> {
            if (Configuration.hasNetworkConnection(context)){
                String amount=editAmount.getText().toString().trim();
                if (radioGroup.getCheckedRadioButtonId()==-1){
                    SweetToast.error(context,"Select transfer type");
                }else if (amount.isEmpty()){
                    SweetToast.error(context,"Enter Amount");
                } else if (Integer.valueOf(amount)<100){
                    SweetToast.error(context,"Invalid amount");
                }else if (Configuration.hasNetworkConnection(context)){
                    listener.sendMoney(beneficiary.getBeneAccount(),beneficiary.getBeneName(),beneficiary.getIfsccode(),
                            beneficiary.getBank(),beneficiary.getBeneficiary_id(),amount,txnType,dialog,progressBar);
                }else {
                    SweetToast.error(context,"Try after sometime");
                }
            }else {
                Configuration.openPopupUpDown(context,R.style.Dialod_UpDown,"internetError",
                        "No internet Connection");
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }


    public BeneMoneyAdapter(Context context, List<BeneficiaryResponse.Beneficiary> contactList,BeneMoneyAdapterListner listener) {
        this.context = context;
        this.listener = listener;
        this.loadList = contactList;
        this.loadListFiltered = contactList;
    }

    public interface BeneMoneyAdapterListner{
        void onBeneficiarySelected(BeneficiaryResponse.Beneficiary beneficiary);
        void sendMoney(String beneAccount, String beneName, String ifsccode, String bank, String beneficiary_id,
                       String amount,String txnType, Dialog dialog,ProgressBar progressBar);
        void deleteBeneficiary(String beneficiary_id);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_beneficiary, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final BeneficiaryResponse.Beneficiary contact = loadListFiltered.get(position);
        holder.txtName.setText(contact.getBeneName());
        holder.txtAccount.setText("A/C No.: "+contact.getBeneAccount());
        holder.txtIfsc.setText("IFSC: "+contact.getIfsccode());
        holder.txtBank.setText("Bank: "+contact.getBank());

        holder.btnTransfer.setVisibility(View.VISIBLE);
        if (position%2==0){
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else {
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.card_color));
        }
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
                    List<BeneficiaryResponse.Beneficiary> filteredList = new ArrayList<>();
                    for (BeneficiaryResponse.Beneficiary row : loadList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBeneName().toLowerCase().contains(charString.toLowerCase())
                                || row.getBeneAccount().toLowerCase().contains(charString.toLowerCase())
                                ||row.getBeneMobile().toLowerCase().contains(charString.toLowerCase())) {
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
                loadListFiltered = (ArrayList<BeneficiaryResponse.Beneficiary>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
