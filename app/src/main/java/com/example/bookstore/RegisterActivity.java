package com.example.bookstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    EditText userNameET;
    EditText userEmailET;
    EditText passwordET;
    EditText passwordConfirmET;
    EditText phoneNumberET;
    EditText addressET;
    RadioGroup accountTypeGroup;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secretKey = getIntent().getIntExtra("SECRET_KEY",0);


        if(secretKey != 99){
            finish();
        }

        userNameET = findViewById(R.id.editTextUserName);
        userEmailET = findViewById(R.id.editTextUserEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordConfirmET = findViewById(R.id.editTextPasswordConfirm);
        phoneNumberET = findViewById(R.id.editTextPhoneNumber);
        addressET = findViewById(R.id.editTextAddress);
        accountTypeGroup = findViewById(R.id.accountTypeGroup);
        accountTypeGroup.check(R.id.customerRadioButton);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String userName = preferences.getString("Username","");
        String password = preferences.getString("Password","");

        userNameET.setText(userName);
        passwordET.setText(password);
        passwordConfirmET.setText(password);
        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG,"onCreate");
    }

    public void confirmReg(View view) {
        String userName = userNameET.getText().toString();
        String userEmail = userEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String address = addressET.getText().toString();

        if (!password.equals(passwordConfirm)){
            Log.e(LOG_TAG,"Error in password confirmation");
        }

        int checkedId = accountTypeGroup.getCheckedRadioButtonId();
        RadioButton radioButton = accountTypeGroup.findViewById(checkedId);
        accountTypeGroup.indexOfChild(radioButton);
        String accountType = radioButton.getText().toString();

        Log.i(LOG_TAG,"Registered as: " + userName + "\nE-mail: "+ userEmail);
        mAuth.createUserWithEmailAndPassword(userEmail,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG,"User created successfully!");
                    startShopping();
                }
                else{
                    Log.d(LOG_TAG,"User creation unsuccessful");
                    Toast.makeText(RegisterActivity.this,"User creation unsuccessful: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void startShopping(/*?*/){
        Intent intent = new Intent(this, ShopListActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG,"onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG,"onRestart");
    }
}