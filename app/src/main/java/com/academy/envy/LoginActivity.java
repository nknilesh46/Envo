package com.academy.envy;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.academy.envy.Utils.StaticConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.regex.Pattern;

import ir.alirezabdn.wp7progress.WP7ProgressBar;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    FloatingActionButton fab;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword;
    private LovelyProgressDialog waitingDialog;
    WP7ProgressBar progressBar;

  //  private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(getApplicationContext(),currentUser.getDisplayName()+"",Toast.LENGTH_SHORT).show();
//        if(currentUser.getDisplayName()!=null){
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            Log.d(TAG, "Already logged in ");
//            finish();
//        }
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.wp7progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        firstTimeAccess = true;
        mAuth = FirebaseAuth.getInstance();
        initFirebase();
        if(mAuth.getCurrentUser() != null){
            if (mAuth.getCurrentUser().getDisplayName()=="" || mAuth.getCurrentUser().getDisplayName()==null){
                startActivity(new Intent(getApplicationContext(), RegisterActivity2.class));
                finish();
            }
            else{
                startActivity(new Intent(getApplicationContext(), SelectEnvActivity.class));
                finish();
            }
        }
//        else{
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            Log.d(TAG, "Already logged in ");
//            finish();
//        }
    }
    public void clickLogin(View w){
        progressBar.showProgressBar();
//        boolean b = Pattern.matches(String.valueOf(VALID_EMAIL_ADDRESS_REGEX), editTextUsername.getText().toString());
//        Toast.makeText(getApplicationContext(), " "+b,
//                Toast.LENGTH_SHORT).show();
        if(!editTextUsername.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")){
            mAuth.signInWithEmailAndPassword(editTextUsername.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.hideProgressBar();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.getDisplayName()==null){
                                    startActivity(new Intent(getApplicationContext(), RegisterActivity2.class));
                                    finish();
                                }
                                else{
                                    startActivity(new Intent(getApplicationContext(), SelectEnvActivity.class));
                                    finish();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                progressBar.hideProgressBar();
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Empty mail/password.",
                    Toast.LENGTH_SHORT).show();
            progressBar.hideProgressBar();
        }

    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
       // authUtils = new AuthUtils();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        LoginActivity.this.finish();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, RegisterActivity.class), StaticConfig.REQUEST_CODE_REGISTER);
        }
    }


}
