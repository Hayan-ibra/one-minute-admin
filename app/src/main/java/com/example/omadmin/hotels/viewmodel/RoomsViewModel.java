package com.example.omadmin.hotels.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.omadmin.hotels.repository.RoomRepository;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.shops.repository.RepositoryItems;

import java.util.ArrayList;

public class RoomsViewModel extends AndroidViewModel {


    RoomRepository repository;

    private LiveData<ArrayList<HotelRoom>> roomLiveData;
    public RoomsViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository();
        roomLiveData= repository.getRoom();
    }



    public LiveData<ArrayList<HotelRoom>> getRoom() {
        return roomLiveData;
    }
}
