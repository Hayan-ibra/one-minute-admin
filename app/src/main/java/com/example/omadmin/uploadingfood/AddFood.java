package com.example.omadmin.uploadingfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.omadmin.MainActivity;
import com.example.omadmin.R;
import com.example.omadmin.SignUp;
import com.example.omadmin.models.Food;
import com.example.omadmin.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFood extends AppCompatActivity {
    EditText name_edt,ingredients_edt,description_edt,price_edt,time_edt;
    ImageView food_image;
    Button add_button;

    Spinner spinner;

    String food_type_selected;


    //firebase authentication

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //firebase firestore

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Foods");

    //firebase storage for image storage
    private StorageReference storageReference;
    private Uri imageUri;



    //key for result
    int GALLERY_KEY =10;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        name_edt=findViewById(R.id.edittext_food_name_addfood);
        ingredients_edt=findViewById(R.id.edittext_ingredients_addfood);
        description_edt=findViewById(R.id.edittext_description_addfood);
        price_edt=findViewById(R.id.edittext_price_addfood);
        time_edt=findViewById(R.id.edittext_time_addfood);
        food_image=findViewById(R.id.imageView_food_image_addfood);
        add_button=findViewById(R.id.button_add_addfood);

        spinner=findViewById(R.id.spinner_foodType);

        //Firebase
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        //Authentication
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
                if(currentUser !=null){
                    //User Already Logged In
                }else{
                    //not user yet
                }
            }
        };




        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(name_edt.getText().toString()) && !TextUtils.isEmpty(ingredients_edt.getText().toString())
                && !TextUtils.isEmpty(price_edt.getText().toString())
                &&  !TextUtils.isEmpty(time_edt.getText().toString()) && food_type_selected!=null){
                    if(imageUri != null ){
                        String name=name_edt.getText().toString();
                        String ingredients =ingredients_edt.getText().toString();
                        String description =description_edt.getText().toString();
                        double price = Double.parseDouble(price_edt.getText().toString());
                        String time=time_edt.getText().toString();




                        addFoodToFirebase(name,ingredients,description,price,time);

                    }else {
                        Toast.makeText(AddFood.this, "please pick image", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(AddFood.this, "please fill all data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //spinner
        spinner = findViewById(R.id.spinner_foodType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,R.array.food_types
                , // Define an array resource with spinner items
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(R.layout.simple_spinner_itemm);
        spinner.setAdapter(adapter);
        selectedFoodType();



        food_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_KEY);



            }
        });



    }

    private void addFoodToFirebase(String name, String ingredients, String description, double price, String time) {


        final  StorageReference  filepaht =storageReference.child("food_images")
                .child("food_image"+ Timestamp.now().getSeconds());

        //uploading the  image
        filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        double rate =0.0;

                        String imageUrll=uri.toString();
                        Food foods= new Food();
                        foods.setFoodUrl(imageUrll);
                        foods.setDescription(description);
                        foods.setName(name);
                        foods.setIngredients(ingredients);
                        foods.setType(food_type_selected);
                        foods.setRestaurantID(firebaseAuth.getCurrentUser().getUid());
                        foods.setRate(rate);
                        foods.setPrice(price);
                        foods.setTime(time);

                        collectionReference.add(foods).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Food food=Food.getInstance();
                                food.setFoodUrl(imageUrll);
                                food.setDescription(description);
                                food.setName(name);
                                food.setIngredients(ingredients);
                                food.setType(food_type_selected);
                                food.setRestaurantID(firebaseAuth.getCurrentUser().getUid());
                                food.setRate(rate);
                                food.setPrice(price);
                                food.setTime(time);
                                Intent intent =new Intent(AddFood.this, Restaurants.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddFood.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddFood.this, ""+e, Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==GALLERY_KEY && resultCode==RESULT_OK){
            imageUri=data.getData();
            food_image.setImageURI(imageUri);
        }
/*
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                // Use croppedImageUri as needed (e.g., display in an ImageView)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle crop error
            }
        }*/
    }

    public void selectedFoodType(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        food_type_selected=null;
                        break;

                    case 1:
                        food_type_selected="dessert";
                        break;
                    case 2:
                        food_type_selected="burger";
                        break;
                    case 3:
                        food_type_selected="western_foods";
                        break;
                    case 4:
                        food_type_selected="sea_food";
                        break;
                    case 5:
                        food_type_selected="salad";
                        break;
                    case 6:
                        food_type_selected="sandwich";
                        break;
                    case 7:
                        food_type_selected="soup";
                        break;
                    case 8:
                        food_type_selected="pizza";
                        break;
                    case 9:
                        food_type_selected="pasta";
                        break;
                    case 10:
                        food_type_selected="beverage";
                        break;
                    case 11:
                        food_type_selected="fast_food";
                        break;
                    case 12:
                        food_type_selected="meals";
                        break;
                    case 13:
                        food_type_selected="cocktails";
                        break;
                    case 14:
                        food_type_selected="others";
                        break;
                    default:
                        Toast.makeText(AddFood.this, "Wrong", Toast.LENGTH_SHORT).show();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }




}