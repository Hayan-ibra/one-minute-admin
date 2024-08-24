package com.example.omadmin.shops.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.omadmin.models.Orders;
import com.example.omadmin.models.Store;
import com.example.omadmin.shops.repository.RepositoryStores;
import com.example.omadmin.uploadingfood.repository.RepositoryOrderActivity;

import java.util.ArrayList;

public class StoreViewModel extends AndroidViewModel {

    private RepositoryStores repository;
    private LiveData<ArrayList<Store>> storeLiveData;


    public StoreViewModel(@NonNull Application application) {
        super(application);
        repository = new RepositoryStores();
        storeLiveData = repository.getStore();
    }



    public LiveData<ArrayList<Store>> getStores() {
        return storeLiveData;
    }
}
