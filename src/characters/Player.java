package characters;

import abstract_.Character;
import interfaces.Attackable;
import interfaces.Skillable;

public class Player extends Character implements Attackable, Skillable {

    // ── Atribut tambahan Player ──────────────────────────────
    private int xp;
    private int xpToNextLevel;
    private int score;
    private int comboCount;      // jumlah PERFECT berturut-turut
    private int perfectCount;
    private int goodCount;
    private int missCount;

    // ── Constructor ──────────────────────────────────────────
    public Player(String name) {
        super(name, 100, 10, 2);
        this.xp            = 0;
        this.xpToNextLevel = 100;
        this.score         = 0;
        this.comboCount    = 0;
        this.perfectCount  = 0;
        this.goodCount     = 0;
        this.missCount     = 0;
    }

    // ── Override attack()
    @Override
    public void attack() {
        System.out.println(getName() + " melepas panah! Damage: " + getAttack());
    }

    // ── Overload attack(String type)
    public void attack(String type) {
        switch (type) {
            case "PERFECT":
                System.out.println("PERFECT! " + getName() + " menembak tepat sasaran! Damage: " + (getAttack() * 2));
                break;
            case "GOOD":
                System.out.println("GOOD! " + getName() + " mengenai musuh! Damage: " + getAttack());
                break;
            case "MISS":
                System.out.println("MISS! Panah " + getName() + " meleset!");
                break;
        }
    }

    // ── Override takeDamage() ────────────────────────────────
    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        comboCount = 0; // combo reset saat kena damage
        System.out.println(getName() + " terkena serangan! HP tersisa: " + getHp());
    }

    // ── Sistem XP & Leveling ─────────────────────────────────
    public void gainXP(int amount) {
        this.xp += amount;
        System.out.println("+" + amount + " XP! Total: " + xp + "/" + xpToNextLevel);
        if (this.xp >= this.xpToNextLevel) {
            levelUp();
        }
    }

    public void levelUp() {
        if (getLevel() >= 5) {
            System.out.println("Sudah Level MAX (5)!");
            return;
        }
        setLevel(getLevel() + 1);
        this.xp = 0;

        // Bonus stat per level
        switch (getLevel()) {
            case 2: setAttack(getAttack() + 5);  setMaxHp(getMaxHp() + 10); break;
            case 3: setAttack(getAttack() + 10); setMaxHp(getMaxHp() + 15); break;
            case 4: setAttack(getAttack() + 15); setMaxHp(getMaxHp() + 20); break;
            case 5: setAttack(getAttack() + 25); setMaxHp(getMaxHp() + 30); break;
        }

        // XP yang dibutuhkan level berikutnya
        int[] xpTable = {0, 100, 250, 450, 700};
        if (getLevel() < 5) {
            this.xpToNextLevel = xpTable[getLevel()];
        }

        // HP full saat naik level
        setHp(getMaxHp());
        System.out.println("LEVEL UP! Sekarang Level " + getLevel());
        System.out.println("Attack: " + getAttack() + " | Max HP: " + getMaxHp());
        manager.SoundManager.getInstance().playSFX("levelup");
    }

    // ── Sistem Combo ─────────────────────────────────────────
    public void checkCombo(String result) {
        if (result.equals("PERFECT")) {
            comboCount++;
            perfectCount++;
            score += 50 * comboCount; // bonus skor per combo

            // Adrenaline Heal setiap 3x combo PERFECT
            if (comboCount % 3 == 0) {
                activatePassive();
            }
        } else if (result.equals("GOOD")) {
            comboCount = 0;
            goodCount++;
            score += 10;
        } else {
            comboCount = 0;
            missCount++;
        }
    }

    // ── Implementasi Skillable ───────────────────────────────
    @Override
    public void activatePassive() {
        // Adrenaline Heal: heal +15 setiap combo 3x
        healHp(15);
        System.out.println("ADRENALINE HEAL! +" + 15 + " HP. HP sekarang: " + getHp());
    }

    @Override
    public String getPassiveInfo() {
        return "Combo Perfect: damage x combo | Adrenaline Heal (combo 3x): +15 HP";
    }

    // ── Override getStatus() ─────────────────────────────────
    @Override
    public String getStatus() {
        return "[ARCHER] " + getName() +
               " | HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | LVL: " + getLevel() +
               " | XP: " + xp + "/" + xpToNextLevel +
               " | Score: " + score +
               " | Combo: " + comboCount;
    }

    // ── Getters ──────────────────────────────────────────────
    public int getXp()           { return xp; }
    public int getXpToNextLevel(){ return xpToNextLevel; }
    public int getScore()        { return score; }
    public int getComboCount()   { return comboCount; }
    public int getPerfectCount() { return perfectCount; }
    public int getGoodCount()    { return goodCount; }
    public int getMissCount()    { return missCount; }

    public void addScore(int amount) {
        this.score += amount;
    }

    // ── Performa untuk sistem ending ─────────────────────────
    public String getPerformanceGrade() {
        int total = perfectCount + goodCount + missCount;
        if (total == 0) return "N/A";
        double perfectRate = (double) perfectCount / total * 100;
        if (score >= 1500)    return "TRUE_ENDING";
        else                  return "NORMAL_ENDING";
    }
}
