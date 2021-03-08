package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.game.data.Score;
import com.example.game.database.ScoreListHelper;

public class EditScore extends AppCompatActivity implements View.OnClickListener{
    //for the getExtras
    final String ID_SCORE="ID_SCORE";
    final String ID_USER="ID_USER";
    final String ID_SCORE_PUNTUATION="ID_SCORE_PUNTUATION";
    final String ID_TIME="ID_TIME";
    final String ID_MAX_NUMBER="ID_MAX_NUMBER";

    private EditText editTextuser;
    private EditText editTextScore;
    private EditText editTextTime;
    private EditText editTextMaxNumber;

    private Button cancelButton;
    private Button saveButton;

    private Score score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_score);

        initializeViews();
        addClickListeners();
        Intent intent = getIntent();
        loadData(intent);
    }

    private void initializeViews(){
        this.editTextuser = findViewById(R.id.editScoreUserEditText);
        this.editTextScore = findViewById(R.id.edtiScoreScoreEditText);
        this.editTextTime = findViewById(R.id.editScoreTime);
        this.editTextMaxNumber = findViewById(R.id.editMaxNumber);

        this.cancelButton = findViewById(R.id.editScoreCancelButton);
        this.saveButton= findViewById(R.id.editScoreSaveButton);

        this.score = new Score();
    }

    private void addClickListeners(){
        this.cancelButton.setOnClickListener(this);
        this.saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editScoreCancelButton:
                break;
            case R.id.editScoreSaveButton:
                this.score.setUser(editTextuser.getText().toString());
                this.score.setScore(editTextScore.getText().toString());
                this.score.setTime(editTextTime.getText().toString());
                this.score.setMaxNumber(Integer.parseInt(editTextMaxNumber.getText().toString()));
                ScoreListHelper db = new ScoreListHelper(this);
                db.updateScore(this.score);
                System.out.println("updated");
                break;
        }
        finish();
    }

    /**
     * loads up the data on the views
     * @param intent intent to get the data
     */
    private void loadData(Intent intent){
        Bundle extras = intent.getExtras();
        String user = extras.getString(ID_USER);
        String score = extras.getString(ID_SCORE_PUNTUATION);
        String time = extras.getString(ID_TIME);
        String maxNumber = extras.getString(ID_MAX_NUMBER);

        user = user.replaceFirst("User: " ,"");
        score = score.replaceFirst("Score: " ,"");
        time = time.replaceFirst("Time: " ,"");
        maxNumber = maxNumber.replaceFirst("Max Number: " ,"");

        editTextuser.setText(user);
        editTextScore.setText(score);
        editTextTime.setText(time);
        editTextMaxNumber.setText(maxNumber);

        this.score.setId(extras.getString(ID_SCORE));
        this.score.setUser(user);
        this.score.setScore(score);
        this.score.setTime(time);
        this.score.setMaxNumber(Integer.parseInt(maxNumber));
        System.out.println();
    }
}