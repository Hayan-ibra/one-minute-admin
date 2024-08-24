package com.example.omadmin.hotels.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.models.Reservation;


import java.util.ArrayList;

public class ReservationsViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<Reservation>> reservations;
    public ReservationsViewModel(@NonNull Application application) {
        super(application);
        reservations=new MutableLiveData<>(new ArrayList<>());
    }



    public LiveData<ArrayList<Reservation>> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<Reservation> reservationsNew) {
        reservations.setValue(reservationsNew);
    }



}
