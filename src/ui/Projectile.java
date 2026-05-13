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
        if (dist < 20) {
            hit   = true;
            hitTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        if (hit) {
            // Efek ledakan kecil saat kena
            g2d.setColor(new Color(255, 200, 50, 200));
            g2d.fillOval((int)x - 15, (int)y - 15, 30, 30);
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int)x - 8, (int)y - 8, 16, 16);
            return;
        }

        // Gambar panah
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Body panah
        float angle = (float) Math.atan2(speedY, speedX);
        float tailX = x - (float)Math.cos(angle) * length;
        float tailY = y - (float)Math.sin(angle) * length;
        g2d.drawLine((int)tailX, (int)tailY, (int)x, (int)y);

        // Ujung panah (segitiga kecil)
        g2d.setColor(new Color(200, 150, 50));
        int[] px = {
                (int)(x + Math.cos(angle) * 8),
                (int)(x + Math.cos(angle + 2.5) * 8),
                (int)(x + Math.cos(angle - 2.5) * 8)
        };
        int[] py = {
                (int)(y + Math.sin(angle) * 8),
                (int)(y + Math.sin(angle + 2.5) * 8),
                (int)(y + Math.sin(angle - 2.5) * 8)
        };
        g2d.fillPolygon(px, py, 3);

        g2d.setStroke(new BasicStroke(1));
    }

    public boolean isActive() { return active; }
    public boolean isHit()    { return hit; }
}