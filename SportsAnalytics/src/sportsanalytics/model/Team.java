package sportsanalytics.model;

public class Team {
    private int teamId;
    private String name;
    private String headCoach;
    private double points;

    public Team(int teamId, String name, String headCoach, double points) {
        this.teamId = teamId;
        this.name = name;
        this.headCoach = headCoach;
        this.points = points;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadCoach() {
        return headCoach;
    }

    public void setHeadCoach(String headCoach) {
        this.headCoach = headCoach;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return name + " (ID: " + teamId + ")";
    }
}
