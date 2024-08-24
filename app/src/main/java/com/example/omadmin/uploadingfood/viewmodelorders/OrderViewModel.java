package com.example.omadmin.uploadingfood.viewmodelorders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.omadmin.models.Orders;
import com.example.omadmin.uploadingfood.repository.RepositoryOrderActivity;

import java.util.ArrayList;

public class OrderViewModel extends AndroidViewModel {
    public OrderViewModel(@NonNull Application application) {

        super(application);
        repository = new RepositoryOrderActivity();
        itemsLiveData = repository.getItems();
    }
    private RepositoryOrderActivity repository;
    private LiveData<ArrayList<Orders>> itemsLiveData;



    public LiveData<ArrayList<Orders>> getOrders() {
        return itemsLiveData;
    }

}
