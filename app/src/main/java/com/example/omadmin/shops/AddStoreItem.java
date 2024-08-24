package com.example.omadmin.shops;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class AddStoreItem extends AppCompatActivity {
    Chip ch_male,ch_female,ch_kidsss,ch_tops,ch_bottoms,ch_shorts, ch_sportwear,ch_jewerly,ch_watches,ch_bags,ch_athletic_shoes,ch_boots,ch_sandals,ch_outerwear
            ,ch_swimwear,ch_sleepwear,ch_work,ch_formalwear,ch_winter, ch_underwear,ch_smartwatch,ch_fitness,ch_trackers;
    EditText edt_name,edt_price,edt_quantity;

    ImageView profile;
    Button btn_add;

    Set<String> selectedCategories = new HashSet<>();
    Set<String> selectedGenders = new HashSet<>();
    ActivityResultLauncher<String> arl;
    Users user=Users.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("StoreItems");
    private StorageReference storageReference =FirebaseStorage.getInstance().getReference();;





    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store_item);
        ch_male=findViewById(R.id.chip_male);
        ch_female=findViewById(R.id.chip_female);
        ch_kidsss=findViewById(R.id.chip_kids_target);
        ch_tops=findViewById(R.id.chip_tops);
        ch_bottoms=findViewById(R.id.chip_bottoms);
        ch_shorts=findViewById(R.id.chip_Shorts);

        ch_sportwear=findViewById(R.id.chip_sportwear);
        ch_jewerly=findViewById(R.id.chip_jewerly);
        ch_watches=findViewById(R.id.chip_watches);
        ch_bags=findViewById(R.id.chip_bags);
        ch_athletic_shoes=findViewById(R.id.chip_athletic);
        ch_boots=findViewById(R.id.chip_boots);
        ch_sandals=findViewById(R.id.chip_sandals);
        ch_winter=findViewById(R.id.chip_winter);
        ch_outerwear=findViewById(R.id.chip_owterwear);
        ch_swimwear=findViewById(R.id.chip_swimwear);
        ch_sleepwear=findViewById(R.id.chip_sleep_wear);


        ch_work=findViewById(R.id.chip_work);
        ch_formalwear=findViewById(R.id.chip_formalwear);
        ch_underwear=findViewById(R.id.chip_underwear);
        ch_smartwatch=findViewById(R.id.chip_smartwatch);
        ch_fitness=findViewById(R.id.chip_fitness);
        ch_trackers=findViewById(R.id.chip_trackers);
        profile=findViewById(R.id.AddStoreItem_image_item);
        edt_name=findViewById(R.id.AddStoreItem_edittext_name_item);
        edt_price=findViewById(R.id.AddStoreItem_edittext_price_item);
        edt_quantity=findViewById(R.id.AddStoreItem_edittext_quantity_item);
        btn_add=findViewById(R.id.AddStoreItem_button_add);
        setChip();
        RegisterForARL();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arl.launch("image/*");
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_price.getText().toString())
                        && !TextUtils.isEmpty(edt_quantity.getText().toString()) ){
                    if (!selectedGenders.isEmpty()){

                        if (!selectedCategories.isEmpty()){

                            if(imageUri!=null){
                                String name =edt_name.getText().toString();
                                String price=edt_price.getText().toString();
                                String quantity=edt_quantity.getText().toString();
                                String parentId=user.getUid();
                                String itemID= UUID.randomUUID().toString();
                                List<String> categoryList = new ArrayList<>(selectedCategories);
                                List<String> genderList = new ArrayList<>(selectedGenders);
                                addItemToStore(name,price,quantity,parentId,itemID,categoryList,genderList);




                            }else {
                                Toast.makeText(AddStoreItem.this, "please select item image", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(AddStoreItem.this, "please select category", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(AddStoreItem.this, "please select at least one gender", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(AddStoreItem.this, "Please fill all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });






    }

    private void addItemToStore(String name, String price, String quantity, String parentId, String itemID, List<String> categoryList, List<String> genderList) {
        final  StorageReference  filepaht =storageReference.child("items_images")
                .child("item_"+ Timestamp.now().getSeconds());
        filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl=uri.toString();
                        StoreItem item=new StoreItem();
                        item.setImageUrl(imageUrl);
                        item.setItemId(itemID);
                        item.setParentId(parentId);
                        item.setItemName(name);
                        item.setPrice(Double.valueOf(price));
                        item.setQuantity(Long.valueOf(quantity));
                        item.setCatigory(categoryList);
                        item.setGender(genderList);
                        collectionReference.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddStoreItem.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddStoreItem.this, ShoppingMainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddStoreItem.this, "Failed", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddStoreItem.this, ShoppingMainActivity.class));
                                finish();
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStoreItem.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setChip() {
        setChipListenerGender(ch_male,"male");
        setChipListenerGender(ch_female,"female");
        setChipListenerGender(ch_kidsss,"kids");

        setChipListener(ch_tops,"tops");
        setChipListener(ch_bottoms,"bottoms");
        setChipListener(ch_shorts,"shorts");
        setChipListener(ch_sportwear,"sportswear");
        setChipListener(ch_jewerly,"jewelry");
        setChipListener(ch_watches,"watches");
        setChipListener(ch_bags,"bags");
        setChipListener(ch_athletic_shoes,"shoes");
        setChipListener(ch_boots,"boots");
        setChipListener(ch_sandals,"sandals");
        setChipListener(ch_winter,"winter_shoes");
        setChipListener(ch_outerwear,"outerwear");
        setChipListener(ch_swimwear,"swimwear");
        setChipListener(ch_sleepwear,"sleepwear");

        setChipListener(ch_work,"workwear");
        setChipListener(ch_formalwear,"formalwear");
        setChipListener(ch_underwear,"underwear");
        setChipListener(ch_smartwatch,"smartwatch");
        setChipListener(ch_fitness,"fitness");
        setChipListener(ch_trackers,"trackers");

    }


    private  void  RegisterForARL(){

        arl = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                profile.setImageURI(o);
                imageUri=o;

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
    private void setChipListenerGender(Chip chip, final String category) {
        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedGenders.add(category);
                } else {
                    selectedGenders.remove(category);
                }
            }
        });
    }

}