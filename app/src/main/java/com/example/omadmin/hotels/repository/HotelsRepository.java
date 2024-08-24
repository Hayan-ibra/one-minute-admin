package com.example.omadmin.hotels.repository;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.hotels.HotelsMainActivity;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Users;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HotelsRepository {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceHotel=db.collection("HotelOwnersN");
    private MutableLiveData<Hotels> hotels =new MutableLiveData<>();
    Users users=Users.getInstance();

    public MutableLiveData<Hotels> getHotels() {
        if (hotels.getValue()==null){
            getHotelsData();
        }
        return hotels;
    }

    private void getHotelsData() {

        collectionReferenceHotel.whereEqualTo("hotelId",users.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (QueryDocumentSnapshot snapshot : value){
                        Hotels hotels1=snapshot.toObject(Hotels.class);
                        hotels.setValue(hotels1);
                    }
                }else {

                }
            }
        });




    }
}
