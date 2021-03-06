package com.academy.envy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.academy.envy.Model.UserNaNo;
import com.academy.envy.Adapter.UserListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveUsersActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef;
    String SELECTED_ENV;
    CircleImageView image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        image = findViewById(R.id.image);

        SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");

        final ArrayList<UserNaNo> toDisplayList = getActiveUserList(SELECTED_ENV);
//        Collections.sort(toDisplayList);

        UserListAdapter adapter = new UserListAdapter(this, toDisplayList);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!user.getDisplayName().equals(toDisplayList.get(position).getUserName())){
                    String number = toDisplayList.get(position).getNumber();
                    String url = "https://api.whatsapp.com/send?phone=+91"+number;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else{
                    Toast.makeText(getApplicationContext(),"Click on other Users...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<UserNaNo> getActiveUserList(final String SELECTED_ENV) {
        final ArrayList<UserNaNo> list = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference().child("BS2");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange( DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot insideUID : snapshot.child("Users").getChildren()) {
                    try {
                        String number = insideUID.child("number").getValue().toString();
                        String userName =  insideUID.child("userName").getValue().toString();
                        String uID = insideUID.getKey();
                        if (insideUID.child("/boolValues"+SELECTED_ENV+"/w").getValue(Boolean.class)) {
                            list.add(new UserNaNo(number,userName,uID,1));
                        }
                        if (insideUID.child("/boolValues"+SELECTED_ENV+"/wd").getValue(Boolean.class)) {
                            list.add(new UserNaNo(number,userName,uID,2));
                        }
                        if (insideUID.child("/boolValues"+SELECTED_ENV+"/whd").getValue(Boolean.class)) {
                            list.add(new UserNaNo(number,userName,uID,3));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return list;
    }
    public void onStart() {
        super.onStart();
        getSupportActionBar().setTitle(SELECTED_ENV);
    }

}
