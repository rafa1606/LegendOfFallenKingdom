package enemies;

public class Goblin extends Enemy {

    // ── Constructor (Phase 1 stats)
    public Goblin() {
        super("Goblin", 50, 5, 0,
              50, 0,          // xpReward, healReward
              4, 1.5f,        // bounceCount, bounceInterval
              0.30f, 0.40f);  // greenZone, yellowZone (merah = 30%)
    }

    // ── Override attack() ────────────────────────────────────
    @Override
    public void attack() {
        System.out.println("Goblin menyerang secara acak! Damage: " + getAttack());
    }

    // ── Override triggerPhase2() ─────────────────────────────
    @Override
    public void triggerPhase2() {
        super.triggerPhase2();
        setMaxHp(30);
        setHp(30);
        setAttack(10);
        setBounceCount(3);
        setBounceInterval(1.0f);
        setGreenZone(0.25f);
        setYellowZone(0.35f);  // merah = 40%
        setXpReward(100);
        setHealReward(30);

        System.out.println("Goblin PHASE 2: Kecepatan bar menjadi acak!");
        System.out.println("[Visual] Badan merah, aura bayangan, mata menyala terang");
    }

    // ── Pasif Phase 2: Kecepatan bar acak ────────────────────
    public float getRandomBounceInterval() {
        if (getPhase() == 2 && isPassiveActive()) {
            // Random antara 0.5 - 1.5 detik
            return 0.5f + (float)(Math.random() * 1.0f);
        }
        return getBounceInterval();
    }

    @Override
    public String getStatus() {
        return "[GOBLIN] HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | Phase: " + getPhase() +
               " | Zone H/K/M: " + (int)(getGreenZone()*100) + "%" +
               "/" + (int)(getYellowZone()*100) + "%" +
               "/" + (int)(getRedZone()*100) + "%";
    }
}
