package ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CharacterRenderer {

    private List<BufferedImage> idleFrames   = new ArrayList<>();
    private List<BufferedImage> attackFrames = new ArrayList<>();
    private List<BufferedImage> hurtFrames   = new ArrayList<>();
    private List<BufferedImage> deathFrames  = new ArrayList<>();
    private List<BufferedImage> currentFrames;

    private int     frameIndex    = 0;
    private int     frameTimer    = 0;
    private int     frameDelay    = 5;
    private int     x, y, targetY, width, height;
    private boolean isSpawning;
    private boolean facingRight;
    private boolean isAttacking   = false;
    private boolean isHit         = false;
    private int     attackOffsetX = 0;
    private int     hitTimer      = 0;
    private double  idleTimer     = 0;
    private int     idleOffsetY   = 0;
    private boolean isDying       = false;
    private boolean deathDone     = false;

    public CharacterRenderer(String folder,
                             String idlePrefix,
                             String attackPrefix,
                             String hurtPrefix,
                             String deathPrefix,
                             int x, int targetY,
                             int width, int height,
                             boolean facingRight) {
        this.x           = x;
        this.targetY     = targetY;
        this.y           = -height;
        this.width       = width;
        this.height      = height;
        this.facingRight = facingRight;
        this.isSpawning  = true;

        idleFrames   = loadFrames(folder, idlePrefix);
        attackFrames = loadFrames(folder, attackPrefix);
        hurtFrames   = loadFrames(folder, hurtPrefix);
        deathFrames  = loadFrames(folder, deathPrefix);
        currentFrames = idleFrames;

        System.out.println("[" + folder + "]");
        System.out.println("  Idle  : " + idleFrames.size() + " frames");
        System.out.println("  Attack: " + attackFrames.size() + " frames");
        System.out.println("  Hurt  : " + hurtFrames.size() + " frames");
        System.out.println("  Death : " + deathFrames.size() + " frames");
    }

    private List<BufferedImage> loadFrames(String folder, String prefix) {
        List<BufferedImage> frames = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return frames;
        for (int i = 0; i < 30; i++) {
            String[] names = {
                    String.format("%s%03d.png", prefix, i),
                    String.format("%s%02d.png", prefix, i),
                    String.format("%s%d.png",   prefix, i),
            };
            boolean loaded = false;
            for (String name : names) {
                File f = new File(folder + "/" + name);
                if (f.exists()) {
                    try {
                        frames.add(ImageIO.read(f));
                        loaded = true;
                        break;
                    } catch (Exception ignored) {}
                }
            }
            if (!loaded && i > 0) break;
        }
        return frames;
    }

    public void update() {
        // Animasi mati
        if (isDying) {
            currentFrames = deathFrames.isEmpty() ? hurtFrames : deathFrames;
            frameTimer++;
            if (frameTimer >= frameDelay) {
                frameTimer = 0;
                if (frameIndex < currentFrames.size() - 1) {
                    frameIndex++;
                } else {
                    deathDone = true;
                }
            }
            return;
        }

        // Spawn dari atas
        if (isSpawning) {
            y += 10;
            if (y >= targetY) { y = targetY; isSpawning = false; }
            return;
        }

        // Idle gerak naik turun
        idleTimer  += 0.05;
        idleOffsetY = (int)(Math.sin(idleTimer) * 5);

        // Pilih animasi
        if (isAttacking) {
            attackOffsetX = facingRight ? 15 : -15;
            currentFrames = attackFrames.isEmpty() ? idleFrames : attackFrames;
        } else if (isHit) {
            currentFrames = hurtFrames.isEmpty() ? idleFrames : hurtFrames;
        } else {
            currentFrames = idleFrames;
            attackOffsetX = 0;
        }

        // Ganti frame
        if (!currentFrames.isEmpty()) {
            frameTimer++;
            if (frameTimer >= frameDelay) {
                frameTimer = 0;
                frameIndex = (frameIndex + 1) % currentFrames.size();
                if (frameIndex == 0 && (isAttacking || isHit)) {
                    isAttacking = false;
                    isHit       = false;
                    hitTimer    = 0;
                }
            }
        }
    }

    public void draw(Graphics2D g2d) {
        int drawX = x + attackOffsetX;
        int drawY = y + idleOffsetY;

        if (currentFrames == null || currentFrames.isEmpty()) {
            g2d.setColor(facingRight
                    ? new Color(30, 100, 170)
                    : new Color(160, 60, 30));
            g2d.fillRoundRect(drawX, drawY, width, height, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(facingRight ? "DAREN" : "ENEMY",
                    drawX + 10, drawY + height / 2);
            return;
        }

        int idx = Math.min(frameIndex, currentFrames.size() - 1);
        BufferedImage frame = currentFrames.get(idx);
        if (frame == null) return;

        if (!facingRight) {
            g2d.drawImage(frame, drawX + width, drawY, -width, height, null);
        } else {
            g2d.drawImage(frame, drawX, drawY, width, height, null);
        }
    }

    public void triggerAttack() {
        if (!isAttacking) {
            isAttacking = true;
            frameIndex  = 0;
            frameTimer  = 0;
        }
    }

    public void triggerHit() {
        if (!isHit) {
            isHit      = true;
            hitTimer   = 0;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public void triggerDeath() {
        if (!isDying) {
            isDying    = true;
            deathDone  = false;
            frameIndex = 0;
            frameTimer = 0;
        }
    }

    public boolean isDeathDone() { return deathDone; }
    public boolean isDying()     { return isDying; }
    public boolean isSpawning()  { return isSpawning; }
}