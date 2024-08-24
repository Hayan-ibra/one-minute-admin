package com.example.omadmin.shops;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.Food;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.shops.bottomsheet.UpdateItemBottomSheet;
import com.example.omadmin.uploadingfood.FoodSingleItem;
import com.example.omadmin.uploadingfood.Restaurants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.List;

public class ShopSingleItem extends AppCompatActivity implements UpdateItemBottomSheet.MyCallback {
    ImageView iv;
    TextView tv_name,tv_quantity,tv_price,tv_genders,tv_catigories;
    FloatingActionButton fab;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("StoreItems");
    String itemId;

    DocumentReference referenceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_single_item);
        iv=findViewById(R.id.ShopSingleItem_image);
        tv_name=findViewById(R.id.ShopSingleItem_text_name);
        tv_price=findViewById(R.id.ShopSingleItem_text_price);
        tv_catigories=findViewById(R.id.ShopSingleItem_text_catigories);
        tv_quantity=findViewById(R.id.ShopSingleItem_text_quantity);
        tv_genders=findViewById(R.id.ShopSingleItem_text_genders);
        fab=findViewById(R.id.ShopSingleItem_fab);
        Intent intent=getIntent();
        StoreItem storeItem = (StoreItem) intent.getSerializableExtra("obj");
        itemId=storeItem.getItemId();
        initiate(storeItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(view);



            }
        });



    }


    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_shopping_item, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return handleMenuItemClick(item);
            }
        });
        popup.show();
    }
    private boolean handleMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.action_update){
            UpdateItemBottomSheet dialog=new UpdateItemBottomSheet(ShopSingleItem.this);
            dialog.show(getSupportFragmentManager(),"aaa");

        } else if (item.getItemId()==R.id.action_delete) {
            AlertDialog dialog = new
                    AlertDialog.Builder(ShopSingleItem.this).setTitle("Confirm Deletion").setMessage("do yo want to delete this food item ?")
                    .setIcon(R.drawable.baseline_delete_24).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteItem(itemId);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            dialog.show();
            return true;

        }else {
            Toast.makeText(this, "select valued item", Toast.LENGTH_SHORT).show();
            return true;
        }



        return false;
    }

    private void deleteItem(String itemId) {
        collectionReference.whereEqualTo("itemId",itemId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the reference to the document
                        DocumentReference docRef = db.collection("StoreItems").document(document.getId());

                        // Delete the document
                        docRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Document deleted successfully
                                        startActivity(new Intent(ShopSingleItem.this, ShoppingMainActivity.class));
                                        finish();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors
                                        Toast.makeText(ShopSingleItem.this, "Failed", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ShopSingleItem.this, ShoppingMainActivity.class));
                                        finish();
                                    }
                                });
                    }
                } else {
                    // Handle errors while querying the collection
                }





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopSingleItem.this, "could not find item", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void initiate(StoreItem storeItem){
        Glide.with(this).load(storeItem.getImageUrl()).into(iv);
        String catigories="";
        List<String> cat=  storeItem.getCatigory();
        for (int i=0 ; i<cat.size();i++){
            catigories=catigories+cat.get(i)+", ";
            if(i==cat.size()-1){
                catigories=catigories+cat.get(i)+" ";

            }else {
                catigories=catigories+cat.get(i)+", ";;

            }
        }
        tv_name.setText(storeItem.getItemName());
        tv_catigories.setText(catigories);
        tv_quantity.setText(String.valueOf("Available items : "+storeItem.getQuantity()));
        tv_price.setText("Price: "+String.valueOf(storeItem.getPrice())+" $");
        String genders="";
        List<String> gen=  storeItem.getGender();
        for (int i=0 ; i<gen.size();i++){
            if(i==gen.size()-1){
                genders=genders+gen.get(i)+" ";

            }else {
                genders=genders+gen.get(i)+" , ";

            }

        }
        tv_genders.setText(genders);



    }

    @Override
    public void onSuccess(Double pricee, Long quantity) {
        collectionReference.whereEqualTo("itemId",itemId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    DocumentSnapshot snapshotItem=value.getDocuments().get(0);
                    referenceItem=snapshotItem.getReference();

                }
            }
        });

        if (pricee!=null && quantity!=null){
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot itemSnapshot = transaction.get(referenceItem);

                Long currentQuantity = itemSnapshot.getLong("quantity");
                Double currentPrice=itemSnapshot.getDouble("price");

                if ( currentQuantity != null && currentPrice!=null) {

                    // Update points
                    transaction.update(referenceItem, "price", pricee);

                    // Update item quantity
                    transaction.update(referenceItem, "quantity", quantity);

                } else {
                    Toast.makeText(ShopSingleItem.this, "Wrong price or quantity", Toast.LENGTH_SHORT).show();
                    throw new FirebaseFirestoreException("Insufficient points or quantity", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }).addOnSuccessListener(aVoid -> {
                // Transaction success
                System.out.println("Updated successfully!");
                Toast.makeText(ShopSingleItem.this, "Quantity and Price updated successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Transaction failure
                System.err.println("Failed to update quantity and Price: " + e.getMessage());
                Toast.makeText(ShopSingleItem.this, "Failed to update quantity and Price: ", Toast.LENGTH_SHORT).show();
            });


        } else if (pricee!=null) {
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot itemSnapshot = transaction.get(referenceItem);
                Double currentPrice=itemSnapshot.getDouble("price");

                if ( currentPrice!=null) {

                    // Update points
                    transaction.update(referenceItem, "price", pricee);

                } else {
                    Toast.makeText(ShopSingleItem.this, "Wrong price ", Toast.LENGTH_SHORT).show();
                    throw new FirebaseFirestoreException("Wrong Price", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }).addOnSuccessListener(aVoid -> {
                // Transaction success
                System.out.println("Updated successfully!");
                Toast.makeText(ShopSingleItem.this, "Price updated successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Transaction failure
                System.err.println("Failed to update Price: " + e.getMessage());
                Toast.makeText(ShopSingleItem.this, "Failed to update Price: ", Toast.LENGTH_SHORT).show();
            });





        } else if (quantity!=null) {
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot itemSnapshot = transaction.get(referenceItem);

                Long currentQuantity = itemSnapshot.getLong("quantity");



                if ( currentQuantity != null ) {

                    // Update item quantity
                    transaction.update(referenceItem, "quantity", quantity);

                } else {
                    Toast.makeText(ShopSingleItem.this, "Failed to retrieve quantity ", Toast.LENGTH_SHORT).show();
                    throw new FirebaseFirestoreException("Failed to retrieve quantity", FirebaseFirestoreException.Code.ABORTED);
                }

                return null;
            }).addOnSuccessListener(aVoid -> {
                // Transaction success
                System.out.println("Updated successfully!");
                Toast.makeText(ShopSingleItem.this, "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Transaction failure
                System.err.println("Failed to update quantity: " + e.getMessage());
                Toast.makeText(ShopSingleItem.this, "Failed to update quantity: ", Toast.LENGTH_SHORT).show();
            });



        }else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }


    }
}