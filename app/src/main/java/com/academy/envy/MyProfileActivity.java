package com.academy.envy;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.academy.envy.Utils.DefaultImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.alirezabdn.wp7progress.WP7ProgressBar;


public class MyProfileActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");
    private DatabaseReference dbRef;
    CircleImageView avatar;
    WP7ProgressBar progressBar;
    TextView name,mailTv,nameTv,numberTv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        name = findViewById(R.id.tv_username);
        mailTv = findViewById(R.id.email_tv_detail);
        nameTv = findViewById(R.id.name_tv_detail);
        numberTv = findViewById(R.id.number_tv_detail);
        avatar = findViewById(R.id.img_avatar);
        progressBar = findViewById(R.id.wp7progressBar);

        name.setText(user.getDisplayName());
        nameTv.setText(user.getDisplayName());
        mailTv.setText(user.getEmail());
        progressBar.showProgressBar();

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(avatar);
        }
        else{
            DefaultImage defaultImage = new DefaultImage(user.getDisplayName());
            avatar.setImageResource(defaultImage.getDefaultImage());
        }

        dbRef = FirebaseDatabase.getInstance().getReference().child("BS2/Users/"+user.getUid()+"/number");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    numberTv.setText(snapshot.getValue().toString());
                    progressBar.hideProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                    numberTv.setText("Number not found!");
                    progressBar.hideProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void avatarOnClick(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Avatar")
                .setMessage("Remove current / Upload new")
                .setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, 1);
                            }
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.showProgressBar();

                            addToProfile(null);
                            StorageReference deleteFile = storageReference.child(user.getUid()+".jpeg");
                            deleteFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.hideProgressBar();
                                }
                            });
                            dialogInterface.dismiss();
                        }
                    }).show();
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            try {
                Uri selectedImage = data.getData();
                handleUpload(selectedImage);
                Toast.makeText(getApplicationContext(),"Uploading...",Toast.LENGTH_LONG).show();
                progressBar.showProgressBar();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUpload(Uri selectedImage) {
        final StorageReference addReference = storageReference.child(user.getUid() + ".jpeg");

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);

        addReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        addReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        addToProfile(uri);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.e(TAG, "onFailure: ",e.getCause() );
                    }
                });
    }

    private void addToProfile(Uri uri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Success! Restart App!",Toast.LENGTH_LONG).show();
                            progressBar.hideProgressBar();
                        }
                    }
                });
    }

    public void signoutOnClick(View v){

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public void resetOnClick(View v){

        new AlertDialog.Builder(this)
                .setTitle("Reset My Account")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbRef = FirebaseDatabase.getInstance().getReference();

                        dbRef.child("BS2/Users/" + user.getUid()).removeValue();

                        updateName(null);
                        finish();
                        startActivity(new Intent(getApplicationContext(), RegisterActivity2.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public void numberOnClick(View v){

        View vewInflater = LayoutInflater.from(this)
                .inflate(R.layout.dialog_editnumber, (ViewGroup) v, false);
        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_username);
        new AlertDialog.Builder(this)
                .setTitle("Edit Number")
                .setView(vewInflater)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newValue = input.getText().toString();
                        if(!user.getDisplayName().equals(newValue)){
                            updateNumber(newValue);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void updateNumber(String newValue) {
        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("BS2/Users/" + user.getUid()).child("number").setValue(newValue);
        Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
    }

    public void nameOnClick(View v){
                View vewInflater = LayoutInflater.from(this)
                .inflate(R.layout.dialog_editname, (ViewGroup) v, false);
        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_username);
        input.setText(user.getDisplayName());
        new AlertDialog.Builder(this)
                .setTitle("Edit Name")
                .setView(vewInflater)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newValue = input.getText().toString();
                        if(!user.getDisplayName().equals(newValue)){
                            progressBar.showProgressBar();
                            updateName(newValue);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
    private void updateName(String name){
        dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("BS2/Users/" + user.getUid()).child("userName").setValue(name);

        if(user != null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.hideProgressBar();
                        Log.d("Testing", "User Profile Updated");
                        Toast.makeText(getApplicationContext(),"Success! Restart App!",Toast.LENGTH_LONG).show();
                    }
                    else{
                        progressBar.hideProgressBar();
                        Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void editMailClick(View view) {
        Toast.makeText(getApplicationContext(), "You cannot edit Email ID!", Toast.LENGTH_SHORT).show();
    }
}
