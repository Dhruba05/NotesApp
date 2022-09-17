package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditText;
    Button Loginbtn;
    ProgressBar progressBar;
    TextView createAccountbtnTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText=findViewById(R.id.email_et);
        passwordEditText=findViewById(R.id.password_et);
        Loginbtn=findViewById(R.id.login_btn);
        progressBar=findViewById(R.id.progress_bar);
        createAccountbtnTextView=findViewById(R.id.createAccount_textview_btn);

        Loginbtn.setOnClickListener((V) ->loginUser());
        createAccountbtnTextView.setOnClickListener((V) -> startActivity(new Intent(LoginActivity.this
        ,CreateAccountActivity.class)));
    }

    private void loginUser() {
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();


        boolean isValidated=validate(email,password);
        if(!isValidated){
            return;
        }
        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else{
                        Utility.showToast(LoginActivity.this,"Email not verified");
                    }
                }
                else{
                    Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                }
            }
        });

    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            Loginbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            Loginbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validate(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password too short");
            return false;
        }

        return true;
    }
}