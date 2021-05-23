package com.example.instantbff;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    private String userId;
    private List<User> users = new ArrayList<>();
    private Integer maxScore = 4;

    private RecyclerView mRecyclerView;
    private static MatchAdapter myAdapter;

    private final Integer SCORE_OF_MYSELF = -1;
    private final String URL_INITIAL = "https://firebasestorage.googleapis.com/v0/b/peppy-breaker-287515.appspot.com/o/img%2F";
    private final String URL_FINAL = "?alt=media&token=64facbd5-c118-4fed-b723-9b4489199cae";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        userId = getIntent().getStringExtra("EXTRA_USER_ID");

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference docRef = firebaseFirestore.collection("users");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot collection = task.getResult();
                if (collection != null) {
                    List<DocumentSnapshot> peopleDocuments = collection.getDocuments();

                    for (DocumentSnapshot document : peopleDocuments) {
                        users.add(new User(
                                String.valueOf(document.getData().get("name")),
                                String.valueOf(document.getData().get("email")),
                                String.valueOf(document.getData().get("city")),
                                String.valueOf(document.getData().get("instagram")),
                                String.valueOf(document.getData().get("age")),
                                String.valueOf(document.getData().get("personality")),
                                String.valueOf(document.getData().get("description"))
                        ));
                        users.get(users.size()-1).setAnimals(String.valueOf(document.get("animals")));
                        users.get(users.size()-1).setMusic(String.valueOf(document.get("music")));
                        users.get(users.size()-1).setSport(String.valueOf(document.get("sport")));
                        users.get(users.size()-1).setFood(String.valueOf(document.get("food")));
                        users.get(users.size()-1).setMovie(String.valueOf(document.get("movie")));
                        users.get(users.size()-1).setImage_url(URL_INITIAL + document.getId() + URL_FINAL);
                        users.get(users.size()-1).setUserId(document.getId());

                        users.get(users.size()-1).setScore(0);

                        if (document.getId().equals(userId)) {
                            currentUser = users.get(users.size()-1);
                            currentUser.setScore(-1);
                        }
                    }

                    for (User user : users) {
                        Integer score = 0;
                        if (user.getScore() != SCORE_OF_MYSELF) {
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
                            if (user.getFood().equals(currentUser.getFood())) {
                                score++;
                            }
                            if (user.getMovie().equals(currentUser.getMovie())) {
                                score++;
                            }

                            user.setScore((int) (( (double) score / (double) maxScore) * 100));
                        }
                    }

                    try {
                        mRecyclerView = findViewById(R.id.recyclerView);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerView.addItemDecoration(new DividerItemDecoration(MatchActivity.this, DividerItemDecoration.VERTICAL));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_myprofile:
                Intent intent = new Intent(MatchActivity.this, MyProfileActivity.class);
                intent.putExtra("EXTRA_USER_ID", userId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
