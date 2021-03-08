package com.example.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.data.Score;
import com.example.game.database.ScoreListHelper;
import com.example.game.recyclerView.AdapterScore;

import java.util.ArrayList;
import java.util.Collections;

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
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_scores);
        initialiizeViews();
        mDB = new ScoreListHelper(this);
        this.context=this;
        System.out.println("load scores");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewScores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapterScore = new AdapterScore(mDB, this);
        recyclerView.setAdapter(adapterScore);
        Toast.makeText(this, Long.toString(mDB.count()), Toast.LENGTH_SHORT).show();

        ItemTouchHelper ti =new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT|ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(adapterScore.getScores(),from,to);
                adapterScore.notifyItemMoved(from,to);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure that you want to delete this score?");
                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Score score = adapterScore.getScores().get(viewHolder.getAdapterPosition());
                                Toast.makeText(context,score.getUser(),Toast.LENGTH_SHORT).show();
                                mDB.delete(Integer.parseInt(score.getId()));
                                adapterScore.setScores(mDB.queryAll());
                                adapterScore.notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("NO,take me back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterScore.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        ti.attachToRecyclerView(recyclerView);
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
                this.
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

    /**
     * creates an alert dialg to search by user. And search with the given string.
     */
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

    /**
     * creates an alert dialog that allows the user to search comparing if the search parameter is
     * smaller, equal or bigger.
     * @param searchBy the parameter to searchby.
     */
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
            adapterScore.setScores(mDB.queryAllOrderByUser());
            adapterScore.notifyDataSetChanged();
        }else if (adapterView.getItemAtPosition(i).toString().equals("Score")){
            this.orderby= ScoreListHelper.KEY_SCORE;
            adapterScore.setScores(mDB.queryAllOrderByScore());
            adapterScore.notifyDataSetChanged();
        }else if (adapterView.getItemAtPosition(i).toString().equals("Time")){
            this.orderby= ScoreListHelper.KEY_TIME;
            adapterScore.setScores(mDB.queryAllOrderByTime());
            adapterScore.notifyDataSetChanged();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}