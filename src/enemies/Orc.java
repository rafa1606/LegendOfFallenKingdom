package enemies;

public class Orc extends Enemy {

    public Orc() {
        super("Orc", 80, 15, 2,
              150, 0,
              3, 1.2f,
              0.25f, 0.35f); // merah = 40%
    }

    @Override
    public void attack() {
        System.out.println("Orc menyerang dengan tenaga besar! Damage: " + getAttack());
    }

    @Override
    public void triggerPhase2() {
        super.triggerPhase2();
        setMaxHp(50); setHp(50);
        setAttack(25); setDefense(5);
        setBounceCount(3); setBounceInterval(0.8f);
        setGreenZone(0.20f); setYellowZone(0.30f); // merah = 50%
        setXpReward(200); setHealReward(40);

        System.out.println("Orc PHASE 2: Damage non-PERFECT dialihkan ke serangan Orc!");
        System.out.println("[Visual] Badan membesar, aura merah/orange, tekstur batu/besi");
    }

    // ── Pasif Phase 2: Damage transfer ───────────────────────
    // Dipanggil di BattleManager saat result != PERFECT
    public int getDamageTransfer(int playerDamage) {
        if (getPhase() == 2 && isPassiveActive()) {
            int transferred = playerDamage / 2;
            System.out.println("Damage transfer! Orc mendapat +" + transferred + " ATK sementara");
            return transferred;
        }
        return 0;
    }

    @Override
    public String getStatus() {
        return "[ORC] HP: " + getHp() + "/" + getMaxHp() +
               " | ATK: " + getAttack() +
               " | Phase: " + getPhase() +
               " | Zone H/K/M: " + (int)(getGreenZone()*100) + "%" +
               "/" + (int)(getYellowZone()*100) + "%" +
               "/" + (int)(getRedZone()*100) + "%";
    }
}
