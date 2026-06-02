package manager;

import characters.Player;

public class StoryManager {

    private int     currentStage  = 0;
    private String  currentText   = "";
    private int     displayIndex  = 0;  // huruf yang sudah ditampilkan
    private int     textTimer     = 0;
    private int     textSpeed     = 3;  // makin kecil makin cepat
    private boolean isDisplaying  = false;
    private boolean isDone        = false;

    // Narasi
    private static final String INTRO =
            "Kerajaan telah jatuh ke dalam kegelapan...\n" +
                    "Sang Daren, harapan terakhir umat manusia,\n" +
                    "melangkah masuk ke sarang iblis.\n" +
                    "Tidak ada jalan kembali...";

    private static final String[] TRANSITIONS = {
            "",
            "Makhluk rendahan...\nTapi yang lebih besar sudah menunggumu.",
            "Baju zirahmu tidak akan melindungimu\ndari jurang maut ini, Daren.",
            "Hahaha! Akhirnya kau tiba...\nBersiaplah menemui ajalmu!"
    };

    private static final String BOSS_DIALOG =
            "Kau berani datang ke hadapanku?\n" +
                    "Rasakan kekuatan kegelapan yang sesungguhnya!";

    // Mulai tampilkan teks
    public void showIntro() {
        startText(INTRO);
    }

    public void showTransitionStory(int stage) {
        this.currentStage = stage;
        if (stage >= 1 && stage < TRANSITIONS.length) {
            startText(TRANSITIONS[stage]);
        }
    }

    public void showBossDialog() {
        startText(BOSS_DIALOG);
    }

    public void showDevilPhase2() {
        startText(
                "HAHAHAHA! Kau mengira itu yang terkuat dariku?\n" +
                        "Rasakan kekuatan sesungguhnya sang Iblis!");
    }

    private void startText(String text) {
        this.currentText  = text;
        this.displayIndex = 0;
        this.textTimer    = 0;
        this.isDisplaying = true;
        this.isDone       = false;
    }

    //  Update typewriter effect
    public void update() {
        if (!isDisplaying || isDone) return;
        textTimer++;
        if (textTimer >= textSpeed) {
            textTimer = 0;
            if (displayIndex < currentText.length()) {
                displayIndex++;
            } else {
                isDone = true;
            }
        }
    }

    // Skip langsung ke akhir
    public void skipText() {
        displayIndex = currentText.length();
        isDone       = true;
    }

    // Teks yang ditampilkan sekarang
    public String getDisplayedText() {
        if (currentText.isEmpty()) return "";
        return currentText.substring(0, displayIndex);
    }

    // Getters
    public boolean isDisplaying() { return isDisplaying; }
    public boolean isDone()       { return isDone; }

    public void hide() {
        isDisplaying = false;
        currentText  = "";
        displayIndex = 0;
    }

    //  Ending
    public void showEnding(Player player) {
        String type = player.getPerformanceGrade();
        if (type.equals("TRUE_ENDING")) {
            startText(
                    "Kekuatan sejati sang Daren menghancurkan kegelapan tanpa sisa!\n" +
                            "Ia kembali ke kerajaan sebagai legenda hidup,\n" +
                            "tak tertandingi sepanjang masa.");
        } else {
            startText(
                    "Sang iblis telah tumbang.\n" +
                            "Daren memenangkan pertarungan, namun busurnya hancur\n" +
                            "dan tubuhnya penuh luka.\n" +
                            "Kerajaan aman, tapi sang pahlawan tak pernah terlihat lagi.");
        }
    }

    public void showGameOver() {
        startText(
                "Sang Daren jatuh bersimbah darah.\n" +
                        "Harapan terakhir telah padam,\n" +
                        "dan Kerajaan akan selamanya dikuasai\n" +
                        "oleh bayang-bayang iblis...");
    }

}