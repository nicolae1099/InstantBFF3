package com.example.instantbff;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private String TAG = "BFF";
    private User currentUser;
    private User tempUser;
    private String userId;
    private List<User> users = new ArrayList<>();
    private Integer maxScore = 4;

    private RecyclerView mRecyclerView;
    private MatchAdapter myAdapter;

    private final Integer SCORE_OF_MYSELF = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        userId = getIntent().getStringExtra("EXTRA_USER_ID");

        /*
        myProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MatchActivity.this, MyProfileActivity.class);
            intent.putExtra("EXTRA_USER_ID", userId);
            startActivity(intent);
        });

         */

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference docRef = firebaseFirestore.collection("users");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot collection = task.getResult();
                if (collection != null) {
                    List<DocumentSnapshot> peopleDocuments = collection.getDocuments();

                    for (DocumentSnapshot document : peopleDocuments) {
                        tempUser = new User(
                                String.valueOf(document.getData().get("name")),
                                String.valueOf(document.getData().get("email")),
                                String.valueOf(document.getData().get("city")),
                                String.valueOf(document.getData().get("instagram")),
                                String.valueOf(document.getData().get("age")),
                                String.valueOf(document.getData().get("personality")),
                                String.valueOf(document.getData().get("description"))
                        );
                        tempUser.setAnimals(String.valueOf(document.get("animals")));
                        tempUser.setMusic(String.valueOf(document.get("music")));
                        tempUser.setSport(String.valueOf(document.get("sport")));
                        tempUser.setScore(0);
                        users.add(tempUser);
                        if (document.getId().equals(userId)) {
                            currentUser = tempUser;
                            currentUser.setScore(-1);
                        }
                    }

                    for (User user : users) {
                        Integer score = 0;
                        if (!user.getScore().equals(SCORE_OF_MYSELF)) {
                            if (user.getAnimals().equals(currentUser.getAnimals())) {
                                score++;
                            }
                            if (user.getMusic().equals(currentUser.getMusic())) {
                                score++;
                            }
                            if (user.getSport().equals(currentUser.getSport())) {
                                score++;
                            }
                            if (user.getPersonality().equals(currentUser.getPersonality())) {
                                score++;
                            }

                            user.setScore((score / maxScore) * 100);
                        }
                    }

                    try {
                        mRecyclerView = findViewById(R.id.recyclerView);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                        myAdapter = new MatchAdapter(users);
                        mRecyclerView.setAdapter(myAdapter);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }
}
