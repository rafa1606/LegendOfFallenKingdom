package enemies;

import interfaces.Skillable;

public class Devil extends Enemy implements Skillable {

    private int hpRegen; // HP regen per pantulan

    public Devil() {
        super("Devil", 150, 40, 8,
              500, 0,
              2, 1.2f,
              0.15f, 0.25f); // merah = 70%
        this.hpRegen = 2;
    }

    // ── Override attack() ─────────────────────────────────────
    @Override
    public void attack() {
        System.out.println("Devil menyerang dengan kekuatan iblis! Damage: " + getAttack());
    }

    // ── Overload attack(String skill) ────────────────────────
    public void attack(String skill) {
        switch (skill) {
            case "CURSE":
                System.out.println("Devil melancarkan KUTUKAN! Damage: " + (getAttack() * 1.5));
                break;
            case "HELLFIRE":
                System.out.println("Devil melempar HELLFIRE! Damage: " + (getAttack() * 2));
                break;
            default:
                attack();
        }
    }

    // ── Overload attack(int dmg, boolean regen) ───────────────
    public void attack(int extraDmg, boolean withRegen) {
        System.out.println("Devil menyerang dengan " + extraDmg + " damage tambahan!");
        if (withRegen) regenHp();
    }

    // ── Override triggerPhase2() — Devil tidak punya phase 2 ──
    @Override
    public void triggerPhase2() {
        System.out.println("Devil tidak memiliki phase 2 — boss final!");
    }

    // ── HP Regen pasif per pantulan ───────────────────────────
    public void regenHp() {
        healHp(hpRegen);
        System.out.println("Devil regenerasi HP! +" + hpRegen + " HP. HP: " + getHp());
    }

    // ── Implementasi Skillable ────────────────────────────────
    @Override
    public void activatePassive() {
        regenHp();
    }

    @Override
    public String getPassiveInfo() {
        return "HP Regen: +" + hpRegen + " HP setiap pantulan";
    }

    // ── Getter ────────────────────────────────────────────────
    public int getHpRegen() { return hpRegen; }

    @Override
    public String getStatus() {
        return "[DEVIL - BOSS] HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | HP Regen: +" + hpRegen + "/bounce" +
               " | Zone H/K/M: " + (int)(getGreenZone()*100) + "%" +
               "/" + (int)(getYellowZone()*100) + "%" +
               "/" + (int)(getRedZone()*100) + "%";
    }
}
