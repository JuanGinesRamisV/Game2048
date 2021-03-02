package com.example.game.data;

public class Score {
    /**
     * bean that holds the data of the score
     */
    private String id;
    private String user;
    private String Score;
    private String time;
    private int MaxNumber;

    public Score() {
    }

    //<editor-fold desc="getters and setters">
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMaxNumber() {
        return MaxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        MaxNumber = maxNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //</editor-fold>
}
