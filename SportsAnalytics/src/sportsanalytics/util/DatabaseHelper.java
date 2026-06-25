package sportsanalytics.util;

import sportsanalytics.model.Athlete;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/sportsanalytics_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase(List<Athlete> defaultAthletes) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS athletes (" +
                "athlete_id INT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "sport VARCHAR(50) NOT NULL, " +
                "performance_rating DOUBLE PRECISION NOT NULL, " +
                "team_id INT NOT NULL, " +
                "match_scores INT[] NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create table
            stmt.execute(createTableSQL);

            // Check if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM athletes")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Populate default athletes
                    for (Athlete a : defaultAthletes) {
                        insertAthleteInternal(conn, a);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database Initialization Error: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static List<Athlete> loadAthletes() {
        List<Athlete> list = new ArrayList<>();
        String selectSQL = "SELECT athlete_id, name, sport, performance_rating, team_id, match_scores FROM athletes ORDER BY athlete_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                int id = rs.getInt("athlete_id");
                String name = rs.getString("name");
                String sport = rs.getString("sport");
                double rating = rs.getDouble("performance_rating");
                int teamId = rs.getInt("team_id");
                
                int[] matchScores = new int[0];
                Array sqlArray = rs.getArray("match_scores");
                if (sqlArray != null) {
                    Integer[] scoresArr = (Integer[]) sqlArray.getArray();
                    matchScores = Arrays.stream(scoresArr).mapToInt(Integer::intValue).toArray();
                }

                list.add(new Athlete(id, name, sport, rating, teamId, matchScores));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error loading athletes from database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    public static void insertAthlete(Athlete a) {
        try (Connection conn = getConnection()) {
            insertAthleteInternal(conn, a);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error inserting athlete into database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void insertAthleteInternal(Connection conn, Athlete a) throws SQLException {
        String insertSQL = "INSERT INTO athletes (athlete_id, name, sport, performance_rating, team_id, match_scores) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (athlete_id) DO UPDATE SET " +
                "name = EXCLUDED.name, " +
                "sport = EXCLUDED.sport, " +
                "performance_rating = EXCLUDED.performance_rating, " +
                "team_id = EXCLUDED.team_id, " +
                "match_scores = EXCLUDED.match_scores;";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, a.getAthleteId());
            pstmt.setString(2, a.getName());
            pstmt.setString(3, a.getSport());
            pstmt.setDouble(4, a.getPerformanceRating());
            pstmt.setInt(5, a.getTeamId());

            Integer[] scoresArr = Arrays.stream(a.getMatchScores()).boxed().toArray(Integer[]::new);
            Array sqlArray = conn.createArrayOf("integer", scoresArr);
            pstmt.setArray(6, sqlArray);

            pstmt.executeUpdate();
        }
    }

    public static void deleteAthlete(int id) {
        String deleteSQL = "DELETE FROM athletes WHERE athlete_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error deleting athlete from database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
