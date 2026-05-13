package enemies;

import abstract_.Character;
import interfaces.Attackable;

public class Enemy extends Character implements Attackable {

    // ── Atribut tambahan Enemy ───────────────────────────────
    private int     xpReward;
    private int     healReward;
    private int     phase;           // 1 = normal, 2 = triggered
    private int     bounceCount;     // jumlah pantulan per giliran
    private float   bounceInterval;  // detik per pantulan
    private float   greenZone;       // lebar zona hijau (%)
    private float   yellowZone;      // lebar zona kuning (%)
    private boolean passiveActive;

    // ── Constructor ──────────────────────────────────────────
    public Enemy(String name, int hp, int attack, int defense,
                 int xpReward, int healReward,
                 int bounceCount, float bounceInterval,
                 float greenZone, float yellowZone) {
        super(name, hp, attack, defense);
        this.xpReward       = xpReward;
        this.healReward     = healReward;
        this.phase          = 1;
        this.bounceCount    = bounceCount;
        this.bounceInterval = bounceInterval;
        this.greenZone      = greenZone;
        this.yellowZone     = yellowZone;
        this.passiveActive  = false;
    }

    // ── Override attack() ────────────────────────────────────
    @Override
    public void attack() {
        System.out.println(getName() + " menyerang! Damage: " + getAttack());
    }

    // ── Override takeDamage() ────────────────────────────────
    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        // Cek apakah perlu trigger phase 2
        if (getHp() <= 0 && phase == 1) {
            // HP habis di phase 1, trigger phase 2
        }
    }

    // ── Trigger Phase 2 (di-override tiap subclass) ──────────
    public void triggerPhase2() {
        this.phase = 2;
        this.passiveActive = true;
        System.out.println(getName() + " BEREVOLUSI ke Phase 2!");
    }

    // ── Aksi AI saat player MISS ─────────────────────────────
    public void aiAction(abstract_.Character target) {
        int dmg = getAttack();
        System.out.println(getName() + " membalas serangan! Damage: " + dmg);
        target.takeDamage(dmg);
    }

    // ── Drop reward saat kalah ───────────────────────────────
    public int[] dropReward() {
        System.out.println(getName() + " dikalahkan! +" + xpReward + " XP, +" + healReward + " HP");
        return new int[]{xpReward, healReward};
    }

    // ── Override getStatus() ─────────────────────────────────
    @Override
    public String getStatus() {
        return "[ENEMY] " + getName() +
               " | HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | Phase: " + phase +
               " | Bounce: " + bounceCount + "x @ " + bounceInterval + "s";
    }

    // ── Getters ──────────────────────────────────────────────
    public int   getXpReward()       { return xpReward; }
    public int   getHealReward()     { return healReward; }
    public int   getPhase()          { return phase; }
    public int   getBounceCount()    { return bounceCount; }
    public float getBounceInterval() { return bounceInterval; }
    public float getGreenZone()      { return greenZone; }
    public float getYellowZone()     { return yellowZone; }
    public float getRedZone()        { return 1.0f - greenZone - yellowZone; }
    public boolean isPassiveActive() { return passiveActive; }

    // ── Setters untuk phase 2 ─────────────────────────────────
    protected void setPhase(int phase)                   { this.phase = phase; }
    protected void setBounceCount(int count)             { this.bounceCount = count; }
    protected void setBounceInterval(float interval)     { this.bounceInterval = interval; }
    protected void setGreenZone(float zone)              { this.greenZone = zone; }
    protected void setYellowZone(float zone)             { this.yellowZone = zone; }
    protected void setXpReward(int xp)                  { this.xpReward = xp; }
    protected void setHealReward(int heal)               { this.healReward = heal; }
    protected void setPassiveActive(boolean active)      { this.passiveActive = active; }
}
