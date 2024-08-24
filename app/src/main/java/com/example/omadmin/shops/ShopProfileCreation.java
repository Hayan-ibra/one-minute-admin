package com.example.omadmin.shops;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.Store;
import com.example.omadmin.models.Users;
import com.example.omadmin.time_date.TimePickerFragmentForStoress;
import com.example.omadmin.time_date.TimePickerFragment_to_ForStores;
import com.example.omadmin.time_date.TimePickerUpdate_from;
import com.example.omadmin.uploadingfood.RestaurantProfileCreation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ShopProfileCreation extends AppCompatActivity implements TimePickerFragmentForStoress.timeCallback, TimePickerFragment_to_ForStores.timeCallbackTo {

    Chip chip_clothing,chip_accessories,chip_footwear,chip_seasonal,chip_lifestyle,chip_technology;

    EditText edt_name,edt_location,edt_phone,edt_description;
    TextView tv_time_f,tv_time_t;
    Button btn_create;
    ImageView iv_profile;
    private Uri imageUri;
    Users user=Users.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("StoreOwnersN");
    private StorageReference storageReference;

    String selected_time_from,selected_time_to;
    Set<String> selectedCategories = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile_creation);
        chip_clothing=findViewById(R.id.chip_clothing);
        chip_accessories=findViewById(R.id.chip_accessories);
        chip_footwear=findViewById(R.id.chip_footwear);
        chip_seasonal=findViewById(R.id.chip_seasonal);
        chip_lifestyle=findViewById(R.id.chip_lifestyle);
        chip_technology=findViewById(R.id.chip_technology);
        iv_profile=findViewById(R.id.ShopProfileCreation_image_profile);
        tv_time_f=findViewById(R.id.ShopProfileCreation_time_from);
        tv_time_t=findViewById(R.id.ShopProfileCreation_time_to);
        btn_create=findViewById(R.id.ShopProfileCreation_button_create);
        edt_name=findViewById(R.id.ShopProfileCreation_edittext_name);
        edt_phone=findViewById(R.id.ShopProfileCreation_edittext_phone);
        edt_description=findViewById(R.id.ShopProfileCreation_descrition);
        edt_location=findViewById(R.id.ShopProfileCreation_edittext_location);
        setChipListener(chip_clothing, "clothing");
        setChipListener(chip_accessories, "accessories");
        setChipListener(chip_footwear, "footwear");
        setChipListener(chip_seasonal, "seasonal");
        setChipListener(chip_lifestyle, "lifestyle");
        setChipListener(chip_technology, "technology");
        //


        storageReference= FirebaseStorage.getInstance().getReference();
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
                DialogFragment timePickerFrag =new TimePickerFragmentForStoress(ShopProfileCreation.this);
                timePickerFrag.show(getSupportFragmentManager(),"pick time");
            }
        });
        tv_time_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFrag2 =new TimePickerFragment_to_ForStores(ShopProfileCreation.this);
                timePickerFrag2.show(getSupportFragmentManager(),"pick time");

            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_location.getText().toString())
                        && !TextUtils.isEmpty(edt_phone.getText().toString()) &&!TextUtils.isEmpty(edt_description.getText().toString())){

                    if(imageUri !=null ){

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
                                CreateShopProfile(shopName,location,phone,description,selected_time_from,selected_time_to,parent_uid,categoryList);

                            }else {
                                Toast.makeText(ShopProfileCreation.this, "Please select at least one category", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(ShopProfileCreation.this, "please select time range", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(ShopProfileCreation.this, "please select image", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(ShopProfileCreation.this, "please fill all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    private void CreateShopProfile(String shopName, String location, String phone, String description,
                                   String selectedTimeFrom1, String selectedTimeTo1, String parentUid, List<String> categoryList) {

        /**/
        collectionReference.whereEqualTo("storeId",parentUid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){

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
                                    store.setWorkTimeFrom(selectedTimeFrom1);
                                    store.setWorkTimeTo(selectedTimeTo1);
                                    store.setPhoneNum(phone);
                                    collectionReference.add(store).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Store store1=Store.getInstance();
                                            store1.setDescription(description);
                                            store1.setStoreName(shopName);
                                            store1.setStoreId(parentUid);
                                            store1.setStoreType(categoryList);
                                            store1.setLocation(location);
                                            store1.setImageUrl(imageUrl);
                                            store1.setWorkTimeFrom(selectedTimeFrom1);
                                            store1.setWorkTimeTo(selectedTimeTo1);
                                            store1.setPhoneNum(phone);

                                            ShoppingMainActivity.menu_State=1;

                                            startActivity( new Intent(ShopProfileCreation.this, ShoppingMainActivity.class));


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ShopProfileCreation.this, "Failed To Upload Data", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShopProfileCreation.this, "Failed To Upload Image", Toast.LENGTH_SHORT).show();
                        }
                    });


                }else {
                    Toast.makeText(ShopProfileCreation.this, "You Already Have Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });



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