package com.example.instantbff;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText fullNameTextView;
    private EditText emailTextView;
    private EditText passwordTextView;
    private EditText cityTextView;
    private EditText instagramTextView;
    private EditText ageTextView;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameTextView = findViewById(R.id.fullName_textView);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        cityTextView = findViewById(R.id.city_textView);
        instagramTextView = findViewById(R.id.instagram_textView);
        ageTextView = findViewById(R.id.age_textView);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullNameTextView.getText().toString().trim();
                String email = emailTextView.getText().toString().trim();
                String password = passwordTextView.getText().toString().trim();
                String city = cityTextView.getText().toString().trim();
                String instagram = instagramTextView.getText().toString().trim();
                String age = ageTextView.getText().toString().trim();

                registerNewUser(name, email, password, city, instagram, age);

                Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerNewUser(String name, String email, String password,
                                String city, String instragram, String age) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        AuthResult tempResult = authResult;
                        FirebaseUser user = mAuth.getCurrentUser();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> person = new HashMap<>();
                        person.put("name", name);
                        person.put("email", email);
                        person.put("city", city);
                        person.put("instagram", instragram);
                        person.put("age", age);

                        String userId = user.getUid();
                        Log.e("BFF", "USERid is " + userId);
                        db.collection("users").document(userId)
                                .set(person)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(RegisterActivity.this, TakeQuizActivity.class);
                                        intent.putExtra("EXTRA_USER_ID", userId);
                                        startActivity(intent);
                                        Log.e("BFF", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("BFF", "Error writing document", e);
                                    }
                                });
                }
                });
    }
}
