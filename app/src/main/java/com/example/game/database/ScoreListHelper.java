package com.example.game.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.game.data.Score;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class ScoreListHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String SCORE_TABLE = "scoreTable";
    private static final String DATABASE_NAME = "wordlist";

    //order by atributes
    public static final int SMALLER = 6;
    public static final int EQUAL = 7;
    public static final int BIGGER = 8;

    //columns keys
    public static final String KEY_ID= "_id";
    public static final String KEY_USER= "user";
    public static final String KEY_SCORE= "score";
    public static final String KEY_TIME= "time";
    public static final String KEY_MAX_NUMBER= "max_number";

    //columns array
    private static final String[] COLUMNS= {KEY_ID,KEY_USER,KEY_SCORE, KEY_TIME,KEY_MAX_NUMBER};

    private static final String WORD_LIST_TABLE_CREATE = "CREATE TABLE "+ SCORE_TABLE + "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_USER+ " TEXT,"+
            KEY_SCORE+ " INTEGER,"+
            KEY_TIME+ " INTEGER,"+
            KEY_MAX_NUMBER+ " TEXT );";
    //database objects
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public ScoreListHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        //fillDatabaseWithData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(ScoreListHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     * Return the number of entries on the table
     * @return the number of entries on the table
     */
    public long count(){
        if (mReadableDB==null){
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, SCORE_TABLE);
    }

    /**
     * Inserts a new score on the database table
     * @param score score to insert
     */
    public void insertScore(Score score){
        if (this.mWritableDB==null){
            this.mWritableDB = getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER, score.getUser());
        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_TIME, score.getTime());
        values.put(KEY_MAX_NUMBER,score.getMaxNumber());
        this.mWritableDB.insert(SCORE_TABLE, null, values);
    }

    /**
     * Get all the scores of the database
     * @return arrayList with all the scores on the database
     */
    public ArrayList<Score> queryAll(){
        ArrayList<Score> scores = new ArrayList<>();
        String query = "SELECT * FROM " + SCORE_TABLE;

        Cursor cursor= null;
        if (mReadableDB==null){
            mReadableDB = getReadableDatabase();
        }
        cursor = mReadableDB.rawQuery(query,null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Score score = new Score();
                score.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                score.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                score.setScore(cursor.getString(cursor.getColumnIndex(KEY_SCORE)));
                score.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                score.setMaxNumber(cursor.getInt(cursor.getColumnIndex(KEY_MAX_NUMBER)));
                scores.add(score);
                cursor.moveToNext();
            }
        }

        return scores;
    }

    /**
     * deletes the score with the given id
     * @param id id of the score to delete
     * @return rows deleted
     */
    public int delete(int id){
        int deleted = 0;
        try{
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(SCORE_TABLE,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});

        }catch (Exception e){
            Log.d("delete",e.getMessage());
        }
        return deleted;
    }

    /**
     * Updates the score with the new information.
      * @param score the score object with new information.
     */
    public void updateScore(Score score){
        SQLiteDatabase db = this.getWritableDatabase();

        String updateScoreByID = "UPDATE " + SCORE_TABLE
                + " SET " + KEY_USER + " = '" + score.getUser() + "' , " +
                KEY_SCORE + " = " + score.getScore() + ", " +
                KEY_TIME + " = '" + score.getTime() + "', " +
                KEY_MAX_NUMBER + " = " + Integer.toString(score.getMaxNumber()) +
                " WHERE " + KEY_ID + " = " + score.getId();

        db.execSQL(updateScoreByID);
    }

    /**
     * gets the max score of all the entries.
     * @return max score of the database.
     */
    public String getMaxScore(){
        if (this.mReadableDB==null){
            this.mReadableDB = getReadableDatabase();
        }

        String maxScore="";

        String topScoreQuery = "SELECT MAX(" + KEY_SCORE + ") topScore FROM "+ SCORE_TABLE;
        Cursor cursor = mReadableDB.rawQuery(topScoreQuery,null);
        cursor.moveToFirst();
        maxScore = cursor.getString(0);
        System.out.println(maxScore);
        return maxScore;
    }

    /**
     * search by user
     * @param word string that contains the user to search
     * @return all the entries of that user
     */
    public ArrayList<Score> searchByUser(String word, String orderBy) {
        ArrayList<Score> scores = new ArrayList<>();
        String[] columns = this.COLUMNS;
        String searchString =  "%" + word + "%";
        String where = KEY_USER + " LIKE ? ";
        String[] whereArgs = new String[]{searchString};

        Cursor cursor= null;

        try{
            if (mReadableDB==null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(SCORE_TABLE,columns,where,whereArgs,null,null,orderBy);

            if (cursor != null & cursor.getCount() > 0) {
// You must move the cursor to the first item.
                cursor.moveToFirst();
                int index;
                String result;
                do {
                    Score score = new Score();
                    score.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                    score.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                    score.setScore(cursor.getString(cursor.getColumnIndex(KEY_SCORE)));
                    score.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                    score.setMaxNumber(cursor.getInt(cursor.getColumnIndex(KEY_MAX_NUMBER)));
                    scores.add(score);
                } while (cursor.moveToNext());
                cursor.close();
            }else if (cursor.getCount()==0){
            }

            System.out.println("debug");
        }catch (Exception e){
            Log.d("TAble","search error");
        }
        return scores;
    }

    /**
     * search by score on the database.
     * @param score the score number.
     * @param filter if you want to search smaller, equal or bigger that the given score.
     * @param orderBy the item which you want to order by the scores.
     * @return array with the scores require the conditions.
     */
    public ArrayList<Score> searchByScore(String score, int filter, String orderBy) {
        ArrayList<Score> scores = new ArrayList<>();
        String[] columns = this.COLUMNS;
        String searchString =  score;
        String where = KEY_SCORE + " < ? ";
        if (filter == SMALLER){
            where = KEY_SCORE + " < ? ";
        }else if (filter == BIGGER){
            where = KEY_SCORE + " > ? ";
        }else if (filter == EQUAL){
            where = KEY_SCORE + " = ? ";
        }
        String[] whereArgs = new String[]{searchString};

        Cursor cursor= null;

        try{
            if (mReadableDB==null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(SCORE_TABLE,columns,where,whereArgs,null,null,orderBy);

            if (cursor != null & cursor.getCount() > 0) {
// You must move the cursor to the first item.
                cursor.moveToFirst();
                int index;
                String result;
                do {
                    Score scoreAux = new Score();
                    scoreAux.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                    scoreAux.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                    scoreAux.setScore(cursor.getString(cursor.getColumnIndex(KEY_SCORE)));
                    scoreAux.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                    scoreAux.setMaxNumber(cursor.getInt(cursor.getColumnIndex(KEY_MAX_NUMBER)));
                    scores.add(scoreAux);
                } while (cursor.moveToNext());
                cursor.close();
            }else if (cursor.getCount()==0){
                System.out.println("cursor vacio");
            }

            System.out.println("debug");
        }catch (Exception e){
            Log.d("TAble","search error");
        }
        return scores;
    }

    /**
     * Search by time on the database
     * @param time the time number
     * @param filter if you want to search smaller, equal or bigger that the given time.
     * @param orderBy the item which you want to order by the scores.
     * @return array with the scores require the conditions.
     */
    public ArrayList<Score> searchByTime(String time, int filter, String orderBy) {
        ArrayList<Score> scores = new ArrayList<>();
        String[] columns = this.COLUMNS;
        String searchString =  time;
        String where = KEY_TIME + " < ? ";
        if (filter == SMALLER){
            where = KEY_TIME + " < ? ";
        }else if (filter == BIGGER){
            where = KEY_TIME + " > ? ";
        }else if (filter == EQUAL){
            where = KEY_TIME + " = ? ";
        }
        String[] whereArgs = new String[]{searchString};

        Cursor cursor= null;

        try{
            if (mReadableDB==null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(SCORE_TABLE,columns,where,whereArgs,null,null,orderBy);

            if (cursor != null & cursor.getCount() > 0) {
// You must move the cursor to the first item.
                cursor.moveToFirst();
                int index;
                String result;
                do {
                    Score scoreAux = new Score();
                    scoreAux.setId(Integer.toString(cursor.getInt(cursor.getColumnIndex(KEY_ID))));
                    scoreAux.setUser(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                    scoreAux.setScore(cursor.getString(cursor.getColumnIndex(KEY_SCORE)));
                    scoreAux.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                    scoreAux.setMaxNumber(cursor.getInt(cursor.getColumnIndex(KEY_MAX_NUMBER)));
                    scores.add(scoreAux);
                } while (cursor.moveToNext());
                cursor.close();
            }else if (cursor.getCount()==0){
                System.out.println("cursor vacio");
            }

            System.out.println("debug");
        }catch (Exception e){
            Log.d("TAble","search error");
        }
        return scores;
    }
}
