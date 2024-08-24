package com.example.omadmin.hotels.recycleradapter;

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
import com.example.omadmin.hotels.RoomSingleItem;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.Hotels;

import java.util.ArrayList;

public class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomsRecyclerAdapter.VH> {


    private ArrayList<HotelRoom> rooms=new ArrayList<>();
    private Context context;

    Hotels hotel;

    public RoomsRecyclerAdapter(Context context, Hotels hotel) {
        this.context = context;
        this.hotel = hotel;
    }

    public RoomsRecyclerAdapter() {

    }



    public void setRooms(ArrayList<HotelRoom> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder{

        ImageView iv;
        TextView tv;


        public VH(@NonNull View itemView) {
            super(itemView);
            iv=itemView.findViewById(R.id.imageView_item_recycler_store_item);
            tv=itemView.findViewById(R.id.textView_item_recycler_store_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, RoomSingleItem.class);
                    intent.putExtra("room",rooms.get(getAdapterPosition()));
                    intent.putExtra("hotel",hotel);
                    context.startActivity(intent);
                }
            });
        }
        void bind(HotelRoom hotelRoom){
            tv.setText("Room : "+hotelRoom.getRoomNum());
            Glide.with(context).load(hotelRoom.getImageUrl()).into(iv);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.store_item_item,null,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HotelRoom room =rooms.get(position);
        holder.bind(room);


    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


}
