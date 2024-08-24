package com.example.omadmin.uploadingfood.history;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.History;
import com.example.omadmin.models.Users;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PointsHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    HistoriesRecyclerAdapter adapter;

    ArrayList<History> histories;


    HistoryViewModel viewModel;

    PointsViewModel viewModelPoints;

    TextView tv_points;


    private FirebaseFirestore db =FirebaseFirestore.getInstance();

    private CollectionReference collectionReferenceHistory =db.collection("History");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_history);
        recyclerView=findViewById(R.id.points_history_recycler);
        tv_points=findViewById(R.id.points_history_text_points);

        Users user=Users.getInstance();
        user.getUid();

        viewModelPoints=new ViewModelProvider(PointsHistory.this).get(PointsViewModel.class);
        viewModelPoints.setPoints(user.getPoints());
        viewModelPoints.getPoints().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                tv_points.setText("Current points : "+String.format("%.2f", aDouble));
            }
        });




        viewModel = new ViewModelProvider(PointsHistory.this).get(HistoryViewModel.class);
        getHistory( user.getUid());


        adapter=new HistoriesRecyclerAdapter(histories,this);


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        viewModel.getHistories().observe(this, new Observer<ArrayList<History>>() {
            @Override
            public void onChanged(ArrayList<History> histories) {
                adapter.notifyDataSetChanged();
            }
        });


    }

    void getHistory(String userId){

        histories=new ArrayList<>();
        collectionReferenceHistory.whereEqualTo("id",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    for (QueryDocumentSnapshot snapshot: value){
                        History history =snapshot.toObject(History.class);
                        histories.add(history);



                    }

                    Collections.sort(histories, new Comparator<History>() {
                        @Override
                        public int compare(History o1, History o2) {
                            return o2.getDate().compareTo(o1.getDate());
                        }
                    });
                    viewModel.setHistories(histories);

                }else {
                    Toast.makeText(PointsHistory.this, "History is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }






}