package com.example.omadmin.hotels.recycleradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omadmin.R;
import com.example.omadmin.models.Reservation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class ReservationsRecyclerAdapter extends RecyclerView.Adapter<ReservationsRecyclerAdapter.VH> {

    private  ClickListener listener;
    private Context context;
    private ArrayList<Reservation> reservations=new ArrayList<>();

    public ReservationsRecyclerAdapter(Context context,ClickListener listener) {
        this.context = context;
        this.listener=listener;

    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }



    class VH extends RecyclerView.ViewHolder{
        TextView tv_hotel,tv_room,tv_start,tv_end;


        MaterialButton delete_btn;

        public VH(@NonNull View itemView) {
            super(itemView);
            tv_hotel=itemView.findViewById(R.id.reservation_item_text_hotel);
            tv_room=itemView.findViewById(R.id.reservation_item_text_room);
            tv_end=itemView.findViewById(R.id.reservation_item_text_ending_date);
            tv_start=itemView.findViewById(R.id.reservation_item_text_starting_date);
            delete_btn=itemView.findViewById(R.id.reservation_item_button_delete);

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.delete(reservations.get(getAdapterPosition()));
                }
            });

        }
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.reservation_recycler_item,parent,false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Reservation reservation=reservations.get(position);
        holder.tv_hotel.setText("Name : "+reservation.getUserName());
        holder.tv_room.setText(reservation.getRoomName());
        Timestamp startingDate=reservation.getDate().get(0);
        Timestamp endDate=reservation.getDate().get((reservation.getDate().size())-1);

        holder.tv_start.setText(startingDate.toDate().toString());
        holder.tv_end.setText(endDate.toDate().toString());

    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public interface ClickListener{
        public void delete(Reservation reservation);
    }

}
