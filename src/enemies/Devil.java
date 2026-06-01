package enemies;

import interfaces.Skillable;

public class Devil extends Enemy implements Skillable {

    private int hpRegen; // HP regen per pantulan

    public Devil() {
        super("Devil", 250, 50, 10,
                500, 0,
                2, 0.6f,         // lebih cepat dari 1.2f
                0.05f, 0.10f);   // zona hijau 5%, kuning 10%, merah 85%
        this.hpRegen = 8;      // regen lebih besar dari 5
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

    // ── HP Regen pasif per pantulan ───────────────────────────
    public void regenHp() {
        healHp(hpRegen);
        System.out.println("Devil regenerasi HP! +" + hpRegen + " HP. HP: " + getHp());
    }

    @Override
    public void triggerPhase2() {
        setPhase(2);
        setPassiveActive(true);
        setMaxHp(150);
        setHp(150);
        setAttack(60);
        setBounceCount(2);
        setBounceInterval(0.5f);  // sangat cepat
        setGreenZone(0.3f);      // zona hijau hanya 5%!
        setYellowZone(0.7f);     // zona kuning 10%
        setXpReward(1000);
        this.hpRegen = 10;        // regen makin besar
        System.out.println("DEVIL PHASE 2! Kekuatan penuh iblis terlepas!");
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
