package com.example.omadmin.uploadingfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.adapters.FoodRecycler;
import com.example.omadmin.models.FoodRecyclerModel;
import com.example.omadmin.models.RestaurantProfile;
import com.example.omadmin.models.Users;
import com.example.omadmin.uploadingfood.history.PointsHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Restaurants extends AppCompatActivity {
    MaterialToolbar toolbar;

    FloatingActionButton fab;



    //firebase authentication

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //firebase firestore

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private FirebaseFirestore db1 =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("RestaurantOwners");
    private CollectionReference collectionFoodReference=db1.collection("Foods");

    //firebase storage for image storage
    private StorageReference storageReference;


    String current_user;
    String current_user_id;

    int meu_item_state =0 ;

    //com.google.android.material.appbar.CollapsingToolbarLayout
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    TextView tv_msg,tv_name,tv_location,tv_rec_title;
    ImageView image_profile;
    RestaurantProfile profile=RestaurantProfile.getInstance();
    CardView cardView;

    RecyclerView recyclerView;

    Users use=Users.getInstance();


    List<FoodRecyclerModel> foods =new ArrayList<>();
    FoodRecycler adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        toolbar=findViewById(R.id.toolbar);
        tv_msg=findViewById(R.id.message_restaurant);
        // tv_name=findViewById(R.id.card_text_name_restaurant);
        //tv_location=findViewById(R.id.card_text_location_restaurant);
        image_profile=findViewById(R.id.image_restaurant_profile_restaurant);
        // cardView=findViewById(R.id.card_profile_restaurant);
        recyclerView=findViewById(R.id.recycler_food_restaurant);
        tv_rec_title=findViewById(R.id.textView_recycler_title_restaurant);
        collapsingToolbarLayout=findViewById(R.id.collapsing_restaurants);
        appBarLayout=findViewById(R.id.appbar_restaurant);





        Users users=Users.getInstance();

       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(profile.getName() != null){
                    tv_msg.setVisibility(View.INVISIBLE);


                    Glide.with(getApplicationContext()).load(profile.getUrl()).fitCenter().into(image_profile);
                    //toolbar.setTitle();
                    collapsingToolbarLayout.setTitle(profile.getName());



                }else {
                    tv_rec_title.setVisibility(View.INVISIBLE);
                    //cardView.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    appBarLayout.setExpanded(false);
                }




            }
        },300);



        getRecyclerData();







        setSupportActionBar(toolbar);
        //Firebase

        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();


    }

    private void getRecyclerData() {




        collectionFoodReference.whereEqualTo("restaurantID",use.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    //getting all snapshots
                    meu_item_state=1;

                    for (QueryDocumentSnapshot snapshot : value){

                        FoodRecyclerModel food =snapshot.toObject(FoodRecyclerModel.class);
                        foods.add(food);


                    }
                    initRecycler();




                }

            }


        });



    }

    private void initRecycler() {
        adapter=new FoodRecycler(this, foods);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu,menu);

        if (meu_item_state==1){
            MenuItem item1=menu.findItem(R.id.menu_restaurant_profile);
                    item1.setVisible(false);




        }else {


        }



        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int casee = item.getItemId();


        if(casee ==R.id.menu_restaurant_profile){

           Intent intent =new Intent(Restaurants.this, RestaurantProfileCreation.class);
           startActivity(intent);


        } else if (casee == R.id.menu_restaurant_add) {


            //tomorrow continue here and create the add food activity :)
            Intent intent=new Intent(Restaurants.this,AddFood.class);
            startActivity(intent);






        } else if (casee == R.id.menu_restaurant_signout) {
            firebaseAuth.signOut();
            RestaurantProfile resUser =RestaurantProfile.getInstance();

            profile.setCity(null);
            profile.setDescription(null);
            profile.setLocation(null);
            profile.setName(null);
            profile.setRate(0);
            profile.setPhoneNum(null);
            profile.setParent_user_id(null);
            profile.setUrl(null);
            profile.setWorkTime_from(null);
            profile.setWorkTime_to(null);

            startActivity(new Intent(Restaurants.this,MainActivity.class));
            finish();


        } else if (casee==R.id.menu_restaurant_edit_profile) {

            startActivity(new Intent(Restaurants.this,UpdateRestaurantProfile.class));




        } else if (casee==R.id.menu_restaurant_orders) {

            startActivity(new Intent(Restaurants.this,OrderActivity.class));




        }else if (casee==R.id.menu_restaurant_history) {

            startActivity(new Intent(Restaurants.this, PointsHistory.class));




        }else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Users users=Users.getInstance();
        current_user=users.getUsername();
        current_user_id=users.getUid();


        collectionReference.whereEqualTo("parent_user_id",current_user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(!value.isEmpty()){
                    //getting all snapshots
                    meu_item_state=1;
                    for (QueryDocumentSnapshot snapshot : value){
                        RestaurantProfile restaurantProfile =RestaurantProfile.getInstance();
                        restaurantProfile.setName(snapshot.getString("name"));
                        restaurantProfile.setCity(snapshot.getString("city"));
                        restaurantProfile.setLocation(snapshot.getString("location"));
                        restaurantProfile.setWorkTime_from(snapshot.getString("workTime_from"));
                        restaurantProfile.setWorkTime_to(snapshot.getString("workTime_to"));
                        restaurantProfile.setRate(snapshot.getDouble("rate"));
                        restaurantProfile.setPhoneNum(snapshot.getString("phoneNum"));
                        restaurantProfile.setStateOfReserve(snapshot.getString("stateOfReserve"));
                        restaurantProfile.setUrl(snapshot.getString("url"));
                        restaurantProfile.setDescription(snapshot.getString("description"));
                        restaurantProfile.setResuid(current_user_id);
                        restaurantProfile.setParent_user_id(current_user_id);

                    }





                }


            }
        });









    }
}