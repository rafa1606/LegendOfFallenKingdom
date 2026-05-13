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
    // Tambah atribut ini di atas class
    private boolean isWalking    = false;
    private int     walkOffsetX  = 0;
    private int     walkTimer    = 0;
    private int     walkDuration = 40; // frame jalan
    private boolean walkingRight = false;

    public CharacterRenderer(String folder,
                             String idlePrefix,
                             String attackPrefix,
                             String hurtPrefix,
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
        currentFrames = idleFrames;

        System.out.println("[" + folder + "]");
        System.out.println("  Idle  : " + idleFrames.size() + " frames");
        System.out.println("  Attack: " + attackFrames.size() + " frames");
        System.out.println("  Hurt  : " + hurtFrames.size() + " frames");
    }

    private List<BufferedImage> loadFrames(String folder, String prefix) {
        List<BufferedImage> frames = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return frames;

        for (int i = 0; i < 30; i++) {
            // Coba 3 format angka: 000, 00, 0
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
                    } catch (Exception e) {
                        System.out.println("Error baca: " + name);
                    }
                }
            }
            // Stop kalau frame i=1 ke atas tidak ketemu
            if (!loaded && i > 0) break;
        }
        return frames;
    }

    public void update() {
        if (isSpawning) {
            y += 10;
            if (y >= targetY) { y = targetY; isSpawning = false; }
            return;
        }

        idleTimer  += 0.05;
        idleOffsetY = (int)(Math.sin(idleTimer) * 5);

        // ── Enemy walk attack ────────────────────────────────
        if (isWalking) {
            walkTimer++;
            int direction = walkingRight ? 1 : -1;
            int halfDuration = walkDuration / 2;

            if (walkTimer <= halfDuration) {
                // Jalan maju ke arah player
                walkOffsetX += direction * 20;
                currentFrames = attackFrames.isEmpty() ? idleFrames : attackFrames;
            } else if (walkTimer <= walkDuration) {
                // Balik ke posisi asal
                walkOffsetX -= direction * 20;
                currentFrames = idleFrames;
            } else {
                // Selesai walk
                isWalking   = false;
                walkOffsetX = 0;
                walkTimer   = 0;
            }
        } else if (isAttacking) {
            attackOffsetX = facingRight ? 15 : -15;
            currentFrames = attackFrames.isEmpty() ? idleFrames : attackFrames;
        } else if (isHit) {
            currentFrames = hurtFrames.isEmpty() ? idleFrames : hurtFrames;
        } else {
            currentFrames = idleFrames;
            attackOffsetX = 0;
        }

        // Ganti frame animasi
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

    // Musuh jalan ke player lalu balik
    public void triggerWalkAttack(boolean walkRight) {
        if (!isWalking) {
            isWalking    = true;
            walkingRight = walkRight;
            walkTimer    = 0;
            walkOffsetX  = 0;
            frameIndex   = 0;
            frameTimer   = 0;
        }
    }

    public void draw(Graphics2D g2d) {
        int drawX = x + attackOffsetX + walkOffsetX; // tambah walkOffsetX
        int drawY = y + idleOffsetY;

        // Kalau tidak ada sprite → pakai placeholder
        if (currentFrames == null || currentFrames.isEmpty()) {
            g2d.setColor(facingRight
                    ? new Color(30, 100, 170)
                    : new Color(160, 60, 30));
            g2d.fillRoundRect(drawX, drawY, width, height, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(facingRight ? "ARCHER" : "ENEMY",
                    drawX + 10, drawY + height / 2);
            return;
        }

        int idx = Math.min(frameIndex, currentFrames.size() - 1);
        BufferedImage frame = currentFrames.get(idx);
        if (frame == null) return;

        if (!facingRight) {
            // Flip horizontal untuk musuh
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

    public boolean isSpawning() { return isSpawning; }
}