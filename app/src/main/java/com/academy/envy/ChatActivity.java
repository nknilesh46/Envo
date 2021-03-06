package com.academy.envy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.academy.envy.Model.MessageDetails;
import com.academy.envy.Adapter.MessageListAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP7ProgressBar;

import static com.google.firebase.messaging.Constants.TAG;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView cheesyList;
    FirebaseAuth mAuth;
    WP7ProgressBar progressBar;
    EditText mEditText;
    private long timeRemaining = 0;
    private RequestQueue mRequestQue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    String SELECTED_ENV;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_message_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = findViewById(R.id.wp7progressBar);
        progressBar.showProgressBar();

        mAuth = FirebaseAuth.getInstance();
        final String SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");

        mRequestQue = Volley.newRequestQueue(this);


        Button mButton = findViewById(R.id.button_chatbox_send);
        ImageButton mPopup = findViewById(R.id.button_group_chat_upload);


        final EditText mEditText = findViewById(R.id.edittext_chatbox);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fetchMessages(SELECTED_ENV);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderID = mAuth.getCurrentUser().getUid();
                String senderName = mAuth.getCurrentUser().getDisplayName();
                String msg = mEditText.getText().toString();
                String msgSent;
                try{
                    msgSent = "Increasing "+ Integer.parseInt(msg)+" days";

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    msgSent = msg;
                }
                mEditText.setText("");
                MessageDetails message = new MessageDetails(senderID,senderName,msgSent);

                if(msg.length()>0){
                    sendMessages(message,SELECTED_ENV);
                }

            }
        });
//        mPopup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {



//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View layout = inflater.inflate(R.layout.pop_up, (ViewGroup) findViewById(R.id.PopupLayout1));
//                final PopupWindow pw = new PopupWindow(layout,400,300,true);
//                pw.showAtLocation(findViewById(R.id.layout_group_chat_root), Gravity.LEFT, 50, 100);
////                pw.showAsDropDown(findViewById(R.id.layout_group_chat_root),50,500);
//                CheckBox mCheckbox = layout.findViewById(R.id.AllCB);
//                mCheckbox.setChecked(false);

//                btnOk.setOnClickListener(new View.OnClickListener() {
//
//                    public void onClick(View v) {
//                        mEditText.setText("@AllActive ");
//                        pw.dismiss();
//                    }
//                });


//                mCheckbox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        boolean checked = ((CheckBox) view).isChecked();
//                        if(checked){
//                            mEditText.setText("@AllActive: ");
//                            mEditText.setSelection(mEditText.getText().length());
//                        }
//                        if (!checked){
//                            mEditText.setText("");
//                        }
//                    }
//                });
//            }
//        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchMessages(final String SELECTED_ENV) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        cheesyList = (RecyclerView)findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        cheesyList.setLayoutManager(linearLayoutManager);
        db.collection(SELECTED_ENV).orderBy("dateSent", Query.Direction.ASCENDING) //.orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null || value.size()==0){//something went wrong
                        }
                        else{
                            List<MessageDetails> messages = value.toObjects(MessageDetails.class);
                            if(messages.get(messages.size()-1).getDateSent()!=null) {
                                cheesyList.setAdapter(new MessageListAdapter(messages, mAuth.getCurrentUser().getUid()));
                            }
                        }

                    }
                });
        Log.d(TAG, "fetchMessages end called ");
        progressBar.hideProgressBar();

    }

    private void sendMessages(final MessageDetails message, final String SELECTED_ENV) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(SELECTED_ENV).add(message)
                .addOnCompleteListener(this, new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.d("C","SENT");
                            if(isSubstring(message.getMsg().toLowerCase(),"increasing")){
                                sendNotification(message, SELECTED_ENV, "dep");
                            }

                            if(isSubstring(message.getMsg().toLowerCase(),"@allactive")){
                                sendNotification(message, SELECTED_ENV, "dep");
                                sendNotification(message, SELECTED_ENV, "act");
                            }

                            if(SELECTED_ENV.equals("Chat")){
                                sendNotification(message, SELECTED_ENV, "dep");
                            }
                        }
                        else{
                            Log.d("C","error");
                            Toast.makeText(ChatActivity.this, "Msg Not sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean isSubstring(String msg, String substr) {
        int M = substr.length();
        int N = msg.length();

        /* A loop to slide pat[] one by one */
        for (int i = 0; i <= N - M; i++) {
            int j;

            /* For current index i, check for
            pattern match */
            for (j = 0; j < M; j++)
                if (msg.charAt(i + j)
                        != substr.charAt(j))
                    break;

            if (j == M)
                return true;
        }
        return false;
    }

    private void sendNotification(MessageDetails message, String SELECTED_ENV, String to) {
        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+SELECTED_ENV.concat(to));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",message.getMsg());
            notificationObj.put("body",SELECTED_ENV);

            JSONObject extraData = new JSONObject();
            extraData.put("message",message.getSenderName()+" : "+message.getMsg());
            extraData.put("env",SELECTED_ENV);

//            json.put("notification",notificationObj);
            json.put("data",extraData);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "response  "+response, Toast.LENGTH_LONG);
                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAiuAYc5U:APA91bGtGplRQBSYYRLYJbtZ6TJPJlisQsKwA1fQj1bqHj7nySV4NvmJ8-ww67ceoEbzU4wjEMOdyDOg8hSJl5Lql7U_j6-d6AGWbICx70A4UFx1l6wKJzOI8bA3zicuBhyNgNzKeg_y");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        final String SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");
        getSupportActionBar().setTitle(SELECTED_ENV);
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ibutton, menu);
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
            final String SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");
            Intent intent = new Intent(this, InfoActivity.class);
            if(SELECTED_ENV.equals("Chat")){
                intent.putExtra("FileName", "Chat.jpeg");
            }
            else {
                intent.putExtra("FileName", "TChat.jpeg");
            }
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ImageBtnClick(View view) {
        final String SELECTED_ENV =  (String)getIntent().getSerializableExtra("SELECTED_ENV");
        EditText mEditText = findViewById(R.id.edittext_chatbox);


        if(SELECTED_ENV.equals("Chat")){
            new AlertDialog.Builder(this)
                    .setTitle("Notification:-")
                    .setPositiveButton("On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseMessaging.getInstance().subscribeToTopic(SELECTED_ENV+"dep");
                        }
                    })
                    .setNegativeButton("Off", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(SELECTED_ENV + "dep");
                        }
                    }).show();
        }
        else{
             mEditText.setText(mEditText.getText()+" @AllActive");
        }

    }
}
