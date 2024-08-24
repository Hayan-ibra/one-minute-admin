package com.example.omadmin.shops.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.models.Store;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShoppingMainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RepositoryItems {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceStores=db.collection("StoreItems");
    private MutableLiveData<ArrayList<StoreItem>> itemsLiveData = new MutableLiveData<>();

    Users users=Users.getInstance();
    String shopId=users.getUid();

    public LiveData<ArrayList<StoreItem>> getItem() {
        if (itemsLiveData.getValue() == null) {
            fetchItemsFromFirestore();
        }
        return itemsLiveData;
    }

    private void fetchItemsFromFirestore() {
        collectionReferenceStores.whereEqualTo("parentId",shopId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    ArrayList<StoreItem> itemList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : value) {

                        StoreItem item=snapshot.toObject(StoreItem.class);
                        itemList.add(item);
                    }

                    itemsLiveData.setValue(itemList);
                } else {
                    // Handle the error

                }



            }
        });

    }
}
