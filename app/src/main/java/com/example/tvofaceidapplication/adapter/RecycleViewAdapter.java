package com.example.tvofaceidapplication.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.R;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {
    private List<MyEmployee> myEmployees;
    private LayoutInflater layoutInflater;
    private Context context;

    public RecycleViewAdapter (Context context,List<MyEmployee> myEmployees){
        this.myEmployees = myEmployees;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgView;
        public TextView txtView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imgView);
            txtView = itemView.findViewById(R.id.textView);
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.item_employee,parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyEmployee myEmployee = myEmployees.get(position);
        byte[] decodedString = Base64.decode(myEmployee.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imgView.setImageBitmap(decodedByte);
        holder.txtView.setText(myEmployee.getName());
    }

    @Override
    public int getItemCount() {
        return myEmployees.size();
    }
}
