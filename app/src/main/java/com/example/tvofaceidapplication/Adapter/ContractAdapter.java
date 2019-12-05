package com.example.tvofaceidapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvofaceidapplication.Model.Contract;
import com.example.tvofaceidapplication.Model.MyLending;
import com.example.tvofaceidapplication.R;

import java.util.List;

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.RecyclerViewHolder> {

    private List<MyLending> mList;


    public ContractAdapter(List<MyLending> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_contract, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
        holder.mContractName.setText(mList.get(position).getName());
        holder.mContractId.setText(mList.get(position).getId());
        holder.mContractTime.setText(mList.get(position).getCreated_at());
        holder.mCOntractStatus.setText(mList.get(position).getStatus());
        holder.llContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClick(mList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llContentView;

        TextView mContractId, mContractTime, mContractName, mCOntractStatus;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            llContentView = itemView.findViewById(R.id.item_contract_view);

            mContractId = itemView.findViewById(R.id.item_contract_id);
            mContractTime = itemView.findViewById(R.id.item_contract_createdAt);
            mContractName = itemView.findViewById(R.id.item_contract_name);
            mCOntractStatus = itemView.findViewById(R.id.item_contract_status);
        }
    }

    public interface OnItemClickedListener {
        void onItemClick(MyLending lending);
    }

    private ContractAdapter.OnItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
