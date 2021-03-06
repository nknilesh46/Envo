package com.academy.envy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.academy.envy.Model.Bool3false;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ir.alirezabdn.wp7progress.WP7ProgressBar;


public class RegisterActivity2 extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    FloatingActionButton fab;
    CardView cvAdd;
    WP7ProgressBar progressBar;


    private EditText editTextDisplayName, editTextNumber, etMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        progressBar = findViewById(R.id.wp7progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        editTextDisplayName = (EditText) findViewById(R.id.et_dispalyname);
        editTextNumber = (EditText) findViewById(R.id.et_number);
        etMail = (EditText) findViewById(R.id.mail);
        etMail.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Signed Out!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });
        etMail.setText(user.getEmail());
    }

    public void clickRegister2(View w){
        progressBar.showProgressBar();
        editTextDisplayName = (EditText) findViewById(R.id.et_dispalyname);
        editTextNumber = (EditText) findViewById(R.id.et_number);

        if(TextUtils.isEmpty(editTextDisplayName.getText().toString()) || TextUtils.isEmpty(editTextDisplayName.getText().toString())){
            //do nothing
            Toast.makeText(getApplicationContext(), "Fields are empty!", Toast.LENGTH_SHORT).show();
            progressBar.hideProgressBar();

        }
        else {
            if(editTextNumber.getText().toString().length()==10) {
                dbRef = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Bool3false> datamap = new HashMap<String, Bool3false>();
                datamap.put("boolValuesT6", new Bool3false());
                datamap.put("boolValuesT13", new Bool3false());


                dbRef.child("BS2/Users/"+ user.getUid()).setValue("");
                dbRef.child("BS2/Users/"+ user.getUid()).setValue(datamap);
                dbRef.child("BS2/Users/" + user.getUid()).child("userName").setValue(editTextDisplayName.getText().toString());
                dbRef.child("BS2/Users/" + user.getUid()).child("number").setValue(editTextNumber.getText().toString());

                userProfile(editTextDisplayName.getText().toString());
                startActivity(new Intent(getApplicationContext(), SelectEnvActivity.class));
                finish();
                progressBar.hideProgressBar();
            }
            else{
                Toast.makeText(getApplicationContext(), "Number must be 10 digits!", Toast.LENGTH_SHORT).show();
                progressBar.hideProgressBar();

            }
        }
    }

    private void userProfile(String name){
        if(user != null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

            user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("Testing", "User Profile Updated");
                    }
                }
            });
        }
    }
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                RegisterActivity2.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }
            @Override
            public void onTransitionCancel(Transition transition) { }
            @Override
            public void onTransitionPause(Transition transition) { }
            @Override
            public void onTransitionResume(Transition transition) { }
        });
    }
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
}
