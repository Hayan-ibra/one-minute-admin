package com.example.omadmin.shops.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.omadmin.models.Store;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.shops.repository.RepositoryItems;
import com.example.omadmin.shops.repository.RepositoryStores;

import java.util.ArrayList;

public class ItemViewModel extends AndroidViewModel {

    private RepositoryItems repository;
    private LiveData<ArrayList<StoreItem>> storeLiveData;


    public ItemViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositoryItems();
        storeLiveData = repository.getItem();
    }



    public LiveData<ArrayList<StoreItem>> getItems() {
        return storeLiveData;
    }
}
