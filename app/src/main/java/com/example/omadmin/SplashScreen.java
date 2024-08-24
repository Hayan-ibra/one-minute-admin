package com.example.omadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.omadmin.hotels.HotelsMainActivity;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShoppingMainActivity;
import com.example.omadmin.tourist.TouristMainActivity;
import com.example.omadmin.uploadingfood.Restaurants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();;

    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser1;

    //firebase connection
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null){



            currentUser1= firebaseAuth.getCurrentUser();

            final String currentUserId =currentUser1.getUid();
            Users users =Users.getInstance();
            users.setUid(currentUserId);





            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    collectionReference.whereEqualTo("uid",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if(!value.isEmpty()){
                                //getting all snapshots
                                for (QueryDocumentSnapshot snapshot : value){

                                    Users users =Users.getInstance();
                                    users.setUsername(snapshot.getString("username"));
                                    users.setUid(snapshot.getString("uid"));
                                    users.setUrl(snapshot.getString("url"));
                                    users.setPoints(snapshot.getDouble("points"));
                                    users.setGender(snapshot.getString("gender"));
                                    users.setPhoneNum(snapshot.getString("phoneNum"));
                                    users.setAccountType(snapshot.getString("accountType"));
                                    users.setEmail(snapshot.getString("email"));




                                    //display list of journals after log in


                                    if(users.getAccountType().equals("restaurant")){

                                        startActivity(new Intent(SplashScreen.this, Restaurants.class));
                                        finish();

                                    }else if (users.getAccountType().equals("hotel")) {
                                        startActivity(new Intent(SplashScreen.this, HotelsMainActivity.class));
                                        finish();
                                    } else if (users.getAccountType().equals("shop")) {
                                        startActivity(new Intent(SplashScreen.this, ShoppingMainActivity.class));
                                        finish();

                                    } else if (users.getAccountType().equals("tourist")) {
                                        startActivity(new Intent(SplashScreen.this, TouristMainActivity.class));
                                        finish();


                                    }


                                }


                            }
                        }
                    });
                }
            },500);








        }else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }
    }
}