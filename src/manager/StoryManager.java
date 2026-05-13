package manager;

import characters.Player;

public class StoryManager {

    private int currentStage;

    public StoryManager() {
        this.currentStage = 0;
    }

    public void showIntro() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("\"Kerajaan telah jatuh ke dalam kegelapan.");
        System.out.println(" Sang Archer, harapan terakhir umat manusia,");
        System.out.println(" melangkah masuk ke sarang iblis.");
        System.out.println(" Tidak ada jalan kembali...\"");
        System.out.println("=".repeat(60));
    }

    public void showTransitionStory(int stage) {
        this.currentStage = stage;
        System.out.println("\n" + "-".repeat(60));
        switch (stage) {
            case 1:
                System.out.println("\"Makhluk rendahan... Tapi yang lebih besar sudah menunggumu.\"");
                break;
            case 2:
                System.out.println("\"Baju zirahmu tidak akan melindungimu dari jurang maut ini, Archer.\"");
                break;
            case 3:
                System.out.println("\"Hahaha! Akhirnya kau tiba... Bersiaplah menemui ajalmu!\"");
                break;
        }
        System.out.println("-".repeat(60));
    }

    public void showBossDialog() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("\"Hahaha! Kau berani datang ke hadapanku?\"");
        System.out.println("\"Rasakan kekuatan kegelapan yang sesungguhnya!\"");
        System.out.println("=".repeat(60));
    }

    public void showEnding(Player player) {
        String type = player.getPerformanceGrade();
        System.out.println("\n" + "=".repeat(60));
        if (type.equals("TRUE_ENDING")) {
            System.out.println("TRUE ENDING:");
            System.out.println("\"Kekuatan sejati sang Archer menghancurkan kegelapan tanpa sisa!");
            System.out.println(" Ia kembali ke kerajaan sebagai legenda hidup,");
            System.out.println(" tak tertandingi sepanjang masa.\"");
        } else {
            System.out.println("NORMAL ENDING:");
            System.out.println("\"Sang iblis telah tumbang. Archer memenangkan pertarungan,");
            System.out.println(" namun busurnya hancur dan tubuhnya penuh luka.");
            System.out.println(" Kerajaan aman, tapi sang pahlawan tak pernah terlihat lagi.\"");
        }
        System.out.println("=".repeat(60));
        System.out.println("Final Score: " + player.getScore());
    }

    public void showGameOver() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BAD ENDING:");
        System.out.println("\"Sang Archer jatuh bersimbah darah.");
        System.out.println(" Harapan terakhir telah padam,");
        System.out.println(" dan Kerajaan akan selamanya dikuasai oleh bayang-bayang iblis...\"");
        System.out.println("=".repeat(60));
    }
}
