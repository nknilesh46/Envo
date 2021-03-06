package com.academy.envy;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FilenameFilter;

public class InfoActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        imageView = findViewById(R.id.imageView);

        String FileName =  (String)getIntent().getSerializableExtra("FileName");
//        Toast.makeText(getApplicationContext(),"Opening "+FileName,Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"Opening "+FirebaseStorage.getInstance().getReference().child("Info/"+FileName),Toast.LENGTH_SHORT).show();


        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Info/"+FileName);

        storageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(imageView);
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Not found!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
