package com.academy.envy;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DatabaseReference dbRef;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("ADMIN MODE");
            verifyPIN();
    }

    private void verifyPIN() {
        dbRef = FirebaseDatabase.getInstance().getReference().child("BS2/AdminMode");
        View vewInflater = LayoutInflater.from(this)
                .inflate(R.layout.dialog_adminpin, (ViewGroup) null, false);
        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_username);
        new AlertDialog.Builder(this)
                .setTitle("Enter Admin PIN")
                .setView(vewInflater)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        final String newValue = input.getText().toString();
                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("admin", "onDataChange: "+newValue+" "+snapshot.getValue().toString());
                                try {
                                    if(newValue.equals(snapshot.getValue().toString())){
                                        dialogInterface.dismiss();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Wrong PIN",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
//                                finish();
                            }
                        });
//                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false).show();

    }

    public void b1(View view) {
    }

    public void b2(View view) {
    }

    public void b3(View view) {
    }

    public void b4(View view) {
    }
}