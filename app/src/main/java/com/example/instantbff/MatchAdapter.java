package com.example.instantbff;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyHolder> {

    private List<User> users;
    private String currentUserID;
    private Integer currentUserPosition;
    //private List<String> interestedIn = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> changedUser;

    public MatchAdapter(List<User> users) {
        this.users = users;

        firebaseFirestore = FirebaseFirestore.getInstance();
        int i = 0;
        for (User user : users) {
            if (user.getScore() == -1) {
                this.currentUserID = user.getUserId();
                this.currentUserPosition = i;
            }
            i++;
        }
       // Log.e("BFF", currentUserID);
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.name.setText(users.get(position).getFullName());
        holder.description.setText(users.get(position).getDescription());
        String scoreText = users.get(position).getScore() + " % ";
        holder.score.setText(scoreText);
        holder.age.setText(users.get(position).getAge());

        Glide.with(holder.itemView.getContext())
                .load(users.get(position).getImage_url())
                .into(holder.imageView);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Log.e("BFF", "checked" + users.get(position).getUserId());
                users.get(currentUserPosition).setInterestedIn(users.get(position).getUserId());
                saveChangedUser();

                CollectionReference docRef = firebaseFirestore.collection("users");

                docRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot collection = task.getResult();
                        if (collection != null) {
                            List<DocumentSnapshot> peopleDocuments = collection.getDocuments();

                            for (DocumentSnapshot document : peopleDocuments) {
                                if (document.getId().equals(users.get(currentUserPosition).getInterestedIn())
                                        && String.valueOf(document.getData().get("interestedIn")).equals(currentUserID)) {
                                    holder.instagram.setText(String.valueOf(document.getData().get("instagram")));
                                }
                            }
                        }
                    }
                });
                // interestedIn.add(users.get(position).getUserId());
                //users.get(position).setInterestedIn();
                // checkedContacts.add(contact);
            } else {
               // users.get(currentUserPosition).setInterestedIn(null);
                //saveChangedUser();
                // checkedContacts.remove(contact);

                Log.e("BFF", "unchecked");
            }
        });

        //changedUser = new HashMap<>();




        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder  {
        CheckBox checkBox;
        TextView name, description, score, age, instagram;
        ImageView imageView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.name_view);
            this.description = itemView.findViewById(R.id.description_view);
            this.score = itemView.findViewById(R.id.score_view);
            this.checkBox = itemView.findViewById(R.id.checkBox);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.age = itemView.findViewById(R.id.age_view);
            this.instagram = itemView.findViewById(R.id.instagram_view);

        }

        void bind(int position) {
        }

    }

    public void saveChangedUser() {
        changedUser = new HashMap<>();
        changedUser.put("name", users.get(currentUserPosition).getFullName());
        changedUser.put("email", users.get(currentUserPosition).getEmail());
        changedUser.put("city", users.get(currentUserPosition).getCity());
        changedUser.put("instagram", users.get(currentUserPosition).getInstagram());
        changedUser.put("age", users.get(currentUserPosition).getAge());
        changedUser.put("personality", users.get(currentUserPosition).getPersonality());
        changedUser.put("description", users.get(currentUserPosition).getDescription());
        changedUser.put("interestedIn", users.get(currentUserPosition).getInterestedIn());

        Log.e("BFF", " " + changedUser.size());

        Log.e("BFF", currentUserID);
        firebaseFirestore.collection("users").document(currentUserID)
                .set(changedUser)
                .addOnSuccessListener(aVoid -> Log.d("BFF", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("BFF", "Error writing document", e));
    }
}


