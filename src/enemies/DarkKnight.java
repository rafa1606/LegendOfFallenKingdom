package enemies;

public class DarkKnight extends Enemy {

    private int poisonDamage; // damage racun per pantulan

    public DarkKnight() {
        super("Dark Knight", 60, 20, 5,
              250, 0,
              3, 0.9f,
              0.20f, 0.30f); // merah = 50%
        this.poisonDamage = 0;
    }

    @Override
    public void attack() {
        System.out.println("Dark Knight menyerang dengan serangan gelap! Damage: " + getAttack());
    }

    @Override
    public void triggerPhase2() {
        super.triggerPhase2();
        setMaxHp(40); setHp(40);
        setAttack(30); setDefense(8);
        setBounceCount(2); setBounceInterval(0.8f);
        setGreenZone(0.15f); setYellowZone(0.25f); // merah = 60%
        setXpReward(300); setHealReward(50);
        this.poisonDamage = 2; // racun aktif

        System.out.println("Dark Knight PHASE 2: Poison aktif! -2 HP per pantulan");
        System.out.println("[Visual] Pedang cahaya hijau, kabut hijau/ungu di kaki");
    }

    // Pasif Phase 2
    public int getPoisonDamage() { return poisonDamage; }

    public void applyPoison(abstract_.Character target) {
        if (getPhase() == 2 && poisonDamage > 0) {
            target.takeDamage(poisonDamage);
            System.out.println("[POISON] " + target.getName() +
                               " terkena racun! -" + poisonDamage + " HP");
        }
    }

    @Override
    public String getStatus() {
        return "[DARK KNIGHT] HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | Phase: " + getPhase() +
               " | Poison: " + poisonDamage + "/bounce" +
               " | Zone H/K/M: " + (int)(getGreenZone()*100) + "%" +
               "/" + (int)(getYellowZone()*100) + "%" +
               "/" + (int)(getRedZone()*100) + "%";
    }
}
