package com.example.omadmin.uploadingfood.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.omadmin.models.History;

import java.util.ArrayList;

public class HistoryViewModel extends AndroidViewModel {
    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<ArrayList<History>> histories =new MutableLiveData<>();


    public  void  setHistories(ArrayList<History> newHistories){
        histories.setValue(newHistories);
    }

    public LiveData<ArrayList<History>> getHistories(){
        return histories;
    }



}
