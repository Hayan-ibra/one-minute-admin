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

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class HotelProfileUpdate extends AppCompatActivity {
    EditText edt_name,edt_location,edt_phone,edt_description,edt_city;
    private Uri imageUri;
    ProgressBar progressBar;

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("HotelOwnersN");
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();

    ImageView iv_profile;
    Button btn_update;
    ActivityResultLauncher<String> arl;
    Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_profile_update);
        edt_name=findViewById(R.id.HotelProfileUpdate_edittext_name);
        edt_location=findViewById(R.id.HotelProfileUpdate_edittext_location);
        edt_phone=findViewById(R.id.HotelProfileUpdate_edittext_phone);
        edt_description=findViewById(R.id.HotelProfileUpdate_descrition);
        iv_profile=findViewById(R.id.HotelProfileUpdate_image_profile);
        btn_update=findViewById(R.id.HotelProfileUpdate_button_create);
        progressBar=findViewById(R.id.HotelProfileUpdate_progressBar);
        edt_city=findViewById(R.id.HotelProfileUpdate_edittext_city);
        users=Users.getInstance();
        Intent intent=getIntent();
        Hotels hotel= (Hotels) intent.getSerializableExtra("hotel");

        progressBar.setVisibility(View.GONE);
        registerForARL();
        fillData(hotel);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_location.getText().toString())
                        && !TextUtils.isEmpty(edt_phone.getText().toString()) && !TextUtils.isEmpty(edt_description.getText().toString())
                        && !TextUtils.isEmpty(edt_city.getText().toString())){
                    progressBar.setVisibility(View.VISIBLE);
                    String name=edt_name.getText().toString();
                    String phone=edt_phone.getText().toString();
                    String location=edt_location.getText().toString();
                    String description =edt_description.getText().toString();
                    String city=edt_city.getText().toString();
                    Double rate=hotel.getRate();
                    Long numberOfPreviews=hotel.getNumberOfPreviews();


                    updateProfile(name,phone,location,description,hotel,city,rate,numberOfPreviews);


                }else {
                    Toast.makeText(HotelProfileUpdate.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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

    private void updateProfile(String name, String phone, String location, String description, Hotels hotel00, String city, Double rate, Long numberOfPreviews) {
        if (imageUri != null) {
            final StorageReference filepaht = storageReference.child("profile_images")
                    .child("Hotel_image" + Timestamp.now().getSeconds());
            filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Hotels hotel = new Hotels();
                            hotel.setHotelId(users.getUid());
                            hotel.setHotelName(name);
                            hotel.setDescription(description);
                            hotel.setRate(rate);
                            hotel.setImageUrl(imageUrl);
                            hotel.setPhoneNumber(phone);
                            hotel.setLocation(location);
                            hotel.setCity(city);
                            hotel.setNumberOfPreviews(numberOfPreviews);
                            collectionReference.whereEqualTo("hotelId", users.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (!value.isEmpty()) {
                                        DocumentSnapshot snapshot = value.getDocuments().get(0);
                                        DocumentReference documentReference = snapshot.getReference();

                                        db.runTransaction((Transaction.Function<Void>) transaction -> {
                                            DocumentSnapshot snapshot11 = transaction.get(documentReference);
                                            Map<String, Object> newObject = new HashMap<>();
                                            newObject.put("hotelName", name);
                                            newObject.put("hotelId", users.getUid());
                                            newObject.put("ImageUrl", imageUrl);
                                            newObject.put("location",location);
                                            newObject.put("rate",rate);
                                            newObject.put("phoneNumber",phone);
                                            newObject.put("description",description);
                                            newObject.put("city",city);
                                            newObject.put("numberOfPreviews",numberOfPreviews);

                                            // Update the field (e.g., incrementing a counter)

                                            transaction.set(documentReference,newObject);

                                            return null;
                                        }).addOnSuccessListener(aVoid -> {
                                            progressBar.setVisibility(View.GONE);
                                            // Transaction success
                                            Toast.makeText(HotelProfileUpdate.this, "success", Toast.LENGTH_SHORT).show();
                                            Hotels hotel1 = Hotels.getInstance();
                                            hotel1.setHotelId(users.getUid());
                                            hotel1.setHotelName(name);
                                            hotel1.setDescription(description);
                                            hotel1.setRate(rate);
                                            hotel1.setImageUrl(imageUrl);
                                            hotel1.setPhoneNumber(phone);
                                            hotel1.setLocation(location);
                                            hotel1.setCity(city);
                                            hotel1.setNumberOfPreviews(numberOfPreviews);
                                            Toast.makeText(HotelProfileUpdate.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(HotelProfileUpdate.this, HotelsMainActivity.class));
                                        }).addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.GONE);
                                            // Transaction failure
                                            Toast.makeText(HotelProfileUpdate.this, "failure", Toast.LENGTH_SHORT).show();
                                        });


                                    }
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HotelProfileUpdate.this, "Failed To Upload Image ", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Hotels hotel = new Hotels();
            hotel.setHotelId(users.getUid());
            hotel.setHotelName(name);
            hotel.setDescription(description);
            hotel.setRate(rate);
            hotel.setImageUrl(hotel00.getImageUrl());
            hotel.setPhoneNumber(phone);
            hotel.setLocation(location);
            hotel.setNumberOfPreviews(numberOfPreviews);


            collectionReference.whereEqualTo("hotelId", users.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (!value.isEmpty()) {
                        DocumentSnapshot snapshot = value.getDocuments().get(0);
                        DocumentReference documentReference = snapshot.getReference();

                        db.runTransaction((Transaction.Function<Void>) transaction -> {
                            DocumentSnapshot snapshot11 = transaction.get(documentReference);
                            Map<String, Object> newObject = new HashMap<>();
                            newObject.put("hotelName", name);
                            newObject.put("hotelId", users.getUid());
                            newObject.put("ImageUrl", hotel00.getImageUrl());
                            newObject.put("location", location);
                            newObject.put("rate", rate);
                            newObject.put("phoneNumber", phone);
                            newObject.put("description", description);
                            newObject.put("city",city);
                            newObject.put("numberOfPreviews",numberOfPreviews);




                            transaction.set(documentReference, newObject);

                            return null;
                        }).addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            // Transaction success
                            Toast.makeText(HotelProfileUpdate.this, "success", Toast.LENGTH_SHORT).show();
                            Hotels hotel1 = Hotels.getInstance();
                            hotel1.setHotelId(users.getUid());
                            hotel1.setHotelName(name);
                            hotel1.setDescription(description);
                            hotel1.setRate(rate);
                            hotel1.setImageUrl(hotel00.getImageUrl());
                            hotel1.setPhoneNumber(phone);
                            hotel1.setLocation(location);
                            hotel1.setCity(city);
                            hotel1.setNumberOfPreviews(numberOfPreviews);
                            Toast.makeText(HotelProfileUpdate.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HotelProfileUpdate.this, HotelsMainActivity.class));
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            // Transaction failure
                            Toast.makeText(HotelProfileUpdate.this, "failure", Toast.LENGTH_SHORT).show();
                        });


                    }
                }
            });












        }















    }













    private void fillData(Hotels hotels) {
        edt_description.setText(hotels.getDescription());
        edt_name.setText(hotels.getHotelName());
        edt_location.setText(hotels.getLocation());
        edt_phone.setText(hotels.getPhoneNumber());
        edt_city.setText(hotels.getCity());
        Glide.with(HotelProfileUpdate.this).load(hotels.getImageUrl()).into(iv_profile);

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