package com.example.omadmin.hotels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.hotels.recycleradapter.ImagesRecyclerAdapter;
import com.example.omadmin.hotels.repository.HotelsRepository;
import com.example.omadmin.hotels.viewmodel.ImagesViewModel;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.RoomImages;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.shoprecyclers.GridSpacingItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class DisplayRoomImages extends AppCompatActivity {



    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    HotelRoom room;


    ImagesRecyclerAdapter adapter;
    ImagesViewModel viewModel;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceRooms=db.collection("RoomImages");

    ArrayList<RoomImages> imagesCopy;

    private int currentItemPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_room_images);
        recyclerView=findViewById(R.id.DisplayRoomImages_recycler);
        floatingActionButton=findViewById(R.id.DisplayRoomImages_floatingActionButton);
        Intent intent=getIntent();
        room= (HotelRoom) intent.getSerializableExtra("room1");




        adapter=new ImagesRecyclerAdapter(DisplayRoomImages.this);

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

// Get the position of the last visible item
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();


        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        getData();
        viewModel=new ViewModelProvider(this).get(ImagesViewModel.class);



        viewModel.getImages().observe(this, new Observer<ArrayList<RoomImages>>() {
            @Override
            public void onChanged(ArrayList<RoomImages> roomImages) {
                adapter.setImages(roomImages);
                imagesCopy=new ArrayList<>(roomImages);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    currentItemPosition = layoutManager.findFirstVisibleItemPosition();
                    Toast.makeText(DisplayRoomImages.this,""+ currentItemPosition, Toast.LENGTH_SHORT).show();

                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteImage(currentItemPosition);


            }
        });




    }

    private void deleteImage(int currentItemPosition) {

        AlertDialog dialog = new
                AlertDialog.Builder(DisplayRoomImages.this).setTitle("Confirm Deletion").setMessage("do yo want to delete this food item ?")
                .setIcon(R.drawable.baseline_delete_24).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!imagesCopy.isEmpty())
                        {
                            deleteItem(imagesCopy.get(currentItemPosition).getImageId());
                        }


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                    }
                }).create();
        dialog.show();
    }

    public int getCurrentItemPosition() {
        return currentItemPosition;
    }

    private void getData() {


        collectionReferenceRooms.whereEqualTo("roomId", room.getRoomId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {

                    ArrayList<RoomImages> roomsimg = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : value) {

                        RoomImages roomI = snapshot.toObject(RoomImages.class);
                        roomsimg.add(roomI);
                    }


                    viewModel.setImages(roomsimg);
                } else {
                    // Handle the error
                    Toast.makeText(DisplayRoomImages.this, "ntohing found", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void deleteItem(String ImageId) {
        collectionReferenceRooms.whereEqualTo("imageId",ImageId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    Toast.makeText(DisplayRoomImages.this, "Found one", Toast.LENGTH_SHORT).show();
                    for (QueryDocumentSnapshot snapshot : value){
                        DocumentReference docRef = collectionReferenceRooms.document(snapshot.getId());
                        RoomImages roomSnap=snapshot.toObject(RoomImages.class);
                        String imgUrl=roomSnap.getImageURL();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imageRef = storage.getReferenceFromUrl(imgUrl);

                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DisplayRoomImages.this, "Successfully deleted image", Toast.LENGTH_SHORT).show();
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DisplayRoomImages.this, "Successfully room deleted ", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DisplayRoomImages.this, "Failed to delete room", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DisplayRoomImages.this, "Failed ", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
            }
        });




    }





}