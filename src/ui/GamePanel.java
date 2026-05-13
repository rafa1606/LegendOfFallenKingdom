package ui;

import manager.GameManager;
import characters.Player;
import enemies.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int WIDTH  = 800;
    public static final int HEIGHT = 500;

    private GameManager       gameManager;
    private Timer             gameLoop;
    private String            gameState;
    private CharacterRenderer archerRenderer;
    private CharacterRenderer enemyRenderer;
    private BounceBar         bounceBar;
    private BufferedImage     currentBackground;
    private List<FloatingText> floatingTexts;
    private Color bgTop    = new Color(15, 10, 35);
    private Color bgBottom = new Color(40, 10, 10);
    private List<Projectile> projectiles = new ArrayList<>();
    private boolean isRoundTransition = false;
    private int     transitionTimer   = 0;
    private int     transitionDelay   = 120; // 120 frame = 2 detik

    private static class FloatingText {
        String text; int x, y; float alpha; Color color;
        FloatingText(String text, int x, int y, Color color) {
            this.text=text; this.x=x; this.y=y; this.alpha=1.0f; this.color=color;
        }
        void update() { y-=2; alpha-=0.02f; }
        boolean isDead() { return alpha<=0; }
    }

    public GamePanel() {
        System.out.println("Working dir: " + System.getProperty("user.dir"));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        gameManager   = new GameManager();
        floatingTexts = new ArrayList<>();
        gameState     = "MENU";
        bounceBar     = new BounceBar(150, HEIGHT - 90, 500, 30);
        gameLoop      = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private BufferedImage loadImage(String path) {
        try {
            File f = new File(path);
            if (f.exists()) return ImageIO.read(f);
            System.out.println("Tidak ditemukan: " + f.getAbsolutePath());
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + path + " → " + e.getMessage());
            return null;
        }
    }

    public void startBattle() {
        gameState = "BATTLE";
        gameManager.startGame();
        setupCharacterRenderers();
        setupBounceBar();
        loadBackground();
    }

    private void loadBackground() {
        currentBackground = loadImage(
                "../assets/backgrounds/bg_stage" + gameManager.getStage() + ".png");
    }

    private void setupCharacterRenderers() {
        String base = "../assets/";

        archerRenderer = new CharacterRenderer(
                base + "characters/archer",
                "Elf_01__IDLE_",
                "Elf_01__ATTACK_",
                "Elf_01__HURT_",
                30, HEIGHT - 300, 130, 260, true
        );

        switch (gameManager.getStage()) {
            case 1:
                enemyRenderer = new CharacterRenderer(
                        base + "enemies/goblin",
                        "ORK_01_IDLE_",
                        "ORK_01_ATTAK_",
                        "ORK_01_HURT_",
                        WIDTH - 280, HEIGHT - 320, 250, 280, false
                );
                break;
            case 2:
                enemyRenderer = new CharacterRenderer(
                        base + "enemies/orc",
                        "ORK_03_IDLE_",
                        "ORK_03_ATTACK_",
                        "ORK_03_HURT_",
                        WIDTH - 280, HEIGHT - 320, 250, 280, false
                );
                break;
            case 3:
                enemyRenderer = new CharacterRenderer(
                        base + "enemies/darkknight",
                        "Troll_03_1_IDLE_",
                        "Troll_03_1_ATTACK_",
                        "Troll_03_1_HURT_",
                        WIDTH - 280, HEIGHT - 320, 250, 280, false
                );
                break;
            case 4:
                enemyRenderer = new CharacterRenderer(
                        base + "enemies/devil",
                        "Idle_",
                        "Attack_",
                        "Hurt_",
                        WIDTH - 280, HEIGHT - 320, 250, 280, false
                );
                break;
        }
    }

    private void setupBounceBar() {
        Enemy enemy = gameManager.getCurrentEnemy();
        if (enemy == null) return;
        bounceBar.configure(
                enemy.getBounceCount(),
                enemy.getBounceInterval(),
                enemy.getGreenZone(),
                enemy.getYellowZone()
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) { update(); repaint(); }

    private void update() {
        if (!gameState.equals("BATTLE")) return;
        if (isRoundTransition) {
            transitionTimer++;
            if (transitionTimer >= transitionDelay) {
                isRoundTransition = false;
                transitionTimer   = 0;
            }
            return; // stop update selama jeda
        }
        if (archerRenderer != null) archerRenderer.update();
        if (enemyRenderer  != null) enemyRenderer.update();

        boolean wasActive = bounceBar.isActive();
        bounceBar.update();

        // Bar habis tanpa input → otomatis MISS → musuh serang!
        if (wasActive && !bounceBar.isActive()) {
            gameManager.processInput("RED"); // RED = MISS

            floatingTexts.add(new FloatingText(
                    "MISS!", WIDTH/2 - 40, HEIGHT - 130, Color.RED));

            if (archerRenderer != null) archerRenderer.triggerHit();
            if (enemyRenderer  != null) enemyRenderer.triggerAttack();

            if (gameManager.isGameOver()) {
                gameState = "GAMEOVER";
            } else {
                // Reset bounce bar untuk giliran berikutnya
                Enemy enemy = gameManager.getCurrentEnemy();
                if (enemy != null) {
                    float interval = enemy.getBounceInterval();
                    if (enemy instanceof Goblin && enemy.getPhase() == 2) {
                        interval = ((Goblin) enemy).getRandomBounceInterval();
                    }
                    bounceBar.configure(
                            enemy.getBounceCount(), interval,
                            enemy.getGreenZone(), enemy.getYellowZone()
                    );
                }
            }
        }

        floatingTexts.removeIf(FloatingText::isDead);
        for (FloatingText ft : floatingTexts) ft.update();
        // ← TAMBAHKAN DI SINI
        projectiles.removeIf(p -> !p.isActive());
        for (Projectile p : projectiles) p.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        switch (gameState) {
            case "MENU":     drawMenu(g2d);     break;
            case "BATTLE":   drawBattle(g2d);   break;
            case "GAMEOVER": drawGameOver(g2d); break;
        }
    }

    private void drawMenu(Graphics2D g2d) {
        GradientPaint gp = new GradientPaint(0,0,bgTop,0,HEIGHT,bgBottom);
        g2d.setPaint(gp); g2d.fillRect(0,0,WIDTH,HEIGHT);
        g2d.setFont(new Font("Georgia", Font.BOLD, 36));
        g2d.setColor(new Color(220,180,80));
        drawCentered(g2d, "LEGEND OF THE FALLEN KINGDOM", WIDTH/2, 160);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.LIGHT_GRAY);
        drawCentered(g2d, "Tekan ENTER untuk mulai", WIDTH/2, 260);
        drawCentered(g2d, "Tekan SPACE saat bar di zona HIJAU / KUNING", WIDTH/2, 300);
        drawCentered(g2d, "Tekan ESC untuk keluar", WIDTH/2, 330);
    }

    private void drawBattle(Graphics2D g2d) {
        if (currentBackground != null) {
            g2d.drawImage(currentBackground, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gp = new GradientPaint(0,0,bgTop,0,HEIGHT,bgBottom);
            g2d.setPaint(gp); g2d.fillRect(0,0,WIDTH,HEIGHT);
        }
        g2d.setColor(new Color(20,15,10,180));
        g2d.fillRect(0, HEIGHT-50, WIDTH, 50);

        if (archerRenderer != null) archerRenderer.draw(g2d);
        if (enemyRenderer  != null) enemyRenderer.draw(g2d);

        // ← TAMBAHKAN DI SINI
        for (Projectile p : projectiles) p.draw(g2d);


        bounceBar.draw(g2d);

        Player player = gameManager.getPlayer();
        Enemy  enemy  = gameManager.getCurrentEnemy();
        if (player != null)
            drawHpBar(g2d, 20, 20, 200, 20,
                    player.getHp(), player.getMaxHp(),
                    new Color(50,200,80), "Archer");
        // Jadi ini (ambil langsung dari BattleManager):
        Enemy currentEnemy = gameManager.getBattleManager().getCurrentEnemy();
        if (currentEnemy != null)
            drawHpBar(g2d, WIDTH-220, 20, 200, 20,
                    currentEnemy.getHp(), currentEnemy.getMaxHp(),
                    new Color(220,60,60), currentEnemy.getName());
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        if (player != null) {
            g2d.drawString("Score: "  + player.getScore(),              20, 70);
            g2d.drawString("Level: "  + player.getLevel(),              20, 90);
            g2d.drawString("Combo: "  + player.getComboCount() + "x",   20, 110);
        }
        g2d.setColor(new Color(220,180,80));
        drawCentered(g2d, "Stage " + gameManager.getStage() + "/4", WIDTH/2, 25);

        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.setColor(Color.WHITE);
        drawCentered(g2d, "Tekan SPACE saat bar di zona HIJAU / KUNING!", WIDTH/2, HEIGHT-105);

        for (FloatingText ft : floatingTexts) {
            g2d.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.max(0, ft.alpha)));
            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            g2d.setColor(ft.color);
            g2d.drawString(ft.text, ft.x, ft.y);
            g2d.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0f));
        }

        // Tambahkan di paling bawah drawBattle(), sebelum kurung tutup
        if (isRoundTransition) {
            // Overlay gelap
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);

            // Teks stage berikutnya
            g2d.setFont(new Font("Georgia", Font.BOLD, 36));
            g2d.setColor(new Color(220, 180, 80));
            drawCentered(g2d, "Stage " + gameManager.getStage() + " / 4",
                    WIDTH/2, HEIGHT/2 - 20);

            // Bar loading sederhana
            int barW = 300;
            int barX = WIDTH/2 - barW/2;
            int barY = HEIGHT/2 + 20;
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRoundRect(barX, barY, barW, 12, 8, 8);
            g2d.setColor(new Color(220, 180, 80));
            int fill = (int)((float)transitionTimer / transitionDelay * barW);
            g2d.fillRoundRect(barX, barY, fill, 12, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(barX, barY, barW, 12, 8, 8);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        GradientPaint gp = new GradientPaint(0,0,Color.BLACK,0,HEIGHT,new Color(60,0,0));
        g2d.setPaint(gp); g2d.fillRect(0,0,WIDTH,HEIGHT);
        Player player = gameManager.getPlayer();
        String ending = gameManager.checkEnding();
        g2d.setFont(new Font("Georgia", Font.BOLD, 40));
        if (ending.equals("TRUE_ENDING")) {
            g2d.setColor(new Color(220,180,80));
            drawCentered(g2d, "TRUE ENDING!", WIDTH/2, 160);
        } else if (ending.equals("NORMAL_ENDING")) {
            g2d.setColor(Color.CYAN);
            drawCentered(g2d, "NORMAL ENDING", WIDTH/2, 160);
        } else {
            g2d.setColor(Color.RED);
            drawCentered(g2d, "GAME OVER", WIDTH/2, 160);
        }
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(Color.WHITE);
        if (player != null) {
            drawCentered(g2d, "Final Score: " + player.getScore(), WIDTH/2, 240);
            drawCentered(g2d, "Level: " + player.getLevel(), WIDTH/2, 270);
            drawCentered(g2d, "Perfect: " + player.getPerfectCount() +
                    "  Good: " + player.getGoodCount() +
                    "  Miss: " + player.getMissCount(), WIDTH/2, 300);
        }
        g2d.setFont(new Font("Arial", Font.PLAIN, 15));
        g2d.setColor(Color.LIGHT_GRAY);
        drawCentered(g2d, "Tekan ENTER untuk kembali ke Menu", WIDTH/2, 360);
    }

    private void drawHpBar(Graphics2D g2d, int x, int y, int w, int h,
                           int hp, int maxHp, Color color, String label) {
        g2d.setColor(new Color(40,40,40,200));
        g2d.fillRoundRect(x,y,w,h,8,8);
        if (maxHp > 0) {
            int fillW = (int)((float)hp/maxHp*w);
            g2d.setColor(color);
            g2d.fillRoundRect(x,y,Math.max(0,fillW),h,8,8);
        }
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x,y,w,h,8,8);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(label+": "+hp+"/"+maxHp, x, y-4);
    }

    private void drawCentered(Graphics2D g2d, String text, int cx, int cy) {
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, cx - fm.stringWidth(text)/2, cy);
    }

    private void handleInput() {
        if (!gameState.equals("BATTLE")) return;
        if (!bounceBar.isActive()) return;

        String zone = bounceBar.getZoneAtCurrentPosition();

        // Simpan stage sebelum proses
        int stageBefore = gameManager.getStage();

        gameManager.processInput(zone);

        int fx = WIDTH/2 - 40;
        int fy = HEIGHT - 130;

        switch (zone) {
            case "GREEN":
                floatingTexts.add(new FloatingText("PERFECT!", fx, fy, Color.GREEN));
                if (archerRenderer != null) archerRenderer.triggerAttack();
                // Tembak panah ke musuh!
                projectiles.add(new Projectile(
                        200, HEIGHT - 200,           // posisi Archer
                        WIDTH - 200, HEIGHT - 200,   // posisi Enemy
                        new Color(180, 130, 40)      // warna coklat
                ));
                if (enemyRenderer != null) enemyRenderer.triggerHit();
                break;

            case "YELLOW":
                floatingTexts.add(new FloatingText("GOOD!", fx, fy, Color.YELLOW));
                if (archerRenderer != null) archerRenderer.triggerAttack();
                projectiles.add(new Projectile(
                        200, HEIGHT - 200,
                        WIDTH - 200, HEIGHT - 200,
                        new Color(180, 130, 40)
                ));
                if (enemyRenderer != null) enemyRenderer.triggerHit();
                break;

            case "RED":
                floatingTexts.add(new FloatingText("MISS!", fx, fy, Color.RED));
                if (archerRenderer != null) archerRenderer.triggerHit();
                // Musuh jalan ke player dan mukul!
                if (enemyRenderer != null) enemyRenderer.triggerWalkAttack(false);
                break;
        }

        if (gameManager.isGameOver()) {
            gameState = "GAMEOVER";
            return;
        }

        if (gameManager.getStage() != stageBefore) {
            isRoundTransition = true;
            transitionTimer   = 0;
            setupCharacterRenderers();
            loadBackground();
        }


        // Update bounce bar
        Enemy enemy = gameManager.getCurrentEnemy();
        if (enemy != null) {
            float interval = enemy.getBounceInterval();
            if (enemy instanceof Goblin && enemy.getPhase() == 2) {
                interval = ((Goblin) enemy).getRandomBounceInterval();
            }
            bounceBar.configure(
                    enemy.getBounceCount(), interval,
                    enemy.getGreenZone(), enemy.getYellowZone()
            );
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (gameState.equals("BATTLE")) handleInput();
                break;
            case KeyEvent.VK_ENTER:
                if (gameState.equals("MENU")) {
                    startBattle();
                } else if (gameState.equals("GAMEOVER")) {
                    gameManager = new GameManager();
                    gameState   = "MENU";
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e)    {}
}