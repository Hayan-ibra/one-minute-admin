package com.example.omadmin.uploadingfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.adapters.OrderRecyclerAdapter;
import com.example.omadmin.models.History;
import com.example.omadmin.models.Orders;
import com.example.omadmin.models.RestaurantProfile;
import com.example.omadmin.models.Users;
import com.example.omadmin.uploadingfood.viewmodelorders.OrderViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private FirebaseFirestore db =FirebaseFirestore.getInstance();

    private CollectionReference collectionReferenceOrders=db.collection("OrdersN");
    private CollectionReference collectionReferenceUsers =db.collection("Users");
    private CollectionReference collectionReferenceHistory =db.collection("History");

    OrderViewModel viewModel;

    RecyclerView recyclerView;
    OrderRecyclerAdapter adapter;

    Users user=Users.getInstance();

    ArrayList <Orders> safeOrders=new ArrayList<>();

    RestaurantProfile profile=RestaurantProfile.getInstance();

    DocumentSnapshot resDocReference;
    DocumentSnapshot userDocReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        recyclerView=findViewById(R.id.recycler_orders);
        viewModel=new ViewModelProvider(this).get(OrderViewModel.class);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter=new OrderRecyclerAdapter(user.getUid(), OrderActivity.this, new OrderRecyclerAdapter.Listenerr() {
            @Override
            public void onclicked(long state, String userId, String time) {




            }
        });
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        viewModel.getOrders().observe(OrderActivity.this, new Observer<ArrayList<Orders>>() {
            @Override
            public void onChanged(ArrayList<Orders> orders) {
                safeOrders.clear();
                for (Orders orders1 : orders){
                    safeOrders.add(orders1);

                }


                adapter.setOrders(safeOrders);



            }
        });



    }


    public void setTheStateToAccepted(String userId, String resId, String time_for_compare, long state) {

        //Toast.makeText(this, ""+state+"  "+time_for_compare+"  " +userId , Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Enter", Toast.LENGTH_SHORT).show();
        collectionReferenceOrders.whereEqualTo("resId",resId).whereEqualTo("userId",userId)
                .whereEqualTo("timeForDelete",time_for_compare).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){

                    DocumentSnapshot documentSnapshot = value.getDocuments().get(0);

                    String documentID= documentSnapshot.getId();

                    DocumentReference docRef = db.collection("OrdersN").document(documentID);

                    Map<String,Object> map=new HashMap<>();
                    map.put("state",state);
                    map.put("resReply",profile.getName());
                    docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(OrderActivity.this, "Set", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OrderActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        });
    }
    public void setTheStateToDenied(String userId, String resId, String time_for_compare, long state) {



        //Toast.makeText(this, ""+state+"  "+time_for_compare+"  " +userId , Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Enter", Toast.LENGTH_SHORT).show();
        collectionReferenceOrders.whereEqualTo("resId",resId).whereEqualTo("userId",userId)
                .whereEqualTo("timeForDelete",time_for_compare).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!value.isEmpty()){
                            Toast.makeText(OrderActivity.this, "Inside", Toast.LENGTH_SHORT).show();

                            DocumentSnapshot documentSnapshot = value.getDocuments().get(0);

                            String documentID= documentSnapshot.getId();

                            DocumentReference docRef = db.collection("OrdersN").document(documentID);


                            docRef.update("state", state).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(OrderActivity.this, "Set", Toast.LENGTH_SHORT).show();




                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OrderActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                });


    }


    public void pointsTransaction( Double pricee,String userIdd,String resId,String foodName,String userName,String quantity) {

        final Double[] respoints = new Double[1];
        final Double[] userPoints = new Double[1];


        collectionReferenceUsers.whereEqualTo("uid",resId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                resDocReference = value.getDocuments().get(0);
                if(value!=null){
                    for(QueryDocumentSnapshot snapshot : value){
                        Users users1=snapshot.toObject(Users.class);
                        respoints[0] =users1.getPoints();

                    }
                }
            }
        });
        collectionReferenceUsers.whereEqualTo("uid",userIdd).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userDocReference = value.getDocuments().get(0);
                if(value!=null){
                    for(QueryDocumentSnapshot snapshot : value){
                        Users users2=snapshot.toObject(Users.class);
                        userPoints[0] = users2.getPoints();


                    }
                }
            }
        });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                String restaurantId = resDocReference.getId();
                String userId=  userDocReference.getId();
                final DocumentReference userRef = db.collection("Users").document(userId);
                final DocumentReference restaurantRef = db.collection("Users").document(restaurantId);

                db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot userSnapshot = transaction.get(userRef);
                    DocumentSnapshot restaurantSnapshot = transaction.get(restaurantRef);

                    Double userPointss = userSnapshot.getDouble("points");
                    Double restaurantPoints = restaurantSnapshot.getDouble("points");

                    if (restaurantPoints >= pricee) {
                        // Subtract points from user
                        transaction.update(restaurantRef, "points", restaurantPoints - pricee);


                        // Add points to restaurant
                        transaction.update(userRef, "points", userPointss + pricee);



                    } else {
                        Toast.makeText(OrderActivity.this, "not enough", Toast.LENGTH_SHORT).show();
                        throw new FirebaseFirestoreException("Not enough points", FirebaseFirestoreException.Code.ABORTED);
                    }

                    return null;
                }).addOnSuccessListener(aVoid -> {

                    // Transaction success
                    Log.d("FirestoreTransaction", "Transaction success!");
                    Toast.makeText(OrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    addToHistory(userIdd,foodName,userName,pricee,quantity);
                }).addOnFailureListener(e -> {
                    // Transaction failur
                    Toast.makeText(OrderActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    Log.w("FirestoreTransaction", "Transaction failure.", e);
                });

            }
        },2000);
    }
    private void addToHistory(String userID, String type, String name, Double pricee,String quantity) {

        History history=new History();
        history.setType("refund for "+type);
        history.setId(userID);
        history.setName(name);
        history.setValue(pricee);
        Timestamp timestamp =Timestamp.now();
        history.setDate(timestamp);
        history.setState(false);
        history.setQuantity(quantity);
        history.setParentName(profile.getName());
        //new Timestamp(new Date())
        collectionReferenceHistory.add(history).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(OrderActivity.this, "H success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderActivity.this, "H failure", Toast.LENGTH_SHORT).show();

            }
        });



        History history2=new History();
        history2.setType(type);
        history2.setId(user.getUid());
        history2.setName(name);
        history2.setValue(pricee);
        Timestamp timestamp2 =Timestamp.now();
        history2.setDate(timestamp2);
        history2.setState(true);
        history2.setQuantity(String.valueOf(quantity));
        history2.setParentName(user.getUsername());
        //new Timestamp(new Date())
        collectionReferenceHistory.add(history2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(OrderActivity.this, "H success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderActivity.this, "H failure", Toast.LENGTH_SHORT).show();

            }
        });









    }



}