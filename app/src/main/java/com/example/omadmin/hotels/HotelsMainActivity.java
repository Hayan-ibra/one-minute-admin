package com.example.omadmin.hotels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.hotels.recycleradapter.RoomsRecyclerAdapter;
import com.example.omadmin.hotels.viewmodel.HotelsViewModel;
import com.example.omadmin.hotels.viewmodel.RoomsViewModel;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShoppingMainActivity;
import com.example.omadmin.shops.shoprecyclers.GridSpacingItemDecoration;
import com.example.omadmin.uploadingfood.Restaurants;
import com.example.omadmin.uploadingfood.history.PointsHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HotelsMainActivity extends AppCompatActivity {
    MaterialToolbar toolbar;

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    RecyclerView rec_rooms;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    ImageView image_profile;
    TextView tv_message;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceHotel=db.collection("HotelOwnersN");

    HotelsViewModel viewModel;

    Hotels hotel;
    Hotels hotels=Hotels.getInstance();

    RoomsViewModel viewModelRoom;
    RoomsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_main);
        toolbar=findViewById(R.id.HotelsMainActivity_toolbar);
        rec_rooms=findViewById(R.id.HotelsMainActivity_recycler_shop_items);
        collapsingToolbarLayout=findViewById(R.id.HotelsMainActivity_collapsing_shopping);
        appBarLayout=findViewById(R.id.HotelsMainActivity_appbar);
        image_profile=findViewById(R.id.HotelsMainActivity_profile_image);
        tv_message=findViewById(R.id.HotelsMainActivity_message_shopping);
        setSupportActionBar(toolbar);
        adapter=new RoomsRecyclerAdapter(this,hotel);
        rec_rooms.setAdapter(adapter);
        rec_rooms.setLayoutManager(new GridLayoutManager(this,2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rec_rooms.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        viewModel=new ViewModelProvider(this).get(HotelsViewModel.class);
        viewModelRoom=new ViewModelProvider(this).get(RoomsViewModel.class);
        viewModel.getHotels().observe(this, new Observer<Hotels>() {
            @Override
            public void onChanged(Hotels hotels) {
                tv_message.setVisibility(View.GONE);
                Glide.with(HotelsMainActivity.this).load(hotels.getImageUrl()).into(image_profile);
                toolbar.setTitle(hotels.getHotelName());
                collapsingToolbarLayout.setTitle(hotels.getHotelName());
                collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                hotel=hotels;
                Hotels hotels1=Hotels.getInstance();
                hotels1.setLocation(hotels.getLocation());
                hotels1.setHotelId(hotels.getHotelId());
                hotels1.setHotelName(hotels.getHotelName());
                hotels1.setCity(hotels.getCity());
                hotels1.setDescription(hotels.getDescription());
                hotels1.setRate(hotels.getRate());
                hotels1.setNumberOfPreviews(hotels.getNumberOfPreviews());
                hotels1.setPhoneNumber(hotels.getPhoneNumber());
            }
        });
        viewModelRoom.getRoom().observe(this, new Observer<ArrayList<HotelRoom>>() {
            @Override
            public void onChanged(ArrayList<HotelRoom> hotelRooms) {

                adapter.setRooms(hotelRooms);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotels,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.hotels_menu_item_create){
            startActivity(new Intent(HotelsMainActivity.this, HotelProfileCreation.class));

        } else if (item.getItemId()==R.id.hotels_menu_item_add_room) {
            startActivity(new Intent(HotelsMainActivity.this, AddRoom.class));


        } else if (item.getItemId()==R.id.hotels_menu_item_sign_out) {

            firebaseAuth.signOut();
            Users users=Users.getInstance();
            users.setEmail(null);
            users.setAccountType(null);
            users.setUid(null);
            users.setUrl(null);
            //users.setPoints(null);
            users.setGender(null);


            hotels.setHotelId(null);
            hotels.setCity(null);
            //hotels.setNumberOfPreviews(null);
            hotels.setDescription(null);
            hotels.setLocation(null);

            startActivity(new Intent(HotelsMainActivity.this, MainActivity.class));
            finish();




        } else if (item.getItemId()==R.id.hotels_menu_item_update) {
            if (hotel!=null){
                Intent intent=new Intent(HotelsMainActivity.this, HotelProfileUpdate.class);
                intent.putExtra("hotel",hotel);
                startActivity(intent);

            }


        } else if (item.getItemId()==R.id.hotels_menu_item_history) {
            startActivity(new Intent(HotelsMainActivity.this, PointsHistory.class));

        }
        return super.onOptionsItemSelected(item);
    }




}