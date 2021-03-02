package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.data.Score;
import com.example.game.database.ScoreListHelper;
import com.example.game.recyclerView.AdapterScore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ManageScores extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final int SCORE_EDIT = 1;

    private ArrayList<Score> scores = new ArrayList();
    private RecyclerView recyclerView;
    private AdapterScore adapterScore;
    private ScoreListHelper mDB;
    private String orderby = ScoreListHelper.KEY_USER;

    private Button userSearchButton;
    private Button timeSearchButton;
    private Button scoreSearchButton;
    private Spinner spinnerOrderBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_scores);
        initialiizeViews();
        mDB = new ScoreListHelper(this);
        System.out.println("load scores");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewScores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapterScore = new AdapterScore(mDB, this);
        recyclerView.setAdapter(adapterScore);
        Toast.makeText(this, Long.toString(mDB.count()), Toast.LENGTH_SHORT).show();
    }

    private void initialiizeViews() {
        this.userSearchButton = findViewById(R.id.searchByUserButton);
        this.timeSearchButton = findViewById(R.id.searchByTimeButton);
        this.scoreSearchButton = findViewById(R.id.searchByScoreButton);
        this.spinnerOrderBy = findViewById(R.id.spinnerManageScores);

        this.userSearchButton.setOnClickListener(this);
        this.timeSearchButton.setOnClickListener(this);
        this.scoreSearchButton.setOnClickListener(this);
        this.spinnerOrderBy.setOnItemSelectedListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCORE_EDIT) {
            this.adapterScore.updateData();
            this.adapterScore.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.searchByUserButton:
                Toast.makeText(this, "serchByUser", Toast.LENGTH_SHORT).show();
                createAlertDialogSearchByUser();
                break;
            case R.id.searchByTimeButton:
                Toast.makeText(this, "timeButton", Toast.LENGTH_SHORT).show();
                createSearchAlertDialog("Time");
                break;
            case R.id.searchByScoreButton:
                Toast.makeText(this, "serchByUser", Toast.LENGTH_SHORT).show();
                createSearchAlertDialog("Score");
                break;
        }
    }

    private void createAlertDialogSearchByUser() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_search_user, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.searchUserEditText);
        Button searchButton = (Button) dialogView.findViewById(R.id.buttonSearchUser);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                ArrayList<Score> scores = new ArrayList<>();
                scores = mDB.searchByUser(editText.getText().toString(),orderby);
                adapterScore.setScores(scores);
                adapterScore.notifyDataSetChanged();
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void createSearchAlertDialog(String searchBy) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_search_by_bigger_smaller_equal, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.searchUserEditText);
        editText.setHint(searchBy);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewSearchBy);
        textView.setText("Search by "+searchBy);
        Button smallerButton = (Button) dialogView.findViewById(R.id.searchBySmallerButton);
        Button equalButton = (Button) dialogView.findViewById(R.id.searchByEqualButton);
        Button biggerButton = (Button) dialogView.findViewById(R.id.searchByBiggerButton);

        smallerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                ArrayList<Score> scores = new ArrayList<>();
                if (searchBy.equals("Score")){
                    scores = mDB.searchByScore(editText.getText().toString(),ScoreListHelper.SMALLER,orderby);
                }else if (searchBy.equals("Time")){
                    scores = mDB.searchByTime(editText.getText().toString(),ScoreListHelper.SMALLER,orderby);
                }
                adapterScore.setScores(scores);
                adapterScore.notifyDataSetChanged();
                dialogBuilder.dismiss();
            }
        });

        equalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Score> scores = new ArrayList<>();
                if (searchBy.equals("Score")){
                    scores = mDB.searchByScore(editText.getText().toString(),ScoreListHelper.EQUAL,orderby);
                }else if (searchBy.equals("Time")){
                    scores = mDB.searchByTime(editText.getText().toString(),ScoreListHelper.EQUAL,orderby);
                }
                adapterScore.setScores(scores);
                adapterScore.notifyDataSetChanged();
                dialogBuilder.dismiss();
            }
        });

        biggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Score> scores = new ArrayList<>();
                if (searchBy.equals("Score")){
                    scores = mDB.searchByScore(editText.getText().toString(),ScoreListHelper.BIGGER,orderby);
                }else if (searchBy.equals("Time")){
                    scores = mDB.searchByTime(editText.getText().toString(),ScoreListHelper.BIGGER,orderby);
                }
                adapterScore.setScores(scores);
                adapterScore.notifyDataSetChanged();
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getItemAtPosition(i).toString().equals("User")){
            this.orderby= ScoreListHelper.KEY_USER;
        }else if (adapterView.getItemAtPosition(i).toString().equals("Score")){
            this.orderby= ScoreListHelper.KEY_SCORE;
        }else if (adapterView.getItemAtPosition(i).toString().equals("Time")){
            this.orderby= ScoreListHelper.KEY_TIME;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}