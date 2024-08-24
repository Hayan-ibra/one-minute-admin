package com.example.omadmin.shops.shoprecyclers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.shops.ShopSingleItem;

import java.util.ArrayList;

public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.VH> {
    private ArrayList<StoreItem> items=new ArrayList<>();
    private Context context;

    public ItemsRecyclerAdapter( Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<StoreItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.store_item_item,null,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        StoreItem storeItem=items.get(position);
        holder.tv.setText(storeItem.getItemName());

        Glide.with(context).load(storeItem.getImageUrl()).into(holder.iv);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView tv;


        public VH(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.imageView_item_recycler_store_item);
            tv=itemView.findViewById(R.id.textView_item_recycler_store_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ShopSingleItem.class);
                    StoreItem item=items.get(getAdapterPosition());
                    intent.putExtra("obj",item);
                    context.startActivity(intent);
                }
            });
        }
    }


}
