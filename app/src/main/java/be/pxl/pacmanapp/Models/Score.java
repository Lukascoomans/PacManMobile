package be.pxl.pacmanapp.Models;

public class Score {
    private String position;
    private String points;
    private String country;
    private String name;


    public Score(String position, String points, String country, String name) {
        this.position = position;
        this.points = points;
        this.country = country;
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
