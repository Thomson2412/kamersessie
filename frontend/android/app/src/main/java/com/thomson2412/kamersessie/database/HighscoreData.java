package com.thomson2412.kamersessie.database;

/**
 * Created by tfink on 16-9-2015.
 */
public class HighscoreData {
    private String _username;
    private int _highscore;
    private String _date;

    public HighscoreData(String un, int hs, String d){
        _username = un;
        _highscore = hs;
        _date = d;
    }



    public void setUsername(String un){
        _username = un;
    }

    public void setHighscore(int hs){
        _highscore = hs;
    }

    public void setDate(String d){
        _date = d;
    }





    public String getUsername(){
        return  _username;
    }

    public int getHighscore(){
        return _highscore;
    }

    public String getDate(){
        return _date;
    }


    @Override
    public String toString(){
        return _username + " " + _highscore + " " + _date;
    }
}
