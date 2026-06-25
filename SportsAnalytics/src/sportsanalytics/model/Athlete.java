package sportsanalytics.model;

import java.util.Arrays;

public class Athlete {
    private int athleteId;
    private String name;
    private String sport;
    private double performanceRating;
    private int teamId;
    private int[] matchScores;

    public Athlete(int athleteId, String name, String sport, double performanceRating, int teamId, int[] matchScores) {
        this.athleteId = athleteId;
        this.name = name;
        this.sport = sport;
        this.performanceRating = performanceRating;
        this.teamId = teamId;
        this.matchScores = matchScores != null ? matchScores : new int[0];
    }

    // Getters and Setters
    public int getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(int athleteId) {
        this.athleteId = athleteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public double getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(double performanceRating) {
        this.performanceRating = performanceRating;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int[] getMatchScores() {
        return matchScores;
    }

    public void setMatchScores(int[] matchScores) {
        this.matchScores = matchScores != null ? matchScores : new int[0];
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "athleteId=" + athleteId +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                ", performanceRating=" + performanceRating +
                ", teamId=" + teamId +
                ", matchScores=" + Arrays.toString(matchScores) +
                '}';
    }
}
