package com.example.game.recyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.EditScore;
import com.example.game.ManageScores;
import com.example.game.R;
import com.example.game.data.Score;
import com.example.game.database.ScoreListHelper;

import java.util.ArrayList;

public class AdapterScore extends RecyclerView.Adapter<AdapterScore.ViewHolderScore> {
    private ScoreListHelper mDB;
    private Context context;
    ArrayList<Score> scores;

    public AdapterScore(ScoreListHelper mDB,Context context) {
        this.context = context;
        this.mDB = mDB;
        this.scores= mDB.queryAll();
    }

    public void setScores(ArrayList<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolderScore onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item,null,false);
        return new ViewHolderScore(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderScore holder, int position) {
        holder.loadData(scores.get(position));
        holder.addClickListeners();
    }

    @Override
    public int getItemCount() {
        return this.scores.size();
    }

    /**
     * updates the data of the scores array from data from the database.
     */
    public void updateData(){
        this.scores = mDB.queryAll();
    }

    public class ViewHolderScore extends RecyclerView.ViewHolder {
        private TextView userScore;
        private TextView score;
        private TextView time;
        private TextView maxNumber;
        private Button editButton;
        private Button deleteButton;
        private String id;

        public ViewHolderScore(@NonNull View itemView) {
            super(itemView);
            //we initialize the different views.
            userScore = itemView.findViewById(R.id.userText);
            score = itemView.findViewById(R.id.scoreTextCardView);
            time = itemView.findViewById(R.id.scoreTime);
            maxNumber = itemView.findViewById(R.id.maxNumber);
            editButton = itemView.findViewById(R.id.editScoreButton);
            deleteButton = itemView.findViewById(R.id.deleteScoreButton);
        }

        /**
         * loads the data on the given cardview
         * @param score
         */
        public void loadData(Score score) {
            this.userScore.setText("User: "+ score.getUser());
            this.score.setText("Score: " + score.getScore());
            this.time.setText("Time: " + score.getTime());
            this.maxNumber.setText("Max Number: " + score.getMaxNumber());
            this.id = score.getId();
        }

        /**
         * adds click listeners to the buttons of each item.
         */
        public void addClickListeners(){
            final String ID_SCORE="ID_SCORE";
            final String ID_USER="ID_USER";
            final String ID_SCORE_PUNTUATION="ID_SCORE_PUNTUATION";
            final String ID_TIME="ID_TIME";
            final String ID_MAX_NUMBER="ID_MAX_NUMBER";

            this.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure that you want to delete this score?");
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("edit button clicked" + id);
                                    Toast.makeText(context,id,Toast.LENGTH_SHORT).show();
                                    mDB.delete(Integer.parseInt(id));
                                    scores = mDB.queryAll();
                                    notifyDataSetChanged();
                                }
                            });
                    builder.setNegativeButton("NO,take me back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            this.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditScore.class);
                    intent.putExtra(ID_SCORE,id);
                    intent.putExtra(ID_USER, userScore.getText());
                    intent.putExtra(ID_SCORE_PUNTUATION, score.getText());
                    intent.putExtra(ID_TIME,time.getText());
                    intent.putExtra(ID_MAX_NUMBER,maxNumber.getText());
                    ((Activity) context).startActivityForResult(
                            intent, ManageScores.SCORE_EDIT);
                }
            });
        }
    }
}