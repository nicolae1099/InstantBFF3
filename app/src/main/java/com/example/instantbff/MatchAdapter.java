package com.example.instantbff;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyHolder> {

    private List<User> users;

    public MatchAdapter(List<User> users) {
        this.users = users;
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
        holder.score.setText(String.valueOf(users.get(position).getScore()));

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder  {
        CheckBox checkBox;
        TextView name, description, score;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.name_view);
            this.description = itemView.findViewById(R.id.description_view);
            this.score = itemView.findViewById(R.id.score_view);
            checkBox = itemView.findViewById(R.id.checkBox);

        }

        void bind(int position) {
        }

    }
}
