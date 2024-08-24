package com.example.omadmin.hotels.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.hotels.repository.HotelsRepository;
import com.example.omadmin.models.Hotels;

import java.util.ArrayList;

public class HotelsViewModel extends AndroidViewModel {
    private MutableLiveData<Hotels> hotels;
    private HotelsRepository repository;
    public HotelsViewModel(@NonNull Application application) {
        super(application);
        repository=new HotelsRepository();
        hotels=repository.getHotels();
    }


    public LiveData<Hotels> getHotels() {
        return hotels;
    }


}
