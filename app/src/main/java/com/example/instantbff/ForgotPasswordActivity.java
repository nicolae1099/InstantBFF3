package com.example.instantbff;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button forgotPasswordButton;
    private EditText emailForgotPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        emailForgotPassword = findViewById(R.id.email_forgotPassword);

        forgotPasswordButton = findViewById(R.id.forgotPassword_button);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailForgotPassword.getText().toString().trim();

                forgotMyPassword(email);

                Toast.makeText(ForgotPasswordActivity.this, "Forgot Password successful", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    public void forgotMyPassword(String email) {
        FirebaseAuth db = FirebaseAuth.getInstance();

        db.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Email sent!");
                        }
                    }
                });
    }
}
