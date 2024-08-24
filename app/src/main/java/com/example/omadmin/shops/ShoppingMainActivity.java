package com.example.omadmin.shops;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.models.Store;
import com.example.omadmin.models.StoreItem;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ViewModel.ItemViewModel;
import com.example.omadmin.shops.ViewModel.StoreViewModel;
import com.example.omadmin.shops.shoprecyclers.GridSpacingItemDecoration;
import com.example.omadmin.shops.shoprecyclers.ItemsRecyclerAdapter;
import com.example.omadmin.uploadingfood.history.PointsHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ShoppingMainActivity extends AppCompatActivity {


    MaterialToolbar toolbar;

    RecyclerView rec_items;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    ImageView image_profile;
    TextView tv_message;

    StoreViewModel viewModel;
    ItemViewModel itemViewModel;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("StoreOwnerN");
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();



    Store store=Store.getInstance();
    public static int menu_State=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_main);
        toolbar=findViewById(R.id.ShoppingMainActivity_toolbar);
        rec_items=findViewById(R.id.ShoppingMainActivity_recycler_shop_items);
        collapsingToolbarLayout=findViewById(R.id.ShoppingMainActivity_collapsing_shopping);
        appBarLayout=findViewById(R.id.ShoppingMainActivity_appbar);
        image_profile=findViewById(R.id.ShoppingMainActivity_profile_image);
        tv_message=findViewById(R.id.ShoppingMainActivity_message_shopping);
        tv_message.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        toolbar.setTitle(store.getStoreName());
        viewModel = new ViewModelProvider(this).get(StoreViewModel.class);
        viewModel.getStores().observe(this, new Observer<ArrayList<Store>>() {
            @Override
            public void onChanged(ArrayList<Store> stores) {
                if (stores.isEmpty()){
                    tv_message.setVisibility(View.VISIBLE);
                    toolbar.setTitle("");
                }
                Toast.makeText(ShoppingMainActivity.this, ""+store.getStoreName(), Toast.LENGTH_SHORT).show();

                toolbar.setTitle(store.getStoreName());
                Glide.with(getApplicationContext()).load(store.getImageUrl()).fitCenter().into(image_profile);
                collapsingToolbarLayout.setTitle(store.getStoreName());
                collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

            }
        });
        ItemsRecyclerAdapter adapter=new ItemsRecyclerAdapter(this);
        rec_items.setAdapter(adapter);
        rec_items.setLayoutManager(new GridLayoutManager(this,2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rec_items.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        itemViewModel=new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getItems().observe(this, new Observer<ArrayList<StoreItem>>() {
            @Override
            public void onChanged(ArrayList<StoreItem> storeItems) {
                adapter.setItems(storeItems);
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping,menu);
        if (menu_State==1){
            MenuItem item1=menu.findItem(R.id.menu_shopping_profile_creation);
            item1.setVisible(false);




        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.menu_shopping_profile_creation){
            startActivity(new Intent(ShoppingMainActivity.this, ShopProfileCreation.class));

        } else if (item.getItemId()==R.id.menu_shopping_profile_update) {
            startActivity(new Intent(ShoppingMainActivity.this, ShopProfileUpdate.class));

        }
        else if (item.getItemId()==R.id.menu_shopping_profile_add_item) {
            startActivity(new Intent(ShoppingMainActivity.this, AddStoreItem.class));

        }
        else if (item.getItemId()==R.id.menu_shopping_profile_history) {
            startActivity(new Intent(ShoppingMainActivity.this, PointsHistory.class));

        }
        else if (item.getItemId()==R.id.menu_shopping_profile_signout) {
            firebaseAuth.signOut();
            Store store=Store.getInstance();
            store.setDescription(null);
            store.setStoreName(null);
            store.setStoreId(null);
            store.setStoreType(null);
            store.setLocation(null);
            store.setImageUrl(null);
            store.setWorkTimeFrom(null);
            store.setWorkTimeTo(null);
            store.setPhoneNum(null);
            startActivity(new Intent(ShoppingMainActivity.this, MainActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Users u1=Users.getInstance();
        collectionReference.whereEqualTo("storeId",u1.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot snapshot:value){
                    menu_State=1;
                   /* Store store =snapshot.toObject(Store.class);
                    Store s1=Store.getInstance();
                    s1.setStoreName(snapshot.getString("storeName"));
                    s1.setPhoneNum(snapshot.getString("phoneNum"));
                    s1.setWorkTimeTo(snapshot.getString("workTimeTo"));
                    s1.setWorkTimeFrom(snapshot.getString("workTimeFrom"));
                    s1.setLocation(snapshot.getString("String location"));
                    s1.setStoreId(snapshot.getString("storeId"));
                    List<String> types = (List<String>) snapshot.get("storeType");
                    s1.setStoreType(types);
                    s1.setDescription(snapshot.getString("description"));
                    s1.setImageUrl(snapshot.getString("imageUrl"));*/

                }
            }
        });
    }
}