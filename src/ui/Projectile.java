package ui;

import java.awt.*;

public class Projectile {

    private float x, y;
    private float targetX, targetY;
    private float speedX, speedY;
    private boolean active;
    private boolean hit;
    private int hitTimer = 0;
    private Color color;
    private Color glowColor;
    private int length = 20;
    private boolean isEnemyProjectile; // true = dari musuh ke Daren

    public Projectile(float startX, float startY,
                      float targetX, float targetY,
                      Color color, Color glowColor,
                      boolean isEnemyProjectile) {
        this.x = startX;
        this.y = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.color = color;
        this.glowColor = glowColor;
        this.isEnemyProjectile = isEnemyProjectile;
        this.active = true;
        this.hit = false;

        float dx = targetX - startX;
        float dy = targetY - startY;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        float speed = 12.0f;
        this.speedX = dx / dist * speed;
        this.speedY = dy / dist * speed;
    }

    // Constructor lama untuk peluru Daren (backward compatible)
    public Projectile(float startX, float startY,
                      float targetX, float targetY, Color color) {
        this(startX, startY, targetX, targetY,
                color, new Color(255, 255, 100), false);
    }

    public void update() {
        if (!active) return;
        if (hit) {
            hitTimer++;
            if (hitTimer > 15) active = false;
            return;
        }

        x += speedX;
        y += speedY;

        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist < 5) {
            hit = true;
            hitTimer = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        if (hit) {
            // Efek ledakan
            g2d.setColor(new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(), 180));
            g2d.fillOval((int)x - 20, (int)y - 20, 40, 40);
            g2d.setColor(color);
            g2d.fillOval((int)x - 10, (int)y - 10, 20, 20);
            return;
        }

        if (isEnemyProjectile) {
            drawMagicBall(g2d);
        } else {
            drawBullet(g2d);
        }
    }

    // Peluru Daren
    private void drawBullet(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int)x - 6, (int)y - 6, 12, 12);
        g2d.setColor(glowColor);
        g2d.setStroke(new BasicStroke(3,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float angle = (float) Math.atan2(speedY, speedX);
        float tailX = x - (float)Math.cos(angle) * 20;
        float tailY = y - (float)Math.sin(angle) * 20;
        g2d.drawLine((int)tailX, (int)tailY, (int)x, (int)y);
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)x - 3, (int)y - 3, 6, 6);
        g2d.setStroke(new BasicStroke(1));
    }

    // Bola sihir musuh
    private void drawMagicBall(Graphics2D g2d) {
        // Aura luar
        g2d.setColor(new Color(
                glowColor.getRed(),
                glowColor.getGreen(),
                glowColor.getBlue(), 80));
        g2d.fillOval((int)x - 14, (int)y - 14, 28, 28);

        // Bola utama
        g2d.setColor(color);
        g2d.fillOval((int)x - 8, (int)y - 8, 16, 16);

        // Kilatan dalam
        g2d.setColor(new Color(
                glowColor.getRed(),
                glowColor.getGreen(),
                glowColor.getBlue(), 200));
        g2d.fillOval((int)x - 4, (int)y - 4, 8, 8);

        // Ekor sihir
        g2d.setColor(new Color(
                glowColor.getRed(),
                glowColor.getGreen(),
                glowColor.getBlue(), 120));
        g2d.setStroke(new BasicStroke(3,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        float angle = (float) Math.atan2(speedY, speedX);
        for (int i = 1; i <= 3; i++) {
            float tailX = x - (float)Math.cos(angle) * (i * 8);
            float tailY = y - (float)Math.sin(angle) * (i * 8);
            int size = 10 - i * 2;
            g2d.fillOval((int)tailX - size/2, (int)tailY - size/2, size, size);
        }
        g2d.setStroke(new BasicStroke(1));
    }

    public boolean isActive() { return active; }
    public boolean isHit()    { return hit; }
    public boolean isEnemyProjectile() { return isEnemyProjectile; }
}