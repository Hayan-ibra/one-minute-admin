package com.example.omadmin.hotels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShopSingleItem;
import com.example.omadmin.shops.bottomsheet.UpdateItemBottomSheet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RoomSingleItem extends AppCompatActivity {

    ImageView roomImage;
    TextView tv_name,tv_price,tv_type,tv_services;
    FloatingActionButton fab;
    HotelRoom room;
    Users user=Users.getInstance();
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("HotelRooms");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    DocumentReference referenceRoom;
    MaterialToolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    Hotels hotel;

    public static String  roomId;

    MaterialButton button;
    HotelRoom hotelRoom=HotelRoom.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_single_item);
        roomImage=findViewById(R.id.RoomSingleItem_image);
        tv_name=findViewById(R.id.RoomSingleItem_text_name);
        tv_price=findViewById(R.id.RoomSingleItem_text_price);
        tv_services=findViewById(R.id.RoomSingleItem_text_services);
        tv_type=findViewById(R.id.RoomSingleItem_text_type);
        toolbar=findViewById(R.id.RoomSingleItem_toolbar);
        button=findViewById(R.id.RoomSingleItem_button);
        collapsingToolbarLayout=findViewById(R.id.RoomSingleItem_collapsing_shopping);
        //appBarLayout=findViewById(R.id.Roos);

        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        room =(HotelRoom) intent.getSerializableExtra("room");
        hotel= (Hotels) intent.getSerializableExtra("hotel");
        init(room);

        hotelRoom.setRoomId(room.getRoomId());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomId=room.getRoomId();
                Intent intent1=new Intent(RoomSingleItem.this,DisplayRoomImages.class);
                intent1.putExtra("room1",room);

                startActivity(intent1);


            }
        });







    }

    private void init(HotelRoom room) {
        //tv_name.setVisibility(View.GONE);
        tv_type.setText(room.getType());
        tv_name.setText("Room : "+room.getRoomNum());
        tv_price.setText(room.getPricePerDay()+"$");
        String services="";
        //toolbar.setTitle("Room : "+room.getRoomNum());
        collapsingToolbarLayout.setTitle(""+room.getRoomNum());
        List<String> serv=  room.getServices();
        for (int i=0 ; i<serv.size();i++){
            //services=services+serv.get(i)+", ";
            if(i==serv.size()-1){
                services=services+serv.get(i)+" ";

            }else {
                services=services+serv.get(i)+", ";;

            }
        }
        tv_services.setText(services);
        Glide.with(RoomSingleItem.this).load(room.getImageUrl()).into(roomImage);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_update_room){

            //updateRoomState();

            showCustomDialog();

        } else if (item.getItemId()==R.id.action_delete_room) {
            AlertDialog dialog = new
                    AlertDialog.Builder(RoomSingleItem.this).setTitle("Confirm Deletion").setMessage("do yo want to delete this food item ?")
                    .setIcon(R.drawable.baseline_delete_24).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteItem(room.getRoomId());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {



                        }
                    }).create();
            dialog.show();
            return true;

        } else if (item.getItemId()==R.id.action_update_state_room) {
            //the whole way changed ...
           // showCustomDialogStateUpdate();
            Intent intent=new Intent(RoomSingleItem.this, ShowReservationState.class);
            intent.putExtra("room",room);
            intent.putExtra("hotel",hotel);
            startActivity(intent);




        } else if (item.getItemId()==R.id.action_show_images_room) {
            Intent intent=new Intent(RoomSingleItem.this,RoomUploadImage.class);
            intent.putExtra("rooom",room);
            intent.putExtra("int",0);
            startActivity(intent);


        } else {
            Toast.makeText(this, "select valued item", Toast.LENGTH_SHORT).show();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }





    private void deleteItem(String roomId) {
        collectionReference.whereEqualTo("roomId",roomId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    for (QueryDocumentSnapshot snapshot : value){
                        DocumentReference docRef = collectionReference.document(snapshot.getId());
                        HotelRoom roomSnap=snapshot.toObject(HotelRoom.class);
                        String imgUrl=roomSnap.getImageUrl();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imageRef = storage.getReferenceFromUrl(imgUrl);

                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RoomSingleItem.this, "Successfully deleted image", Toast.LENGTH_SHORT).show();
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RoomSingleItem.this, "Successfully room deleted ", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RoomSingleItem.this, "Failed to delete room", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RoomSingleItem.this, "Failed ", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
            }
        });




    }


    private void showCustomDialog() {
        // Create an instance of LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.dialog_update_price_room, null);

        // Initialize the views in the dialog
        TextView textView = customView.findViewById(R.id.text_dialog_room_price);
        EditText editText = customView.findViewById(R.id.edittext_dialog_room_price);
        Button button = customView.findViewById(R.id.button_dialog_room_price);

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the custom layout as the dialog's view
        builder.setView(customView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the button click listener
        button.setOnClickListener(v -> {
            // Handle the button click and get the text from EditText

            Double price = Double.valueOf(editText.getText().toString());
            updatePrice(price);
            // Call the callback method
            //onDialogSubmit(inputText);
            // Dismiss the dialog
            dialog.dismiss();
        });
    }

    private void showCustomDialogStateUpdate() {
        // Create an instance of LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.dialog_update_state_room, null);

        // Initialize the views in the dialog

        Button button = customView.findViewById(R.id.dialog_room_state_button_change);
        RadioButton reserved=customView.findViewById(R.id.dialog_room_state_radioButton_reserved);
        RadioButton available=customView.findViewById(R.id.dialog_room_state_radioButton_not_reserved);


        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the custom layout as the dialog's view
        builder.setView(customView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the button click listener
        button.setOnClickListener(v -> {
            // Handle the button click and get the text from EditText

            long state=4;
            if (reserved.isChecked()){
                state=1;

            } else if (available.isChecked()) {
                state=0;
            }
            // Call the callback method
            //onDialogSubmit(inputText);
            // Dismiss the dialog
            if (state==1 || state==0){

                updateReservationState(state);


            }else {
                Toast.makeText(this, "Select State", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    private void updateReservationState(long state) {
        collectionReference.whereEqualTo("roomId",room.getRoomId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    Toast.makeText(RoomSingleItem.this, "inside", Toast.LENGTH_SHORT).show();
                    DocumentSnapshot snapshot=value.getDocuments().get(0);
                    referenceRoom=snapshot.getReference();


                    db.runTransaction((Transaction.Function<Void>) transaction -> {



                        // Update item quantity
                        transaction.update(referenceRoom, "state", state);



                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        // Transaction success
                        System.out.println("Updated successfully!");
                        Toast.makeText(RoomSingleItem.this, "Reservation updated successfully!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Transaction failure
                        System.err.println("Failed to update quantity and Price: " + e.getMessage());
                        Toast.makeText(RoomSingleItem.this, "Failed to update Reservation State : ", Toast.LENGTH_SHORT).show();
                    });




                }else {
                    Toast.makeText(RoomSingleItem.this, "Empty value", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void updatePrice(Double price) {

        collectionReference.whereEqualTo("roomId",room.getRoomId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    Toast.makeText(RoomSingleItem.this, "inside", Toast.LENGTH_SHORT).show();
                    DocumentSnapshot snapshot=value.getDocuments().get(0);
                    referenceRoom=snapshot.getReference();


                    if(price!=null){
                        db.runTransaction((Transaction.Function<Void>) transaction -> {
                            DocumentSnapshot roomSnapshot = transaction.get(referenceRoom);





                            // Update item quantity
                            transaction.update(referenceRoom, "pricePerDay", price);



                            return null;
                        }).addOnSuccessListener(aVoid -> {
                            // Transaction success
                            System.out.println("Updated successfully!");
                            Toast.makeText(RoomSingleItem.this, "Price updated successfully!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            // Transaction failure
                            System.err.println("Failed to update quantity and Price: " + e.getMessage());
                            Toast.makeText(RoomSingleItem.this, "Failed to update Price: ", Toast.LENGTH_SHORT).show();
                        });



                    }else {
                        Toast.makeText(RoomSingleItem.this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(RoomSingleItem.this, "Empty value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}