package com.example.instantbff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TakeQuizActivity extends AppCompatActivity {

    private Button takeQuizButton;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firsttimelogin);

        userId = getIntent().getStringExtra("EXTRA_USER_ID");
        takeQuizButton = findViewById(R.id.takeQuiz_button);
        takeQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeQuiz();

                Toast.makeText(TakeQuizActivity.this, "Quiz was successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TakeQuizActivity.this, QuizActivity.class);
                intent.putExtra("EXTRA_USER_ID", userId);
                startActivity(intent);
                //finish();
            }
        });
    }

    public void takeQuiz() {
        // WELL TAKE QUIZ TO DO
    }
}
