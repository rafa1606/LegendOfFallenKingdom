package ui;

import java.awt.*;

public class Projectile {

    private float x, y;
    private float targetX, targetY;
    private float speedX, speedY;
    private boolean active;
    private boolean hit;
    private int hitTimer = 0;

    // Ukuran & warna panah
    private Color color;
    private int length = 20;

    public Projectile(float startX, float startY,
                      float targetX, float targetY, Color color) {
        this.x       = startX;
        this.y       = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.color   = color;
        this.active  = true;
        this.hit     = false;

        // Hitung arah & kecepatan
        float dx   = targetX - startX;
        float dy   = targetY - startY;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        float speed = 15.0f;
        this.speedX = dx / dist * speed;
        this.speedY = dy / dist * speed;
    }

    public void update() {
        if (!active) return;
        if (hit) {
            hitTimer++;
            if (hitTimer > 10) active = false;
            return;
        }

        x += speedX;
        y += speedY;

        // Cek sudah sampai target
        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist < 5) {
            hit   = true;
            hitTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        if (hit) {
            // Efek ledakan saat kena
            g2d.setColor(new Color(255, 200, 50, 200));
            g2d.fillOval((int)x - 15, (int)y - 15, 30, 30);
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int)x - 8, (int)y - 8, 16, 16);
            return;
        }

        // Gambar peluru (lingkaran kecil + ekor)
        g2d.setColor(new Color(255, 255, 100)); // kuning terang
        g2d.fillOval((int)x - 6, (int)y - 6, 12, 12);

        // Ekor peluru
        g2d.setColor(new Color(255, 150, 50, 180));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float angle = (float) Math.atan2(speedY, speedX);
        float tailX = x - (float)Math.cos(angle) * 20;
        float tailY = y - (float)Math.sin(angle) * 20;
        g2d.drawLine((int)tailX, (int)tailY, (int)x, (int)y);
        g2d.setStroke(new BasicStroke(1));

        // Kilatan cahaya
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval((int)x - 3, (int)y - 3, 6, 6);
    }

    public boolean isActive() { return active; }
    public boolean isHit()    { return hit; }
}