package com.example.omadmin.adapters;

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
import com.example.omadmin.models.FoodRecyclerModel;
import com.example.omadmin.uploadingfood.FoodSingleItem;

import java.util.List;

public class FoodRecycler extends RecyclerView.Adapter<FoodRecycler.VH> {

    Context context;
    List<FoodRecyclerModel> foods;
    Listenerr listenerr;


    public FoodRecycler(Context context, List<FoodRecyclerModel> foods) {
        this.context = context;
        this.foods = foods;

    }

    public class  VH extends RecyclerView.ViewHolder{

        TextView tv_name;
        ImageView image;


        public VH(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.textView_item_recycler_food);
            image=itemView.findViewById(R.id.imageView_item_recycler_food);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position1=getAdapterPosition();

                    Intent intent =new Intent(context, FoodSingleItem.class);
                    FoodRecyclerModel food1 =foods.get(position1);

                    //listenerr.onclicked(position1);


                    intent.putExtra("1",food1.getName());
                    intent.putExtra("2",food1.getRestaurantID());
                    intent.putExtra("3",food1.getFoodUrl());
                    context.startActivity(intent);

                }
            });

        }


    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.food_item,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FoodRecyclerModel food=foods.get(position);
        holder.tv_name.setText(food.getName());

        //using glide
        Glide.with(context).load(food.getFoodUrl()).fitCenter().into(holder.image);


    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


    public interface Listenerr{
        public  void  onclicked(int count);
    }





}
