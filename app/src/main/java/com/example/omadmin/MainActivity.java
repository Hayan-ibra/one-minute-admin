package com.example.omadmin;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.omadmin.hotels.HotelsMainActivity;
import com.example.omadmin.models.Users;
import com.example.omadmin.shops.ShoppingMainActivity;
import com.example.omadmin.uploadingfood.Restaurants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    MaterialButton sign_in_btn,sign_up_btn,forgot_btn;
    EditText name_edt,password_edt;
    ProgressBar progressBar;

    //firebase
    private FirebaseAuth firebaseAuth;

    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser1;

    //firebase connection
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sign_in_btn=findViewById(R.id.button_signin_signin);
        sign_up_btn=findViewById(R.id.button_signup_signin);
        forgot_btn=findViewById(R.id.button_forgot_signin);
        name_edt=findViewById(R.id.edittext_email_login);
        password_edt=findViewById(R.id.edittext_password_login);
        progressBar=findViewById(R.id.progressBar_signin);

        firebaseAuth =FirebaseAuth.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        getPermissions();











        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });


        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(name_edt.getText().toString()) && !TextUtils.isEmpty(password_edt.getText().toString())){

                    sign_up_btn.setEnabled(false);
                    String email =name_edt.getText().toString().trim();
                    String password =password_edt.getText().toString();
                    signInWithEmailAndPass(email,password);
                    progressBar.setVisibility(View.VISIBLE);




                }




            }
        });






    }

    private void signInWithEmailAndPass(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {






                FirebaseUser currentUser =firebaseAuth.getCurrentUser();
               // assert currentUser!=null;
                if(currentUser!=null){
                    final String currentUserId =currentUser.getUid();



                    collectionReference.whereEqualTo("uid",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                            if(error!= null){
                                Toast.makeText(MainActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                            }
                            if(!value.isEmpty()){
                                //getting all snapshots
                                for (QueryDocumentSnapshot snapshot : value){

                                    Users users =Users.getInstance();
                                    users.setUsername(snapshot.getString("username"));
                                    users.setUid(snapshot.getString("uid"));
                                    users.setUrl(snapshot.getString("url"));
                                    users.setPoints(snapshot.getDouble("points"));
                                    users.setGender(snapshot.getString("gender"));
                                    users.setPhoneNum(snapshot.getString("phoneNum"));
                                    users.setAccountType(snapshot.getString("accountType"));
                                    users.setEmail(snapshot.getString("email"));

                                    progressBar.setVisibility(View.INVISIBLE);
                                    sign_up_btn.setEnabled(true);


                                    //display list of journals after log in

                                    if(users.getAccountType().equals("restaurant")){
                                        startActivity(new Intent(MainActivity.this, Restaurants.class));
                                        sign_up_btn.setEnabled(true);

                                    } else if (users.getAccountType().equals("hotel")&& firebaseAuth.getCurrentUser()!=null) {
                                        startActivity(new Intent(MainActivity.this, HotelsMainActivity.class));
                                        finish();
                                    } else if (users.getAccountType().equals("shop") && firebaseAuth.getCurrentUser()!=null) {
                                        startActivity(new Intent(MainActivity.this, ShoppingMainActivity.class));
                                        finish();

                                    }


                                }


                            }
                        }
                    });




                }else {
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    sign_up_btn.setEnabled(true);
                }










            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                sign_up_btn.setEnabled(true);
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null){



            currentUser1= firebaseAuth.getCurrentUser();

            final String currentUserId =currentUser1.getUid();
            Users users =Users.getInstance();
            users.setUid(currentUserId);






            collectionReference.whereEqualTo("uid",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if(!value.isEmpty()){
                        //getting all snapshots
                        for (QueryDocumentSnapshot snapshot : value){

                            Users users =Users.getInstance();
                            users.setUsername(snapshot.getString("username"));
                            users.setUid(snapshot.getString("uid"));
                            users.setUrl(snapshot.getString("url"));
                            users.setPoints(snapshot.getDouble("points"));
                            users.setGender(snapshot.getString("gender"));
                            users.setPhoneNum(snapshot.getString("phoneNum"));
                            users.setAccountType(snapshot.getString("accountType"));
                            users.setEmail(snapshot.getString("email"));




                            //display list of journals after log in


                            if(users.getAccountType().equals("restaurant")){
                                startActivity(new Intent(MainActivity.this, Restaurants.class));
                                finish();

                            }else if (users.getAccountType().equals("hotel")&& firebaseAuth.getCurrentUser()!=null) {
                                startActivity(new Intent(MainActivity.this, HotelsMainActivity.class));
                                finish();
                            } else if (users.getAccountType().equals("shop")) {
                                startActivity(new Intent(MainActivity.this, ShoppingMainActivity.class));
                                finish();

                            }


                        }


                    }
                }
            });







        }
    }

    void getPermissions(){



        ActivityResultLauncher<String> arl = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {

            }
        });


        arl.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        arl.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}