package com.example.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.data.Score;
import com.example.game.database.ScoreListHelper;

import java.util.Random;


public class Game extends AppCompatActivity {
    private View generalView;
    private TextView[][] arrayTextView;
    private String[][] arrayTextViewAux;
    private String previusScore;
    private TextView currentScore;
    private TextView maxScore;
    private boolean posibleToGoBack;
    private Context context;

    private long timeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.generalView = findViewById(R.id.mainLayout);
        addTouchListener();
        initializeArray();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        initializeViews(extras.getString("maxScore"));
        this.context = this.getApplicationContext();
        this.timeStart = System.currentTimeMillis();
        newGameTable();
    }

    private void newGameTable(){
        for (int i = 0; i < arrayTextView.length; i++) {
            for (int j = 0; j < arrayTextView[0].length; j++) {
                formatTile(i, j);
            }
        }

        putNewTileDebug(0,0,1024);
        putNewTileDebug(0,1,1024);
        backupArrayTextView();
        paintGameTable();
    }

    private void initializeViews(String maxScore) {
        this.currentScore = findViewById(R.id.currentScoreNumber);
        this.maxScore = findViewById(R.id.bestScoreNumber);
        this.maxScore.setText(maxScore);
    }

    /**
     * makes a backup of the actual table data in order to do the undoMovement.
     */
    private void backupArrayTextView(){
        for (int i = 0; i < this.arrayTextView.length; i++) {
            for (int j = 0; j < this.arrayTextView[i].length; j++) {
                this.arrayTextViewAux[i][j]=this.arrayTextView[i][j].getText().toString();
            }
        }
        this.posibleToGoBack=true;
        this.previusScore = this.currentScore.getText().toString();
    }

    /**
     * handles the onSwipe gestures of the game and decides what to do.
     */
    private void addTouchListener() {
        generalView.setOnTouchListener(new OnSwipeTouchListener(this.getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                makeMovementSwipe("left");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                makeMovementSwipe("right");
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                makeMovementSwipe("up");
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                makeMovementSwipe("down");
            }
        });
    }

    private void makeMovementSwipe(String direction){
        backupArrayTextView();
        move(direction);
        pairTiles(direction);
        if (checkIfTableEqualsPreviusMovement()){
            showMessage("tablero igual ");
        } else if (countOccupiedTiles()<16){
            putNewTile();
            if (countOccupiedTiles()==16){
                showMessage("tablero lleno");
                if (!checkIfLastMovementIsPossible()){
                    saveScore(false);
                }
            }
        }

        if (checkWin(2048)){
            showMessage("you win");
            saveScore(true);
        }
        paintGameTable();
    }

    private boolean checkIfTableEqualsPreviusMovement(){
        boolean equal=true;
        for (int i = 0; i < this.arrayTextView.length; i++) {
            for (int j = 0; j < this.arrayTextView[i].length; j++) {
                String textVisibleArray= this.arrayTextView[i][j].getText().toString();
                if (this.arrayTextViewAux[i][j].equals(textVisibleArray)==false){
                    equal=false;
                }
            }
        }
        return equal;
    }

    private void saveScore(Boolean win){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_save_score, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
        TextView textView = dialogView.findViewById(R.id.textViewSaveScore);
        Button save = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button cancel = (Button) dialogView.findViewById(R.id.buttonCancel);

        if (win){
            textView.setText("Congratulations you win!!!");
        }else{
            textView.setText("You lose. Try again!!!");
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                showMessage("save buttohn");
                Score score = new Score();
                score.setUser(editText.getText().toString());
                score.setScore(currentScore.getText().toString());
                score.setTime(Long.toString((System.currentTimeMillis()-timeStart)/1000));
                score.setMaxNumber(findMaxNumber());
                ScoreListHelper db = new ScoreListHelper(context);
                db.insertScore(score);
                dialogBuilder.dismiss();
                finish();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void initializeArray() {
        this.arrayTextView = new TextView[4][4];
        this.arrayTextViewAux = new String[4][4];

        this.arrayTextView[0][0] = findViewById((R.id.textView1));
        this.arrayTextView[0][1] = findViewById((R.id.textView2));
        this.arrayTextView[0][2] = findViewById((R.id.textView3));
        this.arrayTextView[0][3] = findViewById((R.id.textView4));

        this.arrayTextView[1][0] = findViewById((R.id.textView5));
        this.arrayTextView[1][1] = findViewById((R.id.textView6));
        this.arrayTextView[1][2] = findViewById((R.id.textView7));
        this.arrayTextView[1][3] = findViewById((R.id.textView8));

        this.arrayTextView[2][0] = findViewById((R.id.textView9));
        this.arrayTextView[2][1] = findViewById((R.id.textView10));
        this.arrayTextView[2][2] = findViewById((R.id.textView11));
        this.arrayTextView[2][3] = findViewById((R.id.textView12));

        this.arrayTextView[3][0] = findViewById((R.id.textView13));
        this.arrayTextView[3][1] = findViewById((R.id.textView14));
        this.arrayTextView[3][2] = findViewById((R.id.textView15));
        this.arrayTextView[3][3] = findViewById((R.id.textView16));
    }

    /**
     * puts a new tile into the game table
     */
    private void putNewTile() {
        Random random = new Random();
        boolean done = false;

        while (done == false) {
            int x = random.nextInt(4);
            int y = random.nextInt(4);
            int posibility = random.nextInt(10);
            if (this.arrayTextView[y][x].getText().equals("")) {
                this.arrayTextView[y][x].setBackgroundColor(getResources().getColor(R.color.number2));
                if (posibility > 6) {
                    this.arrayTextView[y][x].setText(Integer.toString(4));
                } else {
                    this.arrayTextView[y][x].setText(Integer.toString(2));
                }
                done = true;
            }
        }

    }

    private int countOccupiedTiles(){
        int tileCount=0;
        for (int i = 0; i < this.arrayTextView.length; i++) {
            for (int j = 0; j < this.arrayTextView[i].length; j++) {
                if (this.arrayTextView[i][j].getText().toString().equals("")==false){
                    tileCount++;
                }
            }
        }
        return tileCount;
    }

    private void putNewTileDebug(int x, int y, int number) {
        this.arrayTextView[y][x].setText(Integer.toString(number));
    }

    /**
     * move all the tiles to one direction
     * @param direction
     */
    private void move(String direction) {
        int length = arrayTextView.length;
        switch (direction) {
            case "left":
                for (int y = 0; y < length; y++) {
                    for (int x = 1; x < length; x++) {
                        System.out.println("left: " + y + " " + x);
                        if (this.arrayTextView[y][x].getText().equals("") == false) {
                            moveTile(direction,x,y);
                        }
                    }
                }
                break;
            case "right":
                System.out.println("right");
                for (int y = 0; y < length; y++) {
                    for (int x = length-1; x >-1; x--) {
                        System.out.println("right "+y+" "+x);
                       if (this.arrayTextView[y][x].getText().equals("")==false){
                           moveTile(direction,x,y);
                       }
                    }
                }
                break;
            case "down":
                System.out.println("down");
                for (int x = 0; x < length; x++) {
                    for (int y = length-1; y >-1; y--) {
                        System.out.println("down "+y+" "+x);
                        if (this.arrayTextView[y][x].getText().equals("")==false){
                            moveTile(direction,x,y);
                        }
                    }
                }
                break;
            case "up":
                System.out.println("up");
                for (int x = 0; x < length; x++) {
                    for (int y = 0; y <length; y++) {
                        System.out.println("down "+y+" "+x);
                        if (this.arrayTextView[y][x].getText().equals("")==false){
                            moveTile(direction,x,y);
                        }
                    }
                }
                break;
        }
        paintGameTable();
    }

    /**
     * checks if the movement is posible
     * @param direction direction of the movement
     * @param x the x coordinate of the tile
     * @param y the y coordinate o the tile
     * @return true if possible, false if not
     */
    private boolean checkMovement(String direction, int x, int y) {
        switch (direction) {
            case "left":
                if (this.arrayTextView[y][x - 1].getText().equals("")) {
                    return true;
                }else{
                    return false;
                }
            case "right":
                if (this.arrayTextView[y][x + 1].getText().equals("")) {
                    return true;
                }else{
                    return false;
                }
            case "down":
                if (this.arrayTextView[y+1][x].getText().equals("")){
                    return true;
                }else {
                    return false;
                }
            case "up":
                if (this.arrayTextView[y-1][x].getText().equals("")){
                    return true;
                }else {
                    return false;
                }
        }
        return false;
    }

    /**
     * swaps the tile for the one that's gonna be the correct place of the tile
     * @param direction direction of the movement
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    private void swapTile(String direction, int x, int y) {
        String aux = this.arrayTextView[y][x].getText().toString();
        switch (direction) {
            case "left":
                this.arrayTextView[y][x - 1].setText(aux);
                break;
            case "right":
                this.arrayTextView[y][x+1].setText(aux);
                break;
            case "down":
                this.arrayTextView[y+1][x].setText(aux);
                break;
            case "up":
                this.arrayTextView[y-1][x].setText(aux);
                break;
        }
        formatTile(x, y);
    }

    /**
     * moves the specified tile to the max posible possition
     * @param direction the direction of the movement
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    private void moveTile(String direction,int x,int y){
        boolean done=false;
        int auxX = x;
        int auxY = y;
        int lenght = this.arrayTextView.length;
        switch (direction){
            case "left":
                while(!done){
                    if (auxY>=0 && auxX>0){
                        if (checkMovement(direction,auxX,auxY)){
                            swapTile(direction,auxX,auxY);
                            auxX--;
                        }else{
                            done=true;
                        }
                    }else{
                        done=true;
                    }
                }
                break;
            case "right":
                while(!done){
                    if (auxY<lenght && auxX<lenght-1){
                        if (checkMovement(direction,auxX,auxY)){
                            swapTile(direction,auxX,auxY);
                            auxX++;
                        }else{
                            done=true;
                        }
                    }else{
                        done=true;
                    }
                }
                break;
            case "down":
                while(!done){
                    if (auxY<lenght-1 && auxX<lenght){
                        if (checkMovement(direction,auxX,auxY)){
                            swapTile(direction,auxX,auxY);
                            auxY++;
                        }else{
                            done=true;
                        }
                    }else{
                        done=true;
                    }
                }
                break;
            case "up":
                while(!done){
                    if (auxY>0 && auxX>-1){
                        if (checkMovement(direction,auxX,auxY)){
                            swapTile(direction,auxX,auxY);
                            auxY--;
                        }else{
                            done=true;
                        }
                    }else{
                        done=true;
                    }
                }
                break;
        }
    }

    /**
     * formats the specified tile
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     */
    private void formatTile(int x, int y) {
        this.arrayTextView[y][x].setText("");
        this.arrayTextView[y][x].setBackgroundColor(getResources().getColor(R.color.text_view_blue));
    }

    /**
     * paints the tiles with the correct color.
      */
    private void paintGameTable() {
        for (int i = 0; i < this.arrayTextView.length; i++) {
            for (int j = 0; j < this.arrayTextView.length; j++) {
                String aux = this.arrayTextView[i][j].getText().toString();
                if (aux.equals("")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.nullTile));
                } else if (aux.equals("2")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number2));
                } else if (aux.equals("4")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number4));
                } else if (aux.equals("8")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number8));
                } else if (aux.equals("16")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number16));
                } else if (aux.equals("32")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number32));
                } else if (aux.equals("64")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number64));
                } else if (aux.equals("128")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number128));
                } else if (aux.equals("256")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number256));
                } else if (aux.equals("512")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number512));
                } else if (aux.equals("1024")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number1024));
                } else if (aux.equals("2048")) {
                    this.arrayTextView[i][j].setBackgroundColor(getResources().getColor(R.color.number2048));
                }
            }
        }
    }

    private boolean checkTileEqual(String direction,int x, int y){
        String textAux= this.arrayTextView[y][x].getText().toString();
        boolean movement=false;
        switch (direction){
            case "left":
                String textAux2 = this.arrayTextView[y][x-1].getText().toString();
                if (textAux.equals("")==false){
                    if (textAux.equals(textAux2)){
                        movement= true;
                    }else{
                        movement=false;
                    }
                }
                break;
            case "right":
                    textAux2 = this.arrayTextView[y][x+1].getText().toString();
                    if (textAux.equals("")==false){
                        if (textAux.equals(textAux2)){
                            movement= true;
                        }else{
                            movement=false;
                        }
                    }
                break;
            case "up":
                textAux2 = this.arrayTextView[y-1][x].getText().toString();
                if (textAux.equals("")==false){
                    if (textAux.equals(textAux2)){
                        movement= true;
                    }else{
                        movement=false;
                    }
                }
                break;
            case "down":
                textAux2 = this.arrayTextView[y+1][x].getText().toString();
                if (textAux.equals("")==false){
                    if (textAux.equals(textAux2)){
                        movement= true;
                    }else{
                        movement=false;
                    }
                }
                break;
        }
        return movement;
    }

    private void pairTiles(String direction){
        int length = this.arrayTextView.length;
        switch (direction) {
            case "left":
                for (int y = 0; y < length; y++) {
                    for (int x = 1; x < length; x++) {
                        if (checkTileEqual(direction,x,y) == true) {
                            pairTile(direction,x,y);
                            move(direction);
                        }
                    }
                }
                break;
            case "right":
                System.out.println("right");
                for (int y = 0; y < length; y++) {
                    for (int x = length-2; x >-1; x--) {
                        System.out.println("right "+y+" "+x);
                        if (checkTileEqual(direction,x,y) == true) {
                            pairTile(direction,x,y);
                            move(direction);
                        }
                    }
                }
                break;
            case "down":
                System.out.println("down");
                for (int x = 0; x < length; x++) {
                    for (int y = length-2; y >-1; y--) {
                        System.out.println("down tile"+y+" "+x);
                        if (checkTileEqual(direction,x,y) == true) {
                            pairTile(direction,x,y);
                            move(direction);
                        }
                    }
                }
                break;
            case "up":
                System.out.println("up");
                for (int x = 0; x < length; x++) {
                    for (int y = 1; y <length; y++) {
                        System.out.println("pair tiles up "+y+" "+x);
                        if (checkTileEqual(direction,x,y) == true) {
                            pairTile(direction,x,y);
                            move(direction);
                        }
                    }
                }
                break;
        }
    }

    private void pairTile(String direction,int x, int y){
        //we calculate the new number and the new score
        int currentGameScore = Integer.parseInt(this.currentScore.getText().toString());
        int number = Integer.parseInt(this.arrayTextView[y][x].getText().toString());
        currentGameScore += 5*number;
        this.currentScore.setText(Integer.toString(currentGameScore));
        number = number*2;

        switch (direction){
            case "left":
                this.arrayTextView[y][x-1].setText(Integer.toString(number));
                formatTile(x,y);
                break;
            case "right":
                this.arrayTextView[y][x+1].setText(Integer.toString(number));
                formatTile(x,y);
                break;
            case "down":
                this.arrayTextView[y+1][x].setText(Integer.toString(number));
                formatTile(x,y);
                break;
            case "up":
                this.arrayTextView[y-1][x].setText(Integer.toString(number));
                formatTile(x,y);
                break;
        }
    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void backButton(View view){
        if (posibleToGoBack){
            for (int i = 0; i < this.arrayTextViewAux.length; i++) {
                for (int j = 0; j < this.arrayTextViewAux[i].length; j++) {
                    this.arrayTextView[i][j].setText(this.arrayTextViewAux[i][j]);
                }
            }
            this.currentScore.setText(this.previusScore);
            posibleToGoBack=false;
        }else{
            showMessage("you have to move again in order to go back");
        }
        paintGameTable();
    }

    public void newGameButtonListener(View view){
        newGameTable();
        this.currentScore.setText("0");
    }

    /**
     * once the table board is full of tiles this method check if the user can do any movement.
     * @return true if the movement is posible, false if not.
     */
    public boolean checkIfLastMovementIsPossible(){
        boolean possible =false;
        int y=0;
        int x=0;
        int maxY= this.arrayTextView.length;
        int maxX= this.arrayTextView[0].length;
        while (!possible && y<maxY){
            while (!possible && x<maxX){
                if (x+1<maxX){
                    if (checkTileEqual("right",x,y)){
                        possible=true;
                    }
                }
                if (y+1<maxY){
                    if (checkTileEqual("down",x,y)){
                        possible=true;
                    }
                }
                x++;
            }
            y++;
            x=0;
        }
        return possible;
    }

    private boolean checkWin(int number){
        boolean win =false;
        int y=0;
        int x=0;
        int maxY= this.arrayTextView.length;
        int maxX= this.arrayTextView[0].length;
        String winNumber = Integer.toString(number);
        while (!win && y<maxY){
            while (!win && x<maxX){
                if (this.arrayTextView[y][x].getText().toString().equals(winNumber)){
                    win=true;
                }
                x++;
            }
            y++;
            x=0;
        }
        return win;
    }

    private Integer findMaxNumber(){
        int maxNumber=0;

        for (int i = 0; i < arrayTextView.length; i++) {
            for (int j = 0; j < arrayTextView[i].length; j++) {
                if (this.arrayTextView[i][j].getText().toString().equals("")==false){
                    if (Integer.parseInt(this.arrayTextView[i][j].getText().toString())>maxNumber){
                        maxNumber= Integer.parseInt(this.arrayTextView[i][j].getText().toString());
                    }
                }
            }
        }

        return maxNumber;
    }
}