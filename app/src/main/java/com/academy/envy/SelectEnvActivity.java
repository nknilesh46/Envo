package com.academy.envy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import ir.alirezabdn.wp7progress.WP7ProgressBar;


public class SelectEnvActivity extends AppCompatActivity  {
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectenvv);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final TextView loggedInAs = findViewById(R.id.loggedInAs);
        if(user.getDisplayName()!=null || user.getDisplayName()!=""){
            loggedInAs.setText("Hi "+user.getDisplayName());
        }
    }


    public void onT6envClick(View view) {
        if(isNetworkConnected()) {
            Intent intent = new Intent(this, EnvActivity.class);
            intent.putExtra("SELECTED_ENV","T6");
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Check network...", Toast.LENGTH_SHORT).show();
        }
    }

    public void onT13envClick(View view) {
        if(isNetworkConnected()) {
            Intent intent = new Intent(this, EnvActivity.class);
            intent.putExtra("SELECTED_ENV","T13");
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Check network...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.ibutton) {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("FileName", "SelectEnv.jpeg");
            startActivity(intent);
            return true;
        }

        if (id == R.id.my_profile) {
            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
            return true;
        }
        if (id == R.id.action_admin) {
            if(isNetworkConnected()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            else{
                Toast.makeText(getApplicationContext(), "Check network...", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.regular_chat) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("SELECTED_ENV", "Chat");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    }
