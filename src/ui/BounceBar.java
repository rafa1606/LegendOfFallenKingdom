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

    public void draw(Graphics2D g2d) {
        // Background merah dulu
        g2d.setColor(COLOR_RED);
        g2d.fillRoundRect(x, y, width, height, 10, 10);

        // Hitung zona
        float redW    = (1.0f - greenZone - yellowZone) / 2.0f;
        float yellowW = yellowZone / 2.0f;

        // Zona kuning kiri
        int yx1 = x + (int)(width * redW);
        int yw  = (int)(width * yellowW);
        g2d.setColor(COLOR_YELLOW);
        g2d.fillRect(yx1, y, yw, height);

        // Zona hijau tengah
        int gx = x + (int)(width * (redW + yellowW));
        int gw = (int)(width * greenZone);
        g2d.setColor(COLOR_GREEN);
        g2d.fillRect(gx, y, gw, height);

        // Zona kuning kanan
        int yx2 = x + (int)(width * (redW + yellowW + greenZone));
        g2d.setColor(COLOR_YELLOW);
        g2d.fillRect(yx2, y, yw, height);

        // Border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 10, 10);

        // Pointer
        if (active) {
            int pointerX = x + (int)(position * width) - 4;
            g2d.setColor(COLOR_BAR);
            g2d.fillRect(pointerX, y - 5, 8, height + 10);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(pointerX, y - 5, 8, height + 10);
        }

        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Bounce: " + Math.max(0, bounceCount), x, y - 10);
    }

    public String getZoneAtCurrentPosition() {
        float redW    = (1.0f - greenZone - yellowZone) / 2.0f;
        float yellowW = yellowZone / 2.0f;

        float yellowStart1 = redW;
        float greenStart   = redW + yellowW;
        float greenEnd     = greenStart + greenZone;
        float yellowEnd2   = greenEnd + yellowW;

        if (position >= greenStart && position <= greenEnd) {
            return "GREEN";
        } else if ((position >= yellowStart1 && position < greenStart) ||
                (position > greenEnd && position <= yellowEnd2)) {
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
