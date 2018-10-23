package be.pxl.pacmanapp;

public class Score {
    private int position;
    private int score;

    public Score(int position, int score) {
        this.position = position;
        this.score = score;
    }

    public int getPosition() {
        return position;
    }

    public int getScore() {
        return score;
    }
}
