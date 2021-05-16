package com.example.instantbff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseAuth mAuth;
    private String TAG = "BFF";
    private EditText emailEditText;
    private EditText passwordEditText;

    private TextView registerTextView;
    private TextView forgotPasswordTextView;
    private Button loginButton;
    private ProgressBar progressBar;

    private CheckBox rememberMeCheckBox;

    private boolean first_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerTextView = findViewById(R.id.register_textView);
        registerTextView.setOnClickListener(this);

        forgotPasswordTextView = findViewById(R.id.forgot_password_textView);
        forgotPasswordTextView.setOnClickListener(this);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        emailEditText = findViewById(R.id.email_textView);
        passwordEditText = findViewById(R.id.password_textView);

        progressBar = findViewById(R.id.progressBar);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkBox = preferences.getString("remember", "");

        /*if (checkBox.equals("true")) {
            startActivity(new Intent(MainActivity.this, MyProfileActivity.class));
        } else if (checkBox.equals("false")) {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }

         */

        rememberMeCheckBox = findViewById(R.id.rememberMe_checkBox);
        rememberMeCheckBox.setOnCheckedChangeListener(this);

        mAuth = FirebaseAuth.getInstance();

        keepUserLoggedIn();

    }

    private void keepUserLoggedIn() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_textView:
                registerUser();
                break;
            case R.id.forgot_password_textView:
                forgotPassword();
                break;
            case R.id.login_button:
                loginUser();
                break;
        }
    }

    private void forgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);

        String email = emailEditText.getText().toString().trim();
    }

    private void registerUser() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        /*
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Pass is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Min password length should be 6 chars");
            passwordEditText.requestFocus();
            return;
        }
        /*
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email, password);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            first_login = true;
                                            Toast.makeText(MainActivity.this, "Register succes!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);

                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first_login").setValue("true");

                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, "Failed register", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            first_login = false;
                                        }
                                    });

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Failed register", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

         */
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Min password length should be 6 characters");
            passwordEditText.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
                            intent.putExtra("EXTRA_USER_ID", userId);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        progressBar.setVisibility(View.VISIBLE);
        /*
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    DatabaseReference login = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("first_login");

                    login.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null) {
                                // if(snapshot.getValue().toString().compareTo("true") == 0) {
                                if(first_login) {
                                    login.setValue("false");
                                    progressBar.setVisibility(View.GONE);
                                   // startActivity(new Intent(MainActivity.this, InterestsActivity.class));
                                } else {

                                    progressBar.setVisibility(View.GONE);
                                   // startActivity(new Intent(MainActivity.this, RecommendedEvents.class));
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //if (user.isEmailVerified()) {
                    // redirect to user preferences


                    // } else {
                    //      user.sendEmailVerification();
                    //   Toast.makeText(MainActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    //    progressBar.setVisibility(View.GONE);
                    //  }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to login. Check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

         */

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "true");
            editor.apply();
            Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
        } else if (!compoundButton.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            Toast.makeText(this, "UnChecked", Toast.LENGTH_SHORT).show();
        }
    }
}