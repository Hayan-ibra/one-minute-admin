package com.example.omadmin.hotels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.omadmin.R;
import com.example.omadmin.models.HotelRoom;
import com.example.omadmin.models.RoomImages;
import com.example.omadmin.models.TouristDestinations;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
//

public class RoomUploadImage extends AppCompatActivity {
    Button button;
    Uri selectedImageUri;
    HotelRoom room;
    int x;
    TouristDestinations destinations;
    ProgressBar progressBar;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_PICK_IMAGE = 102;
    private ImageView imageView;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceImages=db.collection("RoomImages");//TODO : asddddddddddddd
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_upload_image);
        button=findViewById(R.id.RoomUploadImage_button);
        imageView=findViewById(R.id.RoomUploadImage_imageView);
        progressBar=findViewById(R.id.RoomUploadImage_progressBar);
        progressBar.setVisibility(View.GONE);
        Intent intent=getIntent();
        x=intent.getIntExtra("int",0);
        if (x==0){
            room= (HotelRoom) intent.getSerializableExtra("rooom");
        } else if (x==1) {
            destinations= (TouristDestinations) intent.getSerializableExtra("dest");
        }

        imageView.setOnClickListener(v -> {
            if (checkPermissions()) {
                pickImageFromGallery();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImageUri != null ){
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImage(selectedImageUri);
                }
            }
        });
    }

    private void uploadImage(Uri ImageUri) {
        final  StorageReference  filepaht =storageReference.child("rooms_images")
                .child("room_"+ Timestamp.now().getSeconds());
        filepaht.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepaht.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL=uri.toString();
                        String roomImageId= UUID.randomUUID().toString();
                        RoomImages roomImages=new RoomImages();
                        if (x==0){
                            roomImages.setRoomId(room.getRoomId());
                        } else if (x==1) {
                            roomImages.setRoomId(destinations.getId());
                        }

                        roomImages.setImageId(roomImageId);
                        roomImages.setImageURL(imageURL);
                        collectionReferenceImages.add(roomImages).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                Toast.makeText(RoomUploadImage.this, "Success", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RoomUploadImage.this, "failed to upload image ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream);
                    if (selectedBitmap != null) {
                        // Manually crop the image to a 4:3 aspect ratio
                        Bitmap croppedBitmap = cropToAspectRatio(selectedBitmap, 4, 3);
                        selectedImageUri=saveBitmapToFile(croppedBitmap,this);
                        imageView.setImageBitmap(croppedBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
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