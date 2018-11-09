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

    public HighScoreModel() {

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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
