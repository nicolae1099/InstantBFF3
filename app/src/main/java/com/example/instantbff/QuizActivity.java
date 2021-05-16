package com.example.instantbff;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private Button quizButton1;
    private Button quizButton2;
    private Button quizButton3;
    private Button quizButton4;
    private Button quizButton5;
    private Button quizButton6;

    private List<String> choices = new ArrayList<>();
    private List<String> questions = new ArrayList<>();
    private List<String> topics = new ArrayList<>();
    private List<String> responses = new ArrayList<>();
    private Integer topicCounter;
    private final Integer TOPIC_SIZE = 6;

    private TextView quizQuestion;

    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private User user;
    private String TAG  = "BFF";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("EXTRA_USER_ID");
        Log.e(TAG, "USER_id is" + userId);

        topicCounter = 0;
        choices.addAll(Arrays.asList("cat", "dog", "rabbit", "fish", "monkey", "snake"));
        questions.add("Which is your favorite animal?");
        topics.add("animals");

        choices.addAll(Arrays.asList("rock", "pop", "manele", "jazz", "clasic", "nightcore"));
        questions.add("What type of music do you usually listen to?");
        topics.add("music");

        choices.addAll(Arrays.asList("running", "swimming", "gym", "dancing", "skating", "sleeping"));
        questions.add("Which one is your favorite sport?");
        topics.add("sports");

        quizButton1 = findViewById(R.id.quiz_button_1);
        quizButton1.setOnClickListener(this);
        quizButton2 = findViewById(R.id.quiz_button_2);
        quizButton2.setOnClickListener(this);
        quizButton3 = findViewById(R.id.quiz_button_3);
        quizButton3.setOnClickListener(this);
        quizButton4 = findViewById(R.id.quiz_button_4);
        quizButton4.setOnClickListener(this);
        quizButton5 = findViewById(R.id.quiz_button_5);
        quizButton5.setOnClickListener(this);
        quizButton6 = findViewById(R.id.quiz_button_6);
        quizButton6.setOnClickListener(this);

        quizQuestion = findViewById(R.id.quizTopic_textView);

        quizButton1.setText(choices.get(topicCounter));
        quizButton2.setText(choices.get(topicCounter + 1));
        quizButton3.setText(choices.get(topicCounter + 2));
        quizButton4.setText(choices.get(topicCounter + 3));
        quizButton5.setText(choices.get(topicCounter + 4));
        quizButton6.setText(choices.get(topicCounter + 5));

        quizQuestion.setText(questions.get(topicCounter));


    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        topicCounter++;
        quizQuestion.setText(questions.get(topicCounter));
        responses.add(String.valueOf(button.getText()));

        quizButton1.setText(choices.get(topicCounter * TOPIC_SIZE));
        quizButton2.setText(choices.get(topicCounter * TOPIC_SIZE + 1));
        quizButton3.setText(choices.get(topicCounter * TOPIC_SIZE + 2));
        quizButton4.setText(choices.get(topicCounter * TOPIC_SIZE + 3));
        quizButton5.setText(choices.get(topicCounter * TOPIC_SIZE + 4));
        quizButton6.setText(choices.get(topicCounter * TOPIC_SIZE + 5));

        Log.e("BFF", "" + topicCounter + " " + choices.size() + " " + responses.size());
        if (topicCounter == (choices.size() - 6)) {

            DocumentReference docRef = firebaseFirestore.collection("users").document(userId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        user = new User(
                                String.valueOf(document.getData().get("name")),
                                String.valueOf(document.getData().get("email")),
                                String.valueOf(document.getData().get("city")),
                                String.valueOf(document.getData().get("instagram")),
                                String.valueOf(document.getData().get("age")),
                                String.valueOf(document.getData().get("personality")),
                                String.valueOf(document.getData().get("description"))
                        );

                        Map<String, Object> changedUser = new HashMap<>();
                        changedUser.put("name", user.getFullName());
                        changedUser.put("email", user.getEmail());
                        changedUser.put("city", user.getCity());
                        changedUser.put("instagram", user.getInstagram());
                        changedUser.put("age", user.getAge());

                        changedUser.put(topics.get(0), String.valueOf(responses.get(0)));
                        changedUser.put(topics.get(1), String.valueOf(responses.get(1)));
                        //changedUser.put(topics.get(2), String.valueOf(responses.get(2)));

                        firebaseFirestore.collection("users").document(userId)
                                .set(changedUser)
                                .addOnSuccessListener(aVoid -> {
                                    finish();
                                    Log.e("BFF", "DocumentSnapshot successfully written!");
                                })
                                .addOnFailureListener(e -> Log.e("BFF", "Error writing document", e));

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }
}
