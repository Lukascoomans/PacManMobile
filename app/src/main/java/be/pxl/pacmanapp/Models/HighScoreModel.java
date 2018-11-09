package be.pxl.pacmanapp.Models;

public class HighScoreModel {

    private String id;
    private String name;
    private String score;
    private String country;

    public HighScoreModel(String id, String name, String score, String country) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getCountry() {
        return country;
    }
}
