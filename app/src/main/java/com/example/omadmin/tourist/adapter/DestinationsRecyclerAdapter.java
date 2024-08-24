package com.example.omadmin.tourist.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.TouristDestinations;
import com.example.omadmin.tourist.SingleDestination;
import com.example.omadmin.tourist.my_image_view.DynamicImageView;

import java.util.ArrayList;

public class DestinationsRecyclerAdapter extends RecyclerView.Adapter<DestinationsRecyclerAdapter.Vh> {
    private Context context ;
    private ArrayList<TouristDestinations> destinations=new ArrayList<>();

    public DestinationsRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setDestinations(ArrayList<TouristDestinations> destinations) {
        this.destinations = destinations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.tourist_destination_item,parent,false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        TouristDestinations dest =destinations.get(position);
        Glide.with(context).load(dest.getImageUrl()).into(holder.imageView);
        holder.textView.setText(dest.getName());



    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    class Vh extends RecyclerView.ViewHolder{

        DynamicImageView imageView;
        TextView textView;

        public Vh(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.tourist_destination_image);
            textView=itemView.findViewById(R.id.tourist_destination_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, SingleDestination.class);
                    intent.putExtra("dest",destinations.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
