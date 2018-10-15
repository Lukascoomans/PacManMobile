package be.pxl.pacmanapp;

import android.support.annotation.NonNull;

public class ScoreListViewModel {
    private int playerId;
    private int position;
    private int points;

    @NonNull
    public int getPlayerId(){
        return  playerId;
    }

    public void setPlayerId(@NonNull final int playerId) {
        this.playerId = playerId;
    }

    @NonNull
    public int getPosition(){
        return position;
    }

    public void setPosition(@NonNull final int position) {
        this.position = position;
    }

    @NonNull
    public int getPoints(){
        return points;
    }

    public void setPoints(@NonNull final int points) {
        this.points = points;
    }
}
