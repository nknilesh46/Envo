package com.academy.envy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import ir.alirezabdn.wp7progress.WP7ProgressBar;

public class EnvActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef;

    int workingCtr = 0;
    int workingwithDepCtr = 0;
    int workingwithHugeDepCtr =0;

    String SELECTED_ENV;
    WP7ProgressBar progressBar;

    boolean dataReceived = false;
    boolean loadingdata = true;

    Button chat;
    Switch sw1, sw2, sw3;
    TextView workingTV,workingwithDepTV,workingwithHugeDepTV;
    LinearLayout l1,l2,l3;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env2);
        progressBar = findViewById(R.id.wp7progressBar);
        progressBar.showProgressBar();

        SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");
        getSupportActionBar().setTitle(SELECTED_ENV);


        //Rotation locked to PORTRAIT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sw1 = findViewById(R.id.switch1);
        sw2 = findViewById(R.id.switch2);
        sw3 = findViewById(R.id.switch3);

        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        l3=findViewById(R.id.l3);

        chat = findViewById(R.id.chat);
        workingTV = findViewById(R.id.working);
        workingwithDepTV = findViewById(R.id.workingwithDep);
        workingwithHugeDepTV = findViewById(R.id.workingwithHugeDep);


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean open=true;
                if(workingwithDepCtr+workingwithHugeDepCtr<=1 && (sw2.isChecked() || sw3.isChecked())) {
                    open=false;
                    Toast.makeText(getApplicationContext(), "No need to ping! Nobody else is having date dependency!", Toast.LENGTH_LONG).show();
                }
                if(!sw2.isChecked() && !sw3.isChecked()){
                    open=false;
                    Toast.makeText(getApplicationContext(), "Seems like you do not have dependency!", Toast.LENGTH_LONG).show();
                }
                if(open){ chatClick(chat, SELECTED_ENV); }

            }
        });
        chat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chatClick(chat, SELECTED_ENV);
                return false;
            }
        });


            workingTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActiveUserList();
                }
            });

            workingwithDepTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActiveUserList();
                }
            });
            workingwithHugeDepTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActiveUserList();
                }
            });





        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sw1.getTag() != null)
                    return;

                if (isNetworkConnected() && dataReceived && !loadingdata ) {
                    if (isChecked) {
                        // The toggle is enabled
                        FirebaseMessaging.getInstance().subscribeToTopic(SELECTED_ENV+"act");
                        sw2.setClickable(false); l2.setVisibility(View.INVISIBLE);
                        sw3.setClickable(false); l3.setVisibility(View.INVISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/w").setValue(true);
                    } else {
                        // The toggle is disabled
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(SELECTED_ENV+"act");
                        sw2.setClickable(true); l2.setVisibility(View.VISIBLE);
                        sw3.setClickable(true); l3.setVisibility(View.VISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/w").setValue(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                    if (isChecked) {sw1.setTag("ignore");sw1.setChecked(false);sw1.setTag(null);}
                    else sw1.setChecked(true);
                }
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sw2.getTag() != null)
                    return;

                if (isNetworkConnected() && dataReceived && !loadingdata ) {
                    if (isChecked) {
                        // The toggle is enabled
                        FirebaseMessaging.getInstance().subscribeToTopic(SELECTED_ENV.concat("dep"));
                        sw1.setClickable(false); l1.setVisibility(View.INVISIBLE);
                        sw3.setClickable(false); l3.setVisibility(View.INVISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/wd").setValue(true);
                    } else {
                        // The toggle is disabled
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(SELECTED_ENV.concat("dep"));
                        sw1.setClickable(true); l1.setVisibility(View.VISIBLE);
                        sw3.setClickable(true); l3.setVisibility(View.VISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/wd").setValue(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                    if (isChecked) {sw2.setTag("ignore");sw2.setChecked(false);sw2.setTag(null);}
                    else sw2.setChecked(true);
                }
            }
        });

        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(sw3.getTag() != null)
                    return;

                if (isNetworkConnected() && dataReceived && !loadingdata ) {
                    if (isChecked) {
                        // The toggle is enabled
                        FirebaseMessaging.getInstance().subscribeToTopic(SELECTED_ENV.concat("dep"));
                        sw1.setClickable(false); l1.setVisibility(View.INVISIBLE);
                        sw2.setClickable(false); l2.setVisibility(View.INVISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/whd").setValue(true);
                    } else {
                        // The toggle is disabled
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(SELECTED_ENV.concat("dep"));
                        sw1.setClickable(true); l1.setVisibility(View.VISIBLE);
                        sw2.setClickable(true); l2.setVisibility(View.VISIBLE);
                        dbRef.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/whd").setValue(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                    if (isChecked) {sw3.setTag("ignore");sw3.setChecked(false);sw3.setTag(null);}
                    else sw3.setChecked(true);
                }
            }
        });

        loadingdata = false;


    }

    @Override
    public void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Close activity immediately when internet connection turned off
                    if(!isNetworkConnected()){dataReceived=false;finish();}
                }
            }
        }).start();

        dbRef = FirebaseDatabase.getInstance().getReference().child("BS2");



        ValueEventListener nonstoplistner = new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot) {

                //Resetting counter values everytime on data change
                workingCtr=0;
                workingwithDepCtr=0;
                workingwithHugeDepCtr=0;

                //Resetting active user list everytime on data change
//                TextView working = findViewById(R.id.working);
//                TextView workingwithDep = findViewById(R.id.workingwithDep);
//                TextView workingwithHugeDep = findViewById(R.id.workingwithHugeDep);

                //Increment counter values from each user's UID
                    for (DataSnapshot insideUID : snapshot.child("Users").getChildren()) {
                        try {
                            if (insideUID.child("/boolValues"+SELECTED_ENV+"/w").getValue(Boolean.class)) {
                                workingCtr++;
                            }
                            if (insideUID.child("/boolValues"+SELECTED_ENV+"/wd").getValue(Boolean.class)) {
                                workingwithDepCtr++;
                            }
                            if (insideUID.child("/boolValues"+SELECTED_ENV+"/whd").getValue(Boolean.class)) {
                                workingwithHugeDepCtr++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

//                Updating Firebase DB with counter values (can only be viewed in DB)
                dbRef.child(SELECTED_ENV+"/w").setValue(workingCtr);
                dbRef.child(SELECTED_ENV+"/wd").setValue(workingwithDepCtr);
                dbRef.child(SELECTED_ENV+"/whd").setValue(workingwithHugeDepCtr);

                //Updating textViews with counter values
                workingTV.setText(getString(R.string.working, workingCtr));
                workingwithDepTV.setText(getString(R.string.workingwithDep, workingwithDepCtr));
                workingwithHugeDepTV.setText(getString(R.string.workingwithHugeDep, workingwithHugeDepCtr));

                dataReceived = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //    Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };

        dbRef.addValueEventListener(nonstoplistner);


        //Retain the Checkbox values when user starts the activity
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sw1 = findViewById(R.id.switch1);
                sw2 = findViewById(R.id.switch2);
                sw3 = findViewById(R.id.switch3);
                Switch[] sw = {sw1,sw2,sw3};

                Boolean x1 = dataSnapshot.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/w").getValue(Boolean.class);
                Boolean x2 = dataSnapshot.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/wd").getValue(Boolean.class);
                Boolean x3 = dataSnapshot.child("Users/"+user.getUid()+"/boolValues"+SELECTED_ENV+"/whd").getValue(Boolean.class);
                Boolean[] bool = {x1,x2,x3};

                try {
                    for (int i = 0; i < 3; i++) {
                        if (bool[i]) {
                            sw[i].setChecked(true);
                        } else {
                            sw[i].setChecked(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.e(TAG,"Error while reading data");
            }

        });


    }

    public void chatClick(View view, String SELECTED_ENV) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("SELECTED_ENV", SELECTED_ENV);

        if (SELECTED_ENV != null) {
            startActivity(intent);
        }
    }

    public void openActiveUserList(){

        if (isNetworkConnected() && dataReceived && !loadingdata ) {
            //        Toast.makeText(EnvActivity.this,"Opening...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActiveUsersActivity.class);
            intent.putExtra("SELECTED_ENV", SELECTED_ENV);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ibutton, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ibutton) {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra("FileName", "Env.jpeg");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}