package com.example.omadmin.hotels;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.HotelRoom;
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
import java.util.Set;
import java.util.UUID;

public class AddRoom extends AppCompatActivity {
    Chip chip_single_room,chip_double_room,chip_twin_room,chip_suite,chip_family_room,chip_deluxe_room,
    chip_executive_room,chip_accessible_room;

    Chip chip_room_services,chip_laundry_service,chip_shuttle_service,chip_housekeeping_service,chip_spa_service,
    chip_fitness_service,chip_business_service,chip_event_hosting;
    ProgressBar progressBar;
    ImageView iv;
    EditText edt_price,edt_number;
    Button submit;

    Set<String> selectedRoomService =new HashSet<>();
    private String selectedRoomType=null;


    private Uri imageUri;
    ActivityResultLauncher<String> arl;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("HotelRooms");//TODO : asddddddddddddd
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Users user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        chip_single_room=findViewById(R.id.chip_single_room);
        chip_double_room=findViewById(R.id.chip_fdouble_room);
        chip_twin_room=findViewById(R.id.chip_twin_room);
        chip_suite=findViewById(R.id.chip_suite);
        chip_family_room=findViewById(R.id.chip_family_room);
        chip_deluxe_room=findViewById(R.id.chip_deluxe_room);
        chip_executive_room=findViewById(R.id.chip_executive_room);
        chip_accessible_room=findViewById(R.id.chip_accessible_room);


        chip_room_services=findViewById(R.id.chip_room_service);
        chip_laundry_service=findViewById(R.id.chip_laundry_service);
        chip_shuttle_service=findViewById(R.id.chip_shuttle_service);
        chip_housekeeping_service=findViewById(R.id.chip_housekeeping_service);
        chip_spa_service=findViewById(R.id.chip_spa_service);
        chip_fitness_service=findViewById(R.id.chip_fitness_Center_service);
        chip_business_service=findViewById(R.id.chip_business_center_service);
        chip_event_hosting=findViewById(R.id.chip_event_hosting_service);

        progressBar=findViewById(R.id.AddRoom_progressBar);
        iv=findViewById(R.id.AddRoom_image_room);
        edt_number=findViewById(R.id.AddRoom_edittext_room_number);
        edt_price=findViewById(R.id.AddRoom_edittext_price);
        submit=findViewById(R.id.AddRoom_button_add);
        progressBar.setVisibility(View.GONE);
        user=Users.getInstance();
        setChips();
        RegisterForARL();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arl.launch("image/*");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edt_number.getText().toString()) && !TextUtils.isEmpty(edt_price.getText().toString()) ){
                    if (imageUri!=null){
                        if (!selectedRoomService.isEmpty()){
                            if (selectedRoomType!=null){
                                progressBar.setVisibility(View.VISIBLE);
                                Double price=Double.valueOf(edt_price.getText().toString());
                                String roomNum=edt_number.getText().toString();
                                List<String> services=new ArrayList<>(selectedRoomService);
                                String roomId= UUID.randomUUID().toString();
                                String parentId=user.getUid();

                                uploadRoom(roomNum,roomId,parentId,imageUri,selectedRoomType,services,price);



                            }else {

                                Toast.makeText(AddRoom.this, "select one room type", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(AddRoom.this, "please select service", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AddRoom.this, "please select image ", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(AddRoom.this, "please fill all fields ", Toast.LENGTH_SHORT).show();
                }
            }
        });







    }

    private void uploadRoom(String roomNum, String roomId, String parentId, Uri imageUri, String selectedRoomType, List<String> services,  Double price) {

        final  StorageReference  filepaht =storageReference.child("rooms_images")
                .child("room_"+ Timestamp.now().getSeconds());
        filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl=uri.toString();
                        HotelRoom hotelRoom=new HotelRoom();
                        hotelRoom.setRoomId(roomId);
                        hotelRoom.setRoomNum(roomNum);
                        hotelRoom.setServices(services);
                        hotelRoom.setState(0);//0 not reserved     1 reserved
                        hotelRoom.setPricePerDay(price);
                        hotelRoom.setParentId(parentId);
                        hotelRoom.setType(selectedRoomType);
                        hotelRoom.setImageUrl(imageUrl);
                        Toast.makeText(AddRoom.this, "Enterer", Toast.LENGTH_SHORT).show();
                        collectionReference.add(hotelRoom).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddRoom.this, "Successfully created ", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(AddRoom.this,HotelsMainActivity.class));
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddRoom.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });



    }

    private void RegisterForARL() {
        arl = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                iv.setImageURI(o);
                imageUri=o;

            }
        });
    }


    private void setChips() {

        setSingleChipListener(chip_single_room,"Single Room");
        setSingleChipListener(chip_double_room,"Double Room");
        setSingleChipListener(chip_twin_room,"Twin Room");
        setSingleChipListener(chip_suite,"Suite");
        setSingleChipListener(chip_family_room,"Family Room");
        setSingleChipListener(chip_deluxe_room,"Deluxe Room");
        setSingleChipListener(chip_executive_room,"Executive Room");
        setSingleChipListener(chip_accessible_room,"Accessible Room");

        setChipListener(chip_room_services,"Room Service");
        setChipListener(chip_laundry_service,"Laundry Service");
        setChipListener(chip_shuttle_service,"Shuttle Service");
        setChipListener(chip_housekeeping_service,"Housekeeping");
        setChipListener(chip_spa_service,"Spa Services");
        setChipListener(chip_fitness_service,"Fitness Center");
        setChipListener(chip_business_service,"Business Center");
        setChipListener(chip_event_hosting,"Event Hosting");

    }
    private void setChipListener(Chip chip, final String category) {
        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedRoomService.add(category);
                } else {
                    selectedRoomService.remove(category);
                }
            }
        });
    }
    private void setSingleChipListener(Chip chip, final String category) {
        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedRoomType=category;
                } else {
                    selectedRoomType=null;
                }
            }
        });
    }


}