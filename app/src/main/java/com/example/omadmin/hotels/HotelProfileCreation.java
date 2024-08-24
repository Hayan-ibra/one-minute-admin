package com.example.omadmin.hotels;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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

public class HotelProfileCreation extends AppCompatActivity {

    EditText edt_name,edt_location,edt_phone,edt_description,edt_city;
    private Uri imageUri;
    ProgressBar progressBar;

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("HotelOwnersN");
    private StorageReference storageReference=FirebaseStorage.getInstance().getReference();;

    ImageView iv_profile;
    Button btn_create;
    ActivityResultLauncher<String>  arl;
    Users user=Users.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile_creation);
        edt_name=findViewById(R.id.HotelProfileCreation_edittext_name);
        edt_location=findViewById(R.id.HotelProfileCreation_edittext_location);
        edt_phone=findViewById(R.id.HotelProfileCreation_edittext_phone);
        edt_description=findViewById(R.id.HotelProfileCreation_descrition);
        iv_profile=findViewById(R.id.HotelProfileCreation_image_profile);
        btn_create=findViewById(R.id.HotelProfileCreation_button_create);
        progressBar=findViewById(R.id.HotelProfileCreation_progressBar);
        edt_city=findViewById(R.id.HotelProfileCreation_edittext_city);
        registerForARL();


        progressBar.setVisibility(View.GONE);


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri!=null){
                    if(!TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_description.getText().toString())
                            && !TextUtils.isEmpty(edt_location.getText().toString()) && !TextUtils.isEmpty(edt_phone.getText().toString())
                            && !TextUtils.isEmpty(edt_city.getText().toString())){

                        progressBar.setVisibility(View.VISIBLE);
                        String name=edt_name.getText().toString();
                        String phone =edt_phone.getText().toString();
                        String description=edt_description.getText().toString();
                        String location=edt_location.getText().toString();
                        String hotelId=user.getUid();
                        String city=edt_city.getText().toString();
                        createHotelProfile(name,phone,description,location,hotelId,city);
                    }else {
                        Toast.makeText(HotelProfileCreation.this, "please fill all feilds", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(HotelProfileCreation.this, "Please select image", Toast.LENGTH_SHORT).show();
                }

            }
        });


        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                arl.launch("image/*");


            }
        });








    }

    private void createHotelProfile(String name, String phone, String description, String location, String hotelId ,String city) {
        collectionReference.whereEqualTo("hotelId",user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    final  StorageReference  filepaht =storageReference.child("profile_images")
                            .child("Hotel_image"+ Timestamp.now().getSeconds());
                    filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl=uri.toString();
                                    Hotels hotel=new Hotels();
                                    hotel.setHotelId(hotelId);
                                    hotel.setHotelName(name);
                                    hotel.setDescription(description);
                                    hotel.setRate(0.0);
                                    hotel.setImageUrl(imageUrl);
                                    hotel.setPhoneNumber(phone);
                                    hotel.setLocation(location);
                                    hotel.setCity(city);
                                    hotel.setNumberOfPreviews(0);

                                    collectionReference.add(hotel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Hotels hotel=Hotels.getInstance();
                                            hotel.setHotelId(hotelId);
                                            hotel.setHotelName(name);
                                            hotel.setDescription(description);
                                            hotel.setRate(0.0);
                                            hotel.setImageUrl(imageUrl);
                                            hotel.setPhoneNumber(phone);
                                            hotel.setLocation(location);
                                            hotel.setCity(city);
                                            hotel.setNumberOfPreviews(0);
                                            startActivity(new Intent(HotelProfileCreation.this,HotelsMainActivity.class));
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HotelProfileCreation.this, "Failed To Create Your Profile", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HotelProfileCreation.this, "Failed To upload image", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }else {

                    Toast.makeText(HotelProfileCreation.this, "You already have profile", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(HotelProfileCreation.this,HotelsMainActivity.class));
                    finish();

                }
            }
        });
        final  StorageReference  filepaht =storageReference.child("profile_images")
                .child("Hotel_image"+ Timestamp.now().getSeconds());


    }

    private void registerForARL() {
       arl = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                iv_profile.setImageURI(o);
                imageUri=o;

            }
        });


    }
}