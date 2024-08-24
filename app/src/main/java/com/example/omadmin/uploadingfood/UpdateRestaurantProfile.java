package com.example.omadmin.uploadingfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.RestaurantProfile;
import com.example.omadmin.time_date.TimePickerFragment;
import com.example.omadmin.time_date.TimePickerFragment_to;
import com.example.omadmin.time_date.TimePickerUpdate_from;
import com.example.omadmin.time_date.TimePickerUpdate_to;
import com.example.omadmin.time_date.TimeSelected;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateRestaurantProfile extends AppCompatActivity {

    public  static TextView tv_from,tv_to;
    Button signup_button;
    ProgressBar progressBar;
    ImageView profile_img;

    EditText name_edt,city_edt,location_edt,phone_edt,desc_edt;





    //firebase authentication

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //firebase firestore

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("RestaurantOwners");

    //firebase storage for image storage
    private StorageReference storageReference;
    private Uri imageUri;



    //key for result
    int GALLERY_KEY =10;

    int state;
    TimeSelected time= TimeSelected.getInstance();

    RestaurantProfile profile=RestaurantProfile.getInstance();

    public static  String time_from_int;
    public static String time_to_int;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_retaurant_profile);

        tv_from=findViewById(R.id.from_time_text_update);
        tv_to=findViewById(R.id.to_time_text_update);
        //Ui Elements
        signup_button=findViewById(R.id.button_update_update_restaurant);
        profile_img=findViewById(R.id.imageView_profile_update_restaurant);
        name_edt=findViewById(R.id.edittext_resname_updte_restaurant);
        phone_edt=findViewById(R.id.edittext_phonenum_update_restaurant);
        city_edt=findViewById(R.id.edittext_city_update_restaurant);
        location_edt=findViewById(R.id.edittext_location_update_restaurant);
        desc_edt=findViewById(R.id.edittext_description_update_restaurant);
        progressBar=findViewById(R.id.progressBar_food_profile_update);

        firebaseAuth= FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();





        progressBar.setVisibility(View.INVISIBLE);
        fillMyProfile();




        //Authentication
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
                if(currentUser !=null){
                    //User Already Logged In



                }else{
                    //not user yet




                }


            }
        };

















        tv_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display time picker
                DialogFragment timePickerFrag =new TimePickerUpdate_from();
                timePickerFrag.show(getSupportFragmentManager(),"pick time");


            }
        });
        tv_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFrag2 =new TimePickerUpdate_to();
                timePickerFrag2.show(getSupportFragmentManager(),"pick time");

            }
        });


        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeSelected time= TimeSelected.getInstance();


                if(!TextUtils.isEmpty(name_edt.getText().toString()) && !TextUtils.isEmpty(city_edt.getText().toString())
                        && !TextUtils.isEmpty(location_edt.getText().toString()) &&!TextUtils.isEmpty(phone_edt.getText().toString())
                        &&!TextUtils.isEmpty(desc_edt.getText().toString())  ){



                    if(time.getTime_to()!=null && time.getTime_from()!=null){

                        String restaurantName=name_edt.getText().toString();
                        String city=city_edt.getText().toString();
                        String location =location_edt.getText().toString();
                        String phone=phone_edt.getText().toString();
                        String description=desc_edt.getText().toString();
                        String timeOfWork_from=time.getTime_from();
                        String timeOfWork_to=time.getTime_to();
                        progressBar.setVisibility(View.VISIBLE);

                        currentUser=firebaseAuth.getCurrentUser();
                        String parent_uid=currentUser.getUid();
                        state= 0;



                            UpdateProfile(restaurantName,city,location,phone,description,timeOfWork_from,timeOfWork_to,parent_uid,state);

                        }else {
                            Toast.makeText(UpdateRestaurantProfile.this, "please select time range", Toast.LENGTH_SHORT).show();
                        }





                }else {
                    Toast.makeText(UpdateRestaurantProfile.this, "please fill all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_KEY);
            }
        });














    }

    private void fillMyProfile() {
        name_edt.setText(profile.getName());

        city_edt.setText(profile.getCity());

        location_edt.setText(profile.getLocation());

        phone_edt.setText(profile.getPhoneNum());

        desc_edt.setText(profile.getDescription());
        tv_from.setText(profile.getWorkTime_from());
        tv_to.setText(profile.getWorkTime_to());

        Glide.with(this).load(profile.getUrl()).fitCenter().into(profile_img);

    }

    private void UpdateProfile(String restaurantName, String city, String location, String phone,
                               String description, String timeOfWorkFrom, String timeOfWorkTo, String parentUid, int state) {


        if(imageUri!=null){

            String currentImageUrl=profile.getUrl();
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentImageUrl);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    collectionReference.whereEqualTo("parent_user_id",parentUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Obtain a reference to the document
                                DocumentReference docRef = db.collection("RestaurantOwners").document(documentSnapshot.getId());

                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {



                                        final  StorageReference  filepaht =storageReference.child("profile_images")
                                                .child("my_image"+ Timestamp.now().getSeconds());
                                        filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl=uri.toString();

                                                        String image_url=uri.toString();
                                                        RestaurantProfile restaurantProfile=new RestaurantProfile();
                                                        restaurantProfile.setCity(city);
                                                        restaurantProfile.setDescription(description);
                                                        restaurantProfile.setLocation(location);
                                                        restaurantProfile.setName(restaurantName);
                                                        restaurantProfile.setRate(0.0);
                                                        restaurantProfile.setParent_user_id(parentUid);
                                                        restaurantProfile.setUrl(image_url);
                                                        restaurantProfile.setPhoneNum(phone);
                                                        restaurantProfile.setWorkTime_from(timeOfWorkFrom);
                                                        restaurantProfile.setWorkTime_to(timeOfWorkTo);
                                                        restaurantProfile.setTime_from_int(time_from_int);
                                                        restaurantProfile.setTime_to_int(time_to_int);



                                                        collectionReference.add(restaurantProfile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                RestaurantProfile res= RestaurantProfile.getInstance();
                                                                res.setCity(city);
                                                                res.setDescription(description);
                                                                res.setLocation(location);
                                                                res.setName(restaurantName);
                                                                res.setRate(0.0);
                                                                res.setParent_user_id(parentUid);
                                                                res.setUrl(image_url);
                                                                res.setPhoneNum(phone);
                                                                res.setWorkTime_from(timeOfWorkFrom);
                                                                res.setWorkTime_to(timeOfWorkTo);
                                                                restaurantProfile.setTime_from_int(time_from_int);
                                                                restaurantProfile.setTime_to_int(time_to_int);

                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                Intent intent=new Intent(UpdateRestaurantProfile.this,Restaurants.class);
                                                                startActivity(intent);
                                                                finish();

                                                            }
                                                        });



                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(UpdateRestaurantProfile.this, "could not retrive file path", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdateRestaurantProfile.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateRestaurantProfile.this, "could not delete", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateRestaurantProfile.this, "Failed to get", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateRestaurantProfile.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            });


        }else {






            collectionReference.whereEqualTo("parent_user_id",parentUid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Obtain a reference to the document
                        DocumentReference docRef = db.collection("RestaurantOwners").document(documentSnapshot.getId());

                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {


                                String imageUrl=profile.getUrl();
                                RestaurantProfile restaurantProfile=new RestaurantProfile();
                                restaurantProfile.setCity(city);
                                restaurantProfile.setDescription(description);
                                restaurantProfile.setLocation(location);
                                restaurantProfile.setName(restaurantName);
                                restaurantProfile.setRate(0.0);
                                restaurantProfile.setParent_user_id(parentUid);
                                restaurantProfile.setUrl(imageUrl);
                                restaurantProfile.setPhoneNum(phone);
                                restaurantProfile.setWorkTime_from(timeOfWorkFrom);
                                restaurantProfile.setWorkTime_to(timeOfWorkTo);
                                restaurantProfile.setTime_from_int(time_from_int);
                                restaurantProfile.setTime_to_int(time_to_int);



                                collectionReference.add(restaurantProfile).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        RestaurantProfile res= RestaurantProfile.getInstance();
                                        res.setCity(city);
                                        res.setDescription(description);
                                        res.setLocation(location);
                                        res.setName(restaurantName);
                                        res.setRate(0.0);
                                        res.setParent_user_id(parentUid);
                                        res.setUrl(imageUrl);
                                        res.setPhoneNum(phone);
                                        res.setWorkTime_from(timeOfWorkFrom);
                                        res.setWorkTime_to(timeOfWorkTo);
                                        restaurantProfile.setTime_from_int(time_from_int);
                                        restaurantProfile.setTime_to_int(time_to_int);

                                        progressBar.setVisibility(View.INVISIBLE);
                                        Intent intent=new Intent(UpdateRestaurantProfile.this,Restaurants.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                });



                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateRestaurantProfile.this, "could not delete", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateRestaurantProfile.this, "Failed to get", Toast.LENGTH_SHORT).show();
                }
            });








        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==GALLERY_KEY && resultCode==RESULT_OK){
            imageUri=data.getData();
            profile_img.setImageURI(imageUri);
        }

    }}