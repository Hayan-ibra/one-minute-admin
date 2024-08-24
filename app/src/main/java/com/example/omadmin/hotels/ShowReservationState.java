package com.example.omadmin.hotels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.hotels.recycleradapter.ReservationsRecyclerAdapter;
import com.example.omadmin.hotels.viewmodel.ReservationsViewModel;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.Hotels;
import com.example.omadmin.models.Reservation;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShowReservationState extends AppCompatActivity {
    RecyclerView recyclerView;
    ReservationsViewModel viewModel;
    Users users=Users.getInstance();
    HotelRoom room;
    Hotels hotel=Hotels.getInstance();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    private CollectionReference collectionReferenceReservations=db.collection("ReservationsN");
    private CollectionReference collectionReferenceUsers =db.collection("Users");
    private CollectionReference collectionReferenceHistory =db.collection("History");

    DocumentReference reservationReference;
    DocumentReference userReference;
    DocumentReference hotelReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reservation_state);
        recyclerView=findViewById(R.id.reservations_recycler);
        viewModel=new ViewModelProvider(this).get(ReservationsViewModel.class);
        Intent intent=getIntent();
        room= (HotelRoom) intent.getSerializableExtra("room");

        getReservationData(room);
        ReservationsRecyclerAdapter adapter=new ReservationsRecyclerAdapter(this, new ReservationsRecyclerAdapter.ClickListener() {
            @Override
            public void delete(Reservation reservation) {

                AlertDialog dialog = new
                        AlertDialog.Builder(ShowReservationState.this).setTitle("Confirm Deletion").setMessage("do yo want to delete this food item ?")
                        .setIcon(R.drawable.baseline_delete_24).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeReservationState(reservation.getDocId(),reservation.getUserId(),reservation);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {



                            }
                        }).create();
                dialog.show();

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        viewModel.getReservations().observe(this, new Observer<ArrayList<Reservation>>() {
            @Override
            public void onChanged(ArrayList<Reservation> reservations) {
                adapter.setReservations(reservations);
            }
        });




    }

    private void changeReservationState(String docId,String userId,Reservation reservation) {
        collectionReferenceReservations.whereEqualTo("docId",docId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    reservationReference=value.getDocuments().get(0).getReference();
                }
            }
        });

        collectionReferenceUsers.whereEqualTo("uid",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    userReference=value.getDocuments().get(0).getReference();
                }
            }
        });
        collectionReferenceUsers.whereEqualTo("uid",users.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    hotelReference=value.getDocuments().get(0).getReference();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userReference!=null && reservationReference!=null && hotelReference!=null){

                    String documentHistoryId= UUID.randomUUID().toString();
                    DocumentReference referenceHistory = collectionReferenceHistory.document(documentHistoryId);
                    Map<String, Object> history = new HashMap<>();
                    history.put("id", reservation.getUserId());
                    history.put("costumerName", users.getUsername());
                    history.put("parentName",hotel.getHotelName());
                    history.put("quantity",String.valueOf(00));
                    history.put("name","Room : "+room.getRoomNum());
                    history.put("type","reservation cancelled");
                    history.put("date",Timestamp.now());
                    history.put("value",(reservation.getEndPrice())*0.8);
                    history.put("state",false);

                    String documentHistoryId2=UUID.randomUUID().toString();
                    DocumentReference referenceHistoryHotel = collectionReferenceHistory.document(documentHistoryId2);
                    Map<String, Object> historyHotel = new HashMap<>();
                    historyHotel.put("id", hotel.getHotelId());
                    historyHotel.put("costumerName",hotel.getHotelName() );
                    historyHotel.put("parentName",users.getUsername());
                    historyHotel.put("quantity",String.valueOf(00));
                    historyHotel.put("name","Room : "+room.getRoomNum());
                    historyHotel.put("type","reservation cancelled");
                    historyHotel.put("date",Timestamp.now());
                    historyHotel.put("value",(reservation.getEndPrice())*0.8);
                    historyHotel.put("state",true);

                    db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot userSnapshot = transaction.get(userReference);
                        DocumentSnapshot reservationSnapshot = transaction.get(reservationReference);
                        DocumentSnapshot hotelSnapshot=transaction.get(hotelReference);

                        Double userPointss = userSnapshot.getDouble("points");
                        Double hotelPoints = hotelSnapshot.getDouble("points");


                        if (hotelPoints >= reservation.getEndPrice()) {
                            // Subtract points from user
                            transaction.update(userReference, "points", userPointss + (reservation.getEndPrice())*0.8);

                            // Add points to restaurant
                            transaction.update(hotelReference, "points", hotelPoints -(reservation.getEndPrice())*0.8);
                            transaction.set(referenceHistory,history);
                            transaction.set(referenceHistoryHotel,historyHotel);

                            reservationReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ShowReservationState.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {

                            Toast.makeText(ShowReservationState.this, "not enough points ", Toast.LENGTH_SHORT).show();
                            throw new IllegalStateException("exception");

                        }

                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        Toast.makeText(ShowReservationState.this, "Operation Done Successfully", Toast.LENGTH_SHORT).show();


                    }).addOnFailureListener(e -> {
                        Toast.makeText(ShowReservationState.this, "Operation Failed", Toast.LENGTH_SHORT).show();

                    });



                }else {
                    Toast.makeText(ShowReservationState.this, "There is null somewhere", Toast.LENGTH_SHORT).show();
                }

            }
        },1000);






    }

    void getReservationData(HotelRoom room){
        ArrayList<Reservation> res=new ArrayList<>();
        collectionReferenceReservations.whereEqualTo("roomId",room.getRoomId()).get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot:task.getResult()){
                        Reservation reservation=snapshot.toObject(Reservation.class);

                        int x=(reservation.getDate().size())-1;
                        if (Timestamp.now().compareTo(reservation.getDate().get(x)) < 0){
                            res.add(reservation);
                        }



                    }

                    viewModel.setReservations(res);



                }
            }
        });


    }
}