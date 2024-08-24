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

import com.example.omadmin.R;

import com.example.omadmin.models.RestaurantProfile;
import com.example.omadmin.time_date.TimePickerFragment;
import com.example.omadmin.time_date.TimePickerFragment_to;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RestaurantProfileCreation extends AppCompatActivity {

    public  static TextView tv_from,tv_to;
    Button signup_button;
    ProgressBar progressBar;
    ImageView profile_img;

    EditText name_edt,city_edt,location_edt,phone_edt,desc_edt;
    TimePickerFragment timePickerFragment;


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

    public static  String time_from_int;
    public static String time_to_int;


    //key for result
    int GALLERY_KEY =10;

     int state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        tv_from=findViewById(R.id.from_time_text);
        tv_to=findViewById(R.id.to_time_text);
        //Ui Elements
        signup_button=findViewById(R.id.button_signup_signup_restaurant);
        profile_img=findViewById(R.id.imageView_profile_signup_restaurant);


        name_edt=findViewById(R.id.edittext_resname_signup_restaurant);
        phone_edt=findViewById(R.id.edittext_phonenum_signup_restaurant);
        city_edt=findViewById(R.id.edittext_city_signup_restaurant);
        location_edt=findViewById(R.id.edittext_location_signup_restaurant);
        desc_edt=findViewById(R.id.edittext_description_signup_restaurant);
        progressBar=findViewById(R.id.progressBar_food_profile_creation);
        progressBar.setVisibility(View.INVISIBLE);



        //Firebase

        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
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
                DialogFragment timePickerFrag =new TimePickerFragment();
                timePickerFrag.show(getSupportFragmentManager(),"pick time");


            }
        });
        tv_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFrag2 =new TimePickerFragment_to();
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

                    if(imageUri !=null ){

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



                            CreateRestaurantProfile(restaurantName,city,location,phone,description,timeOfWork_from,timeOfWork_to,parent_uid,state);

                        }else {
                            Toast.makeText(RestaurantProfileCreation.this, "please select time range", Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        Toast.makeText(RestaurantProfileCreation.this, "please select image", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    Toast.makeText(RestaurantProfileCreation.this, "please fill all feilds", Toast.LENGTH_SHORT).show();
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

    private void CreateRestaurantProfile(String restaurantName, String city, String location, String phone,
                                         String description, String timeOfWorkFrom, String timeOfWorkTo, String parentUid,int state) {



        collectionReference.whereEqualTo("parent_user_id",parentUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value.isEmpty()){


                    final  StorageReference  filepaht =storageReference.child("profile_images")
                            .child("my_image"+ Timestamp.now().getSeconds());
                    filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {




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

                                            progressBar.setVisibility(View.INVISIBLE);
                                            Intent intent=new Intent(RestaurantProfileCreation.this,Restaurants.class);
                                            startActivity(intent);
                                            finish();


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RestaurantProfileCreation.this, ""+e, Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RestaurantProfileCreation.this, ""+e, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });













                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RestaurantProfileCreation.this, "You Already Have Account", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RestaurantProfileCreation.this,Restaurants.class);
                    startActivity(intent);
                    finish();



                }


            }
        });










    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==GALLERY_KEY && resultCode==RESULT_OK){
            imageUri=data.getData();
            profile_img.setImageURI(imageUri);
        }

    }




    public void SSS(int i){
        state=i;
    }



}