package com.example.omadmin.shops;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.Store;
import com.example.omadmin.models.Users;
import com.example.omadmin.time_date.TimePickerFragmentForStoress;
import com.example.omadmin.time_date.TimePickerFragment_to_ForStores;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopProfileUpdate extends AppCompatActivity implements TimePickerFragmentForStoress.timeCallback, TimePickerFragment_to_ForStores.timeCallbackTo{
    Chip chip_clothing,chip_accessories,chip_footwear,chip_seasonal,chip_lifestyle,chip_technology;

    EditText edt_name,edt_location,edt_phone,edt_description;
    TextView tv_time_f,tv_time_t;
    Button btn_update;
    ImageView iv_profile;
    private Uri imageUri;
    Users user=Users.getInstance();

    Store store=Store.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("StoreOwnersN");
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();;

    String selected_time_from,selected_time_to;
    Set<String> selectedCategories = new HashSet<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile_update);
        chip_clothing=findViewById(R.id.chip_clothing_ShopProfileUpdate);
        chip_accessories=findViewById(R.id.chip_accessories_ShopProfileUpdate);
        chip_footwear=findViewById(R.id.chip_footwear_ShopProfileUpdate);
        chip_seasonal=findViewById(R.id.chip_seasonal_ShopProfileUpdate);
        chip_lifestyle=findViewById(R.id.chip_lifestyle_ShopProfileUpdate);
        chip_technology=findViewById(R.id.chip_technology_ShopProfileUpdate);
        iv_profile=findViewById(R.id.ShopProfileUpdate_image_profile);
        tv_time_f=findViewById(R.id.ShopProfileUpdate_time_from);
        tv_time_t=findViewById(R.id.ShopProfileUpdate_time_to);
        btn_update=findViewById(R.id.ShopProfileUpdate_button_create);
        edt_name=findViewById(R.id.ShopProfileUpdate_edittext_name);
        edt_phone=findViewById(R.id.ShopProfileUpdate_edittext_phone);
        edt_description=findViewById(R.id.ShopProfileUpdate_descrition);
        edt_location=findViewById(R.id.ShopProfileUpdate_edittext_location);
        setChipListener(chip_clothing, "clothing");
        setChipListener(chip_accessories, "accessories");
        setChipListener(chip_footwear, "footwear");
        setChipListener(chip_seasonal, "seasonal");
        setChipListener(chip_lifestyle, "lifestyle");
        setChipListener(chip_technology, "technology");
        fillMyProfile();



        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_location.getText().toString())
                        && !TextUtils.isEmpty(edt_phone.getText().toString()) &&!TextUtils.isEmpty(edt_description.getText().toString())){

                    if(selected_time_from!=null && selected_time_to!=null){
                        String shopName=edt_name.getText().toString();
                        String location =edt_location.getText().toString();
                        String phone=edt_phone.getText().toString();
                        String description=edt_description.getText().toString();
                        //progressBar.setVisibility(View.VISIBLE);
                        List<String> categoryList = new ArrayList<>(selectedCategories);
                        String parent_uid=user.getUid();
                        //state= 0;


                        if (!categoryList.isEmpty()){
                            UpdateShopProfile(shopName,location,phone,description,selected_time_from,selected_time_to,parent_uid,categoryList);

                        }else {
                            Toast.makeText(ShopProfileUpdate.this, "Please select at least one category", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(ShopProfileUpdate.this, "please select time range", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    Toast.makeText(ShopProfileUpdate.this, "please fill all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,10);
            }
        });

        tv_time_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFrag =new TimePickerFragmentForStoress(ShopProfileUpdate.this);
                timePickerFrag.show(getSupportFragmentManager(),"pick time");
            }
        });
        tv_time_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFrag2 =new TimePickerFragment_to_ForStores(ShopProfileUpdate.this);
                timePickerFrag2.show(getSupportFragmentManager(),"pick time");

            }
        });




    }

    private void UpdateShopProfile(String shopName, String location, String phone, String description, String selectedTimeFrom,
                                   String selectedTimeTo, String parentUid, List<String> categoryList) {
        btn_update.setEnabled(false);
        btn_update.setClickable(false);
        if(imageUri!=null){
            final  StorageReference  filepaht =storageReference.child("profile_images")
                    .child("store_image"+ Timestamp.now().getSeconds());
            filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            Store store=new Store();
                            store.setDescription(description);
                            store.setStoreName(shopName);
                            store.setStoreId(parentUid);
                            store.setStoreType(categoryList);
                            store.setLocation(location);
                            store.setImageUrl(imageUrl);
                            store.setWorkTimeFrom(selectedTimeFrom);
                            store.setWorkTimeTo(selectedTimeTo);
                            store.setPhoneNum(phone);
                            collectionReference.whereEqualTo("storeId",parentUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(!value.isEmpty()){
                                        DocumentSnapshot snapshot = value.getDocuments().get(0);
                                        DocumentReference documentReference =snapshot.getReference();
                                        documentReference.set(store).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ShopProfileUpdate.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                Store store1=Store.getInstance();
                                                store1.setDescription(description);
                                                store1.setStoreName(shopName);
                                                store1.setStoreId(parentUid);
                                                store1.setStoreType(categoryList);
                                                store1.setLocation(location);
                                                store1.setImageUrl(imageUrl);
                                                store1.setWorkTimeFrom(selectedTimeFrom);
                                                store1.setWorkTimeTo(selectedTimeTo);
                                                store1.setPhoneNum(phone);
                                                startActivity(new Intent(ShopProfileUpdate.this, ShoppingMainActivity.class));


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ShopProfileUpdate.this, ""+e, Toast.LENGTH_SHORT).show();
                                            }
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
                    Toast.makeText(ShopProfileUpdate.this, "Failde to upload image", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            String imageUrl=store.getImageUrl();
            Store store=new Store();
            store.setDescription(description);
            store.setStoreName(shopName);
            store.setStoreId(parentUid);
            store.setStoreType(categoryList);
            store.setLocation(location);
            store.setImageUrl(imageUrl);
            store.setWorkTimeFrom(selectedTimeFrom);
            store.setWorkTimeTo(selectedTimeTo);
            store.setPhoneNum(phone);
            collectionReference.whereEqualTo("storeId",parentUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(!value.isEmpty()){
                        DocumentSnapshot snapshot = value.getDocuments().get(0);
                        DocumentReference documentReference =snapshot.getReference();
                        documentReference.set(store).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ShopProfileUpdate.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                Store store1=Store.getInstance();
                                store1.setDescription(description);
                                store1.setStoreName(shopName);
                                store1.setStoreId(parentUid);
                                store1.setStoreType(categoryList);
                                store1.setLocation(location);
                                store1.setImageUrl(imageUrl);
                                store1.setWorkTimeFrom(selectedTimeFrom);
                                store1.setWorkTimeTo(selectedTimeTo);
                                store1.setPhoneNum(phone);
                                startActivity(new Intent(ShopProfileUpdate.this, ShoppingMainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShopProfileUpdate.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });





        }



    }


    private void fillMyProfile() {
        edt_name.setText(store.getStoreName());
        edt_location.setText(store.getLocation());
        edt_phone.setText(store.getPhoneNum());
        edt_description.setText(store.getDescription());
        tv_time_f.setText(store.getWorkTimeFrom());
        tv_time_t.setText(store.getWorkTimeTo());
        selected_time_from=store.getWorkTimeFrom();
        selected_time_to=store.getWorkTimeTo();
        Glide.with(this).load(store.getImageUrl()).fitCenter().into(iv_profile);

    }

    private void setChipListener(Chip chip, final String category) {
        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==10 && resultCode==RESULT_OK){
            imageUri=data.getData();
            iv_profile.setImageURI(imageUri);
        }

    }

    @Override
    public void onTimeSetFrom(String timeFrom) {
        selected_time_from=timeFrom;
        tv_time_f.setText(timeFrom);
    }

    @Override
    public void onTimeSetTo(String timeTo) {
        selected_time_to=timeTo;
        tv_time_t.setText(timeTo);

    }
}