package com.example.omadmin.hotels.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.models.Users;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RoomRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceRooms=db.collection("HotelRooms");
    private MutableLiveData<ArrayList<HotelRoom>> roomLiveData = new MutableLiveData<>();

    Users users=Users.getInstance();
    String hotelId=users.getUid();

    public LiveData<ArrayList<HotelRoom>> getRoom() {
        if (roomLiveData.getValue() == null) {
            fetchItemsFromFirestore();
        }
        return roomLiveData;
    }

    private void fetchItemsFromFirestore() {
        collectionReferenceRooms.whereEqualTo("parentId",hotelId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    ArrayList<HotelRoom> rooms = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : value) {

                        HotelRoom room=snapshot.toObject(HotelRoom.class);
                        rooms.add(room);
                    }

                    roomLiveData.setValue(rooms);
                } else {
                    // Handle the error

                }



            }
        });

    }

}
