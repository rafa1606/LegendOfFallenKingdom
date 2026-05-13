package ui;

import java.awt.*;

public class BounceBar {

    // ── Atribut ──────────────────────────────────────────────
    private int     x, y, width, height;
    private float   position;    // 0.0 = kiri, 1.0 = kanan
    private float   speed;       // kecepatan gerak per frame
    private boolean movingRight;
    private boolean active;
    private int     bounceCount;  // sisa pantulan
    private int     maxBounce;

    // Zona (dalam persentase 0.0 - 1.0)
    private float greenZone;    // lebar zona hijau di tengah
    private float yellowZone;   // lebar zona kuning di samping hijau

    // ── Warna zona ────────────────────────────────────────────
    private static final Color COLOR_GREEN  = new Color(60, 200, 80);
    private static final Color COLOR_YELLOW = new Color(240, 200, 40);
    private static final Color COLOR_RED    = new Color(220, 50, 50);
    private static final Color COLOR_BAR    = Color.WHITE;
    private static final Color COLOR_BG     = new Color(30, 30, 30, 180);

    // ── Constructor ──────────────────────────────────────────
    public BounceBar(int x, int y, int width, int height) {
        this.x       = x;
        this.y       = y;
        this.width   = width;
        this.height  = height;
        this.position = 0.0f;
        this.movingRight = true;
        this.active  = false;
    }

    // ── Set config dari musuh ─────────────────────────────────
    public void configure(int bounceCount, float intervalSeconds,
                          float greenZone, float yellowZone) {
        this.maxBounce   = bounceCount;
        this.bounceCount = bounceCount;
        this.greenZone   = greenZone;
        this.yellowZone  = yellowZone;
        // Speed: 1.0 / (interval * 60fps) untuk satu kali lebar
        this.speed       = 1.0f / (intervalSeconds * 60.0f);
        this.position    = 0.0f;
        this.movingRight = true;
        this.active      = true;
    }

    // ── Set kecepatan random (pasif Goblin phase 2) ───────────
    public void setRandomSpeed(float minInterval, float maxInterval) {
        float interval = minInterval + (float)(Math.random() * (maxInterval - minInterval));
        this.speed     = 1.0f / (interval * 60.0f);
    }

    // ── Update tiap frame ─────────────────────────────────────
    public void update() {
        if (!active) return;

        // Gerakkan bar
        if (movingRight) {
            position += speed;
            if (position >= 1.0f) {
                position     = 1.0f;
                movingRight  = false;
                bounceCount--;
            }
        } else {
            position -= speed;
            if (position <= 0.0f) {
                position     = 0.0f;
                movingRight  = true;
                bounceCount--;
            }
        }

        // Habis pantulan → bar berhenti
        if (bounceCount <= 0) {
            active = false;
        }
    }

    // ── Draw ke canvas ────────────────────────────────────────
    public void draw(Graphics2D g2d) {
        // Background bar
        g2d.setColor(COLOR_BG);
        g2d.fillRoundRect(x, y, width, height, 10, 10);

        // Zona Merah (full background dulu)
        g2d.setColor(COLOR_RED);
        g2d.fillRoundRect(x, y, width, height, 10, 10);

        // Zona Kuning (kiri & kanan tengah)
        float redW    = (1.0f - greenZone - yellowZone) / 2;
        int   yellowX = x + (int)(width * redW);
        int   yellowW = (int)(width * (greenZone + yellowZone));
        g2d.setColor(COLOR_YELLOW);
        g2d.fillRect(yellowX, y, yellowW, height);

        // Zona Hijau (tengah)
        float greenStart = redW + yellowZone / 2;
        int   greenX     = x + (int)(width * greenStart);
        int   greenW     = (int)(width * greenZone);
        g2d.setColor(COLOR_GREEN);
        g2d.fillRect(greenX, y, greenW, height);

        // Garis border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 10, 10);

        // Pointer (bar bergerak)
        if (active) {
            int pointerX = x + (int)(position * width) - 4;
            g2d.setColor(COLOR_BAR);
            g2d.fillRect(pointerX, y - 5, 8, height + 10);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(pointerX, y - 5, 8, height + 10);
        }

        // Bounce counter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Bounce: " + Math.max(0, bounceCount), x, y - 10);
    }

    // ── Deteksi zona saat player input ───────────────────────
    public String getZoneAtCurrentPosition() {
        float redW  = (1.0f - greenZone - yellowZone) / 2;
        float greenStart = redW + yellowZone / 2;
        float greenEnd   = greenStart + greenZone;
        float yellowEnd  = redW + yellowZone / 2 + greenZone + yellowZone / 2;

        if (position >= greenStart && position <= greenEnd) {
            return "GREEN";
        } else if (position >= redW && position <= yellowEnd) {
            return "YELLOW";
        } else {
            return "RED";
        }
    }

    // ── Getters ──────────────────────────────────────────────
    public boolean isActive()   { return active; }
    public float   getPosition(){ return position; }
    public void    setActive(boolean active) { this.active = active; }
}
