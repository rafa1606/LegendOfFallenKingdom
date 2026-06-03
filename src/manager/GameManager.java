package manager;

import characters.Player;
import enemies.*;

public class GameManager {

    // Atribut
    private Player        player;
    private Enemy         currentEnemy;
    private BattleManager battleManager;
    private StoryManager  storyManager;
    private ScoreManager  scoreManager;
    private int           stage;        // 1=Goblin, 2=Orc, 3=DarkKnight, 4=Devil
    private boolean       isGameOver;

    // Constructor
    public GameManager() {
        this.player        = new Player("Daren");
        this.battleManager = new BattleManager(player);
        this.storyManager  = new StoryManager();
        this.scoreManager  = new ScoreManager();
        this.stage         = 1;
        this.isGameOver    = false;
    }

    // Start game
    public void startGame() {
        System.out.println("=== LEGEND OF THE FALLEN KINGDOM ===");
        storyManager.showIntro();
        spawnEnemy();
    }

    // Spawn musuh sesuai stage
    public void spawnEnemy() {
        switch (stage) {
            case 1: currentEnemy = new Goblin();    break;
            case 2: currentEnemy = new Orc();       break;
            case 3: currentEnemy = new DarkKnight();break;
            case 4: currentEnemy = new Devil();     break;
            default:
                endGame();
                return;
        }
        battleManager.setCurrentEnemy(currentEnemy);
        System.out.println("\n[STAGE " + stage + "] " + currentEnemy.getName() + " muncul!");
    }

    // Proses input dari GamePanel
    public void processInput(String zone) {
        if (isGameOver) return;
        battleManager.processInput(zone);

        // Cek hasil battle
        if (battleManager.isBattleOver()) {
            if (battleManager.getBattleResult().equals("WIN")) {
                nextStage();
            } else {
                isGameOver = true;
                storyManager.showGameOver();
            }
        }
    }

    // Lanjut ke stage berikutnya
    public void nextStage() {
        stage++;
        if (stage > 4) {
            endGame();
            return;
        }
        storyManager.showTransitionStory(stage - 1);
        if (stage == 4) storyManager.showBossDialog();
        spawnEnemy();
    }

    // Game selesai
    public void endGame() {
        isGameOver = true;
        storyManager.showEnding(player);
    }

    // Cek ending berdasarkan skor
    public String checkEnding() {
        if (!player.isAlive())           return "BAD_ENDING";
        if (player.getScore() >= 1500)   return "TRUE_ENDING";
        return "NORMAL_ENDING";
    }

    //  Tampilkan skor
    public void showScore() {
        System.out.println("\n=== FINAL SCORE ===");
        System.out.println("Nama   : " + player.getName());
        System.out.println("Score  : " + player.getScore());
        System.out.println("Level  : " + player.getLevel());
        System.out.println("Perfect: " + player.getPerfectCount());
        System.out.println("Good   : " + player.getGoodCount());
        System.out.println("Miss   : " + player.getMissCount());
        System.out.println("Ending : " + checkEnding());
    }

    // Getters
    public Player        getPlayer()        { return player; }
    public Enemy         getCurrentEnemy()  { return currentEnemy; }
    public BattleManager getBattleManager() { return battleManager; }
    public StoryManager  getStoryManager()  { return storyManager; }
    public ScoreManager  getScoreManager()  { return scoreManager; }
    public int           getStage()         { return stage; }
    public boolean       isGameOver()       { return isGameOver; }
}
