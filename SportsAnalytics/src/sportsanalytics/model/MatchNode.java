package sportsanalytics.model;

public class MatchNode {
    private int matchId;
    private String teamAName;
    private String teamBName;
    private int scoreA;
    private int scoreB;
    private String roundName;

    public MatchNode(int matchId, String teamAName, String teamBName, int scoreA, int scoreB, String roundName) {
        this.matchId = matchId;
        this.teamAName = teamAName;
        this.teamBName = teamBName;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.roundName = roundName;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public String getTeamAName() {
        return teamAName;
    }

    public void setTeamAName(String teamAName) {
        this.teamAName = teamAName;
    }

    public String getTeamBName() {
        return teamBName;
    }

    public void setTeamBName(String teamBName) {
        this.teamBName = teamBName;
    }

    public int getScoreA() {
        return scoreA;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    @Override
    public String toString() {
        return roundName + ": " + teamAName + " vs " + teamBName + " (" + scoreA + "-" + scoreB + ")";
    }
}
