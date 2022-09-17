package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText,passwordEditText,confirmEditText;
    Button createAccountbtn;
    ProgressBar progressBar;
    TextView loginbtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        emailEditText=findViewById(R.id.email_et);
        passwordEditText=findViewById(R.id.password_et);
        confirmEditText=findViewById(R.id.confirm_password_et);
        createAccountbtn=findViewById(R.id.create_accnt_btn);
        progressBar=findViewById(R.id.progress_bar);
        loginbtnTextView=findViewById(R.id.login_textview_btn);

        createAccountbtn.setOnClickListener(v ->createAccount() );
        loginbtnTextView.setOnClickListener(v -> finish());

    }

    void createAccount() {
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        String confirmPassword=confirmEditText.getText().toString();

        boolean isValidated=validate(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        createAccountFirebase(email,password);


    }
    void createAccountFirebase(String email,String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this
                , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            Utility.showToast(CreateAccountActivity.this,"Account created succesfully,check Email to Verify");
                            //Toast.makeText(CreateAccountActivity.this, "Account created succesfully,check Email to Verify", Toast.LENGTH_SHORT).show();
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        firebaseAuth.signOut();
                        finish();
                        }
                        else {
                            //failure
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                          //  Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });



    }
    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountbtn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            createAccountbtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validate(String email,String password,String confirmPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password too short");
            return false;
        }
        if (!password.equals(confirmPassword)) {

            confirmEditText.setError("Password not matched");
            return false;
        }
        return true;
    }
}