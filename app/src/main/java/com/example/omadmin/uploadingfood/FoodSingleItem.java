package com.example.omadmin.uploadingfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.models.Food;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

public class FoodSingleItem extends AppCompatActivity {

    MaterialToolbar toolbar;

    FloatingActionButton fab;



    //firebase authentication

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //firebase firestore

    private FirebaseFirestore db =FirebaseFirestore.getInstance();

    private CollectionReference collectionReference=db.collection("Foods");


    //firebase storage for image storage
    private StorageReference storageReference;


    String current_user;
    String current_user_id;

    int meu_item_state =0 ;

    //com.google.android.material.appbar.CollapsingToolbarLayout
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    Users user=Users.getInstance();
    String userId;

    ImageView image_profile;
    TextView card_tv_price,tv_name,card_tv_type,card_tv_time,tv_description,tv_ingredients;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_single_item);
        image_profile=findViewById(R.id.food_image_food_details);
        tv_description=findViewById(R.id.text_description_food_details);
        tv_ingredients=findViewById(R.id.text_ingredients_food_details);
        appBarLayout=findViewById(R.id.appbar_foood_details);
        collapsingToolbarLayout=findViewById(R.id.collapsing_food_details);


        card_tv_price=findViewById(R.id.text_card_price_food_details);
        ///card_tv_rate=findViewById(R.id.text_card_rate_food_details);
        card_tv_type=findViewById(R.id.text_card_type_food_details);
        card_tv_time=findViewById(R.id.text_card_time_food_details);
        tv_name=findViewById(R.id.text_name_food_details);
        fab=findViewById(R.id.floatingActionButton_food_details);




        Users user=Users.getInstance();
        userId=user.getUid();


        getFoodDetails();



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new
                        AlertDialog.Builder(FoodSingleItem.this).setTitle("Confirm Deletion").setMessage("do yo want to delete this food item ?")
                        .setIcon(R.drawable.baseline_delete_24).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Food food1 =Food.getInstance();
                                collectionReference.whereEqualTo("name",food1.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Get the reference to the document
                                                DocumentReference docRef = db.collection("Foods").document(document.getId());

                                                // Delete the document
                                                docRef.delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Document deleted successfully
                                                                startActivity(new Intent(FoodSingleItem.this,Restaurants.class));
                                                                finish();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Handle any errors
                                                                Toast.makeText(FoodSingleItem.this, "Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            // Handle errors while querying the collection
                                        }





                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(FoodSingleItem.this, "could not find item", Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                dialog.show();





            }
        });

    }

    private void getFoodDetails() {
        Intent intent=getIntent();
        String foodName =intent.getStringExtra("1");

        collectionReference.whereEqualTo("name",foodName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){

                    Food food =Food.getInstance();

                    for (QueryDocumentSnapshot snapshot : value){


                        food.setIngredients(snapshot.getString("ingredients"));
                        food.setName(snapshot.getString("name"));
                        food.setFoodUrl(snapshot.getString("foodUrl"));
                        food.setType(snapshot.getString("type"));
                        food.setDescription(snapshot.getString("description"));
                        food.setRate(snapshot.getDouble("rate"));
                        food.setPrice(snapshot.getDouble("price"));
                        food.setTime(snapshot.getString("time"));




                    }
                    tv_description.setText(food.getDescription());
                    tv_ingredients.setText(food.getIngredients());
                    card_tv_price.setText(food.getPrice().toString()+"$");
                   // card_tv_rate.setText(food.getRate().toString());
                    card_tv_time.setText(food.getTime());
                    card_tv_type.setText(food.getType());
                    Glide.with(FoodSingleItem.this).load(food.getFoodUrl()).fitCenter().into(image_profile);
                    //collapsingToolbarLayout.setTitle(food.getName());

                    tv_name.setText(food.getName());







                }
            }
        });




    }


























}