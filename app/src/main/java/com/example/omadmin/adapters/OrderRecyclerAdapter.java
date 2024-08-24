package com.example.omadmin.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.Orders;
import com.example.omadmin.uploadingfood.OrderActivity;

import java.util.ArrayList;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.VH> {

    String resID;
    ArrayList<Orders> orders=new ArrayList<>();
    Context context;

    Listenerr listenerr;

    public OrderRecyclerAdapter(String resID,Context context, Listenerr listenerr) {

        this.context = context;
        this.listenerr = listenerr;
        this.resID=resID;
    }

    public void setOrders(ArrayList<Orders> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public class VH extends RecyclerView.ViewHolder{
        TextView tv_name,tv_food_name,tv_time,tv_notes,tv_number,tv_phone,tv_state;
        ImageView iv_food;
        Button btn_accept,btn_reject;

        CardView cardView;

        public VH(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.order_item_text_usernmae);
            tv_food_name=itemView.findViewById(R.id.order_item_text_food_name);
            tv_number=itemView.findViewById(R.id.order_item_text_number_of_items);
            tv_notes=itemView.findViewById(R.id.order_item_text_notes);
            tv_time=itemView.findViewById(R.id.order_item_text_tiem);
            iv_food=itemView.findViewById(R.id.order_item_image_food);
            btn_accept=itemView.findViewById(R.id.order_item_button_accept);
            btn_reject=itemView.findViewById(R.id.order_item_button_refuse);
            tv_phone=itemView.findViewById(R.id.order_item_text_phone);
            cardView=itemView.findViewById(R.id.card_order);
            tv_state=itemView.findViewById(R.id.order_item_text_state);



            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    int pos=getAdapterPosition();
                    String userID=orders.get(pos).getUserId();
                    String time_compare=orders.get(pos).getTimeForDelete();
                    if(context instanceof OrderActivity){

                        ((OrderActivity) context).setTheStateToAccepted(userID,resID,time_compare,1);


                    }
                    btn_accept.setEnabled(false);
                    btn_accept.setClickable(false);
                    btn_reject.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_accept.setEnabled(true);
                            btn_accept.setClickable(true);
                        }
                    },2000);

                }
            });

            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    Orders orders1 =orders.get(pos);
                    String userID=orders1.getUserId();
                    String time_compare=orders1.getTimeForDelete();
                    Double price=orders1.getPrice();

                    String userName=orders1.getUsername();
                    String food=orders1.getFoodName();
                    String userId1=orders1.getUserId();
                    String resId1=orders1.getResId();
                    String quantity=String.valueOf(orders1.getNumberOfItems());
                    if(context instanceof OrderActivity){

                        ((OrderActivity) context).setTheStateToDenied(userID,resID,time_compare,2);
                        ((OrderActivity) context).pointsTransaction(price,userId1,resId1,food,userName,quantity);


                    }
                    btn_reject.setEnabled(false);
                    btn_reject.setClickable(false);
                    btn_accept.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_reject.setEnabled(true);
                            btn_reject.setClickable(true);
                        }
                    },2000);
                }
            });

        }
    }






    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.order_item,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Orders order =orders.get(position);
        holder.tv_name.setText("user : "+order.getUsername());
        String time= (String) DateUtils.getRelativeTimeSpanString(order.getTime().getSeconds()*1000);
        holder.tv_food_name.setText(order.getFoodName());
        holder.tv_time.setText("from : "+time);
        holder.tv_number.setText("quantity : "+order.getNumberOfItems());
        holder.tv_phone.setText("phone "+order.getPhoneNum());
        holder.tv_state.setVisibility(View.GONE);
        if (order.getSpecialNotes()!= null){
            holder.tv_notes.setText("costumer notes "+order.getSpecialNotes());
        }
        Glide.with(context).load(order.getImageUrl()).into(holder.iv_food);

        holder.tv_name.setTag(order.getUserId());
        holder.tv_phone.setTag(order.getTimeForDelete());
        if (order.getState()==1){
            holder.btn_reject.setEnabled(false);
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
            holder.tv_state.setText("Accepted");
            holder.tv_state.setTextColor(Color.GREEN);
            holder.tv_state.setVisibility(View.VISIBLE);

        } else if (order.getState()==2) {
            holder.btn_accept.setEnabled(false);
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
            holder.tv_state.setText("Denied");
            holder.tv_state.setTextColor(Color.RED);
            holder.tv_state.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public interface Listenerr{
        public  void  onclicked(long state,String userId,String time);
    }


}
