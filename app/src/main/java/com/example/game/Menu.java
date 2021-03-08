package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.database.ScoreListHelper;


public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, Game.class);
        ScoreListHelper db = new ScoreListHelper(this);
        String maxScore = db.getMaxScore();
        if (maxScore==null){
            maxScore="0";
        }
        intent.putExtra("maxScore", maxScore);
        startActivity(intent);
    }

    public void manageScores(View view){
        startActivity(new Intent(this, ManageScores.class));
    }

}