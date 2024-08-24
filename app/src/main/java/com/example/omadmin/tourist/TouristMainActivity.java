package com.example.omadmin.tourist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.hotels.HotelsMainActivity;
import com.example.omadmin.models.TouristDestinations;
import com.example.omadmin.models.Users;
import com.example.omadmin.tourist.adapter.DestinationsRecyclerAdapter;
import com.example.omadmin.tourist.viewmodel.DestinationsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TouristMainActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    DestinationsViewModel viewModel;
    android.widget.SearchView searchView;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    ImageView searchBy;

    DestinationsRecyclerAdapter adapter;

    int search_state=0;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Tourist");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_main);
        toolbar=findViewById(R.id.TouristMainActivity_toolbar);
        recyclerView=findViewById(R.id.TouristMainActivity_recycler);
        searchView=findViewById(R.id.tourist_search);
        searchBy=findViewById(R.id.tourist_search_by_icon);
        adapter=new DestinationsRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        getData();
        searchViewDetails(searchView);
        searchByIconChanger(searchBy,searchView);

        setSupportActionBar(toolbar);
        viewModel=new ViewModelProvider(this).get(DestinationsViewModel.class);


        viewModel.getDestinations().observe(this, new Observer<ArrayList<TouristDestinations>>() {
            @Override
            public void onChanged(ArrayList<TouristDestinations> touristDestinations) {
                adapter.setDestinations(touristDestinations);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.tourist_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_tourist_add_site){
           startActivity(new Intent(TouristMainActivity.this,AddTouristSite.class));
        } else if (item.getItemId()==R.id.menu_tourist_sign_out) {

            firebaseAuth.signOut();
            Users users=Users.getInstance();
            users.setEmail(null);
            users.setAccountType(null);
            users.setUid(null);
            users.setUrl(null);
            //users.setPoints(null);
            users.setGender(null);



            startActivity(new Intent(TouristMainActivity.this, MainActivity.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void searchByIconChanger(ImageView searchBy,android.widget.SearchView searchView) {
        searchBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (search_state<=2){
                    search_state=search_state+1;
                }else {
                    search_state=1;
                }
                switch (search_state){
                    case 0:

                        break;
                    case 1:
                        searchBy.setImageResource(R.drawable.baseline_location_on_24);
                        searchView.setQueryHint( "Search by city");
                        break;
                    case 2:
                        searchBy.setImageResource(R.drawable.baseline_translate_24);
                        searchView.setQueryHint("Search by name");
                        break;
                    default:
                }
            }
        });
    }

    private void getData() {
        ArrayList<TouristDestinations> destinations=new ArrayList<>();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                        TouristDestinations destinationsSnap=snapshot.toObject(TouristDestinations.class);
                        destinations.add(destinationsSnap);

                    }
                    viewModel.setDestinations(destinations);



                }else {
                    Toast.makeText(TouristMainActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void searchViewDetails(android.widget.SearchView searchView){

        searchView.setIconified(true);
        searchView.setSubmitButtonEnabled(true);//search with button
        searchView.setFilterTouchesWhenObscured(true);

        //searchView.

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                SearchHotels(s);

                return false;
            }



            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    private void SearchHotels(String s) {

        ArrayList<TouristDestinations> destinations=new ArrayList<>();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){

                    for(QueryDocumentSnapshot snapshot :queryDocumentSnapshots ){
                        TouristDestinations destinationsSnap =snapshot.toObject(TouristDestinations.class);

                        if(search_state==1){
                            int result =calculate(destinationsSnap.getCity(),s);
                            if (result <= 3) {
                                // Document is similar enough, display it
                                // You can add it to a list of results or display it directly
                                destinations.add(destinationsSnap);

                            }


                        } else if (search_state==2) {
                            int result =calculate(destinationsSnap.getName(),s);
                            if (result <= 3) {
                                // Document is similar enough, display it
                                // You can add it to a list of results or display it directly
                               destinations.add(destinationsSnap);

                            }

                        }else {
                            Toast.makeText(TouristMainActivity.this, "Please choose filter", Toast.LENGTH_SHORT).show();
                        }





                    }
                    viewModel.setDestinations(destinations);

                    //recycler



                }
                else {
                    Toast.makeText(TouristMainActivity.this, "Empty", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    public static int calculate(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;

        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;

        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }
}