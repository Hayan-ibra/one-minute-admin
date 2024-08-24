package com.example.omadmin.tourist;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omadmin.R;
import com.example.omadmin.models.TouristDestinations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UpdateDestinationActivity extends AppCompatActivity {
    private EditText edt_name,edt_location,edt_description;
    Spinner spinner;
    private ImageView iv_profile;
    private Button button_add;
    private ProgressBar progressBar;
    EditText edt_time_from,edt_time_to;
    RadioButton rad_am_from,rad_pm_from,rad_am_to,rad_pm_to;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Tourist");


    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    String selected_city;
    ActivityResultLauncher<String> arl;

    String amOrPmFrom;

    String amOrPmTo;
    TouristDestinations destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_destination);
        edt_name=findViewById(R.id.UpdateDestinationActivity_edittext_name);
        edt_description=findViewById(R.id.UpdateDestinationActivity_edittext_description);
        edt_location=findViewById(R.id.UpdateDestinationActivity_edittext_location);
        button_add=findViewById(R.id.UpdateDestinationActivity_button_add);
        iv_profile=findViewById(R.id.UpdateDestinationActivity_image);
        progressBar=findViewById(R.id.UpdateDestinationActivity_progress_bar);
        spinner=findViewById(R.id.UpdateDestinationActivity_spinner);
        edt_time_from=findViewById(R.id.UpdateDestinationActivity_edittext_time_from);
        edt_time_to=findViewById(R.id.UpdateDestinationActivity_edittext_time_to);
        rad_am_from=findViewById(R.id.UpdateDestinationActivity_radio_button_am_from);
        rad_pm_from=findViewById(R.id.UpdateDestinationActivity_radio_button_pm_from);
        rad_am_to=findViewById(R.id.UpdateDestinationActivity_radio_button_am_to);
        rad_pm_to=findViewById(R.id.UpdateDestinationActivity_radio_button_pm_to);
        Intent intent=getIntent();
        destination= (TouristDestinations) intent.getSerializableExtra("dest");
        fillData(destination);
        registerForARL();
        setSelectedItem();
        progressBar.setVisibility(View.GONE);


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rad_am_to.isChecked()){
                    amOrPmTo=" Am";
                } else if (rad_pm_to.isChecked()) {
                    amOrPmTo=" Pm";
                }

                if (rad_am_from.isChecked()){
                    amOrPmFrom=" Am";
                } else if (rad_pm_from.isChecked()) {
                    amOrPmFrom=" Pm";
                }
                if (!TextUtils.isEmpty(edt_description.getText().toString()) &&
                        !TextUtils.isEmpty(edt_name.getText().toString()) && !TextUtils.isEmpty(edt_location.getText().toString())&&
                        !TextUtils.isEmpty(edt_time_from.getText().toString()) && !TextUtils.isEmpty(edt_time_to.getText().toString()) && amOrPmFrom!=null && amOrPmTo!=null ){

                    if (selected_city!=null ){

                        int timeF=Integer.valueOf(edt_time_from.getText().toString());
                        int timeT=Integer.valueOf(edt_time_to.getText().toString());
                        if (timeF<=12 && timeT<=12 && timeT>=0 && timeF>=0 ){
                            String name=edt_name.getText().toString();
                            String location=edt_location.getText().toString();
                            String description=edt_description.getText().toString();
                            String id= UUID.randomUUID().toString();
                            progressBar.setVisibility(View.VISIBLE);
                            String timeFrom=String.valueOf(timeF)+amOrPmFrom;
                            String timeTo =String.valueOf(timeT)+amOrPmTo;

                            updateDestination(name,location,description,id,imageUri,timeFrom,timeTo);

                        }else {
                            Toast.makeText(UpdateDestinationActivity.this, "Please select correct time", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(UpdateDestinationActivity.this, "please select city", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(UpdateDestinationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arl.launch("image/*");
            }
        });

    }



    private void updateDestination(String name, String location, String description, String id, Uri imageUri, String timeFrom, String timeTo) {
        if (imageUri!=null){
            final  StorageReference  filepaht =storageReference.child("destinations")
                    .child("destination_"+ Timestamp.now().getSeconds());
            filepaht.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            collectionReference.whereEqualTo("id",destination.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (!value.isEmpty()){
                                        DocumentSnapshot snapshot=value.getDocuments().get(0);
                                        DocumentReference documentReference=snapshot.getReference();
                                        TouristDestinations destinations=new TouristDestinations();
                                        destinations.setDescription(description);
                                        destinations.setImageUrl(imageUrl);
                                        destinations.setName(name);
                                        destinations.setLocation(location);
                                        destinations.setId(id);
                                        destinations.setCity(selected_city);
                                        destinations.setTimeFrom(timeFrom);
                                        destinations.setTimeTo(timeTo);

                                        documentReference.set(destinations).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(UpdateDestinationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(UpdateDestinationActivity.this,TouristMainActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UpdateDestinationActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                            }
                                        });




                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else {
            collectionReference.whereEqualTo("id",destination.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (!value.isEmpty()){
                        DocumentSnapshot snapshot=value.getDocuments().get(0);
                        DocumentReference documentReference=snapshot.getReference();
                        TouristDestinations destinations=new TouristDestinations();
                        destinations.setDescription(description);
                        destinations.setImageUrl(destinations.getImageUrl());
                        destinations.setName(name);
                        destinations.setLocation(location);
                        destinations.setId(id);
                        destinations.setCity(selected_city);
                        destinations.setTimeFrom(timeFrom);
                        destinations.setTimeTo(timeTo);

                        documentReference.set(destinations).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UpdateDestinationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateDestinationActivity.this,TouristMainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateDestinationActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                }
            });


        }



    }

    private void fillData(TouristDestinations destination) {
        edt_description.setText(destination.getDescription());
        edt_location.setText(destination.getLocation());
        edt_name.setText(destination.getName());
        Glide.with(this).load(destination.getImageUrl()).into(iv_profile);


    }
    public void setSelectedItem(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    selected_city=spinner.getItemAtPosition(position).toString();
                } else if (position == 0) {
                    selected_city=null;
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

    }
    private  void  registerForARL(){

        arl = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {

                imageUri=o;

                if (imageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream);
                        if (selectedBitmap != null) {
                            // Manually crop the image to a 4:3 aspect ratio
                            Bitmap croppedBitmap = cropToAspectRatio(selectedBitmap, 4, 3);
                            imageUri=saveBitmapToFile(croppedBitmap,UpdateDestinationActivity.this);
                            iv_profile.setImageBitmap(croppedBitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }



    private Bitmap cropToAspectRatio(Bitmap bitmap, int aspectX, int aspectY) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth, newHeight;
        if (width * aspectY > height * aspectX) {
            newWidth = height * aspectX / aspectY;
            newHeight = height;
        } else {
            newWidth = width;
            newHeight = width * aspectY / aspectX;
        }
        int xOffset = (width - newWidth) / 2;
        int yOffset = (height - newHeight) / 2;
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newWidth, newHeight);
    }


    public Uri saveBitmapToFile(Bitmap bitmap, Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save the bitmap to the file
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(image);
    }
}