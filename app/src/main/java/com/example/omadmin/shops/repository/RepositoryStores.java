package com.example.omadmin.shops.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.models.Orders;
import com.example.omadmin.models.Store;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShoppingMainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RepositoryStores {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceStores=db.collection("StoreOwnersN");
    private MutableLiveData<ArrayList<Store>> storesLiveData = new MutableLiveData<>();

    Users users=Users.getInstance();
    String shopId=users.getUid();

    public LiveData<ArrayList<Store>> getStore() {
        if (storesLiveData.getValue() == null) {
            fetchItemsFromFirestore();
        }
        return storesLiveData;
    }

    private void fetchItemsFromFirestore() {
        collectionReferenceStores.whereEqualTo("storeId",shopId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    ArrayList<Store> storeList = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : value) {
                        ShoppingMainActivity.menu_State=1;
                        Store store = snapshot.toObject(Store.class);
                        storeList.add(store);
                        Store s1=Store.getInstance();
                        s1.setStoreName(snapshot.getString("storeName"));
                        s1.setPhoneNum(snapshot.getString("phoneNum"));
                        s1.setWorkTimeTo(snapshot.getString("workTimeTo"));
                        s1.setWorkTimeFrom(snapshot.getString("workTimeFrom"));
                        s1.setLocation(snapshot.getString("location"));
                        s1.setStoreId(snapshot.getString("storeId"));
                        List<String> types = (List<String>) snapshot.get("storeType");
                        s1.setStoreType(types);
                        s1.setDescription(snapshot.getString("description"));
                        s1.setImageUrl(snapshot.getString("imageUrl"));
                    }

                    storesLiveData.setValue(storeList);
                } else {
                    // Handle the error

                }



            }
        });

    }
}
