package manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {

    // Inner class untuk entry leaderboard
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        private String name;
        private int    score;

        public ScoreEntry(String name, int score) {
            this.name  = name;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // descending
        }

        public String getName()  { return name;  }
        public int    getScore() { return score; }

        @Override
        public String toString() {
            return name + " : " + score;
        }
    }

    // Atribut
    private List<ScoreEntry> leaderboard;
    private static final int MAX_ENTRIES = 10;

    // Constructor
    public ScoreManager() {
        this.leaderboard = new ArrayList<>();
    }

    // Simpan skor
    public void saveScore(String name, int score) {
        leaderboard.add(new ScoreEntry(name, score));
        Collections.sort(leaderboard);
        if (leaderboard.size() > MAX_ENTRIES) {
            leaderboard = leaderboard.subList(0, MAX_ENTRIES);
        }
        System.out.println("Skor disimpan! " + name + ": " + score);
    }

    // Tampilkan leaderboard
    public void showHighScore() {
        System.out.println("\n=== HIGHSCORE ===");
        if (leaderboard.isEmpty()) {
            System.out.println("Belum ada skor tersimpan.");
            return;
        }
        for (int i = 0; i < leaderboard.size(); i++) {
            System.out.println((i+1) + ". " + leaderboard.get(i));
        }
        System.out.println("=================");
    }

    //  Getter
    public List<ScoreEntry> getLeaderboard() { return leaderboard; }
}
