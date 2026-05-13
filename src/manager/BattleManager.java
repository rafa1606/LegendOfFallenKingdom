package manager;

import characters.Player;
import enemies.*;

public class BattleManager {

    private Player  player;
    private Enemy   currentEnemy;
    private String  currentZone;
    private boolean isBattleOver;
    private String  battleResult;

    public BattleManager(Player player) {
        this.player       = player;
        this.isBattleOver = false;
        this.battleResult = "";
    }

    public void setCurrentEnemy(Enemy enemy) {
        this.currentEnemy = enemy;
        this.isBattleOver = false;
        System.out.println("\n=== BATTLE START: " + enemy.getName() + " ===");
    }

    public void processInput(String zone) {
        if (isBattleOver || currentEnemy == null) return;
        this.currentZone = zone;

        String result;
        if (zone.equals("GREEN"))       result = "PERFECT";
        else if (zone.equals("YELLOW")) result = "GOOD";
        else                            result = "MISS";

        System.out.println("[INPUT] Zona: " + zone + " → " + result);

        int damage = calculateDamage(result);
        player.attack(result);
        player.checkCombo(result);

        if (!result.equals("MISS")) {
            applyDamageToEnemy(damage, result);
            player.gainXP(result.equals("PERFECT")
                    ? 20 * Math.min(player.getComboCount(), 3) : 10);
        } else {
            enemyCounterAttack();
        }

        checkResult();
    }

    public int calculateDamage(String result) {
        switch (result) {
            case "PERFECT":
                int combo = Math.min(player.getComboCount(), 3);
                return player.getAttack() * 2 * Math.max(1, combo);
            case "GOOD":
                return player.getAttack();
            default:
                return 0;
        }
    }

    public int calculateDamage(int baseDmg) {
        return baseDmg;
    }

    private void applyDamageToEnemy(int damage, String result) {
        // Pasif Orc Phase 2
        if (currentEnemy instanceof Orc && result.equals("GOOD")) {
            Orc orc = (Orc) currentEnemy;
            int transferred = orc.getDamageTransfer(damage);
            damage -= transferred;
        }

        currentEnemy.takeDamage(damage);
        System.out.println(currentEnemy.getName() +
                " HP: " + currentEnemy.getHp() + "/" + currentEnemy.getMaxHp());

        checkPhaseTransition();
    }

    private void enemyCounterAttack() {
        currentEnemy.attack();
        currentEnemy.aiAction(player);

        // Devil regen saat player MISS
        if (currentEnemy instanceof Devil) {
            ((Devil) currentEnemy).regenHp();
        }

        // Poison DarkKnight
        if (currentEnemy instanceof DarkKnight) {
            ((DarkKnight) currentEnemy).applyPoison(player);
        }

        System.out.println("Player HP: " + player.getHp() + "/" + player.getMaxHp());
    }

    public void checkPhaseTransition() {
        if (currentEnemy.getPhase() == 1 && !currentEnemy.isAlive()) {
            currentEnemy.triggerPhase2();
            System.out.println(currentEnemy.getName() +
                    " Phase 2! HP: " + currentEnemy.getHp());
        }
    }

    public void checkResult() {
        if (!player.isAlive()) {
            isBattleOver = true;
            battleResult = "LOSE";
            System.out.println("\n=== GAME OVER ===");
            return;
        }

        // Musuh biasa — phase 2 habis
        if (!currentEnemy.isAlive()
                && currentEnemy.getPhase() == 2
                && !(currentEnemy instanceof Devil)) {
            isBattleOver = true;
            battleResult = "WIN";
            int[] reward = currentEnemy.dropReward();
            player.gainXP(reward[0]);
            if (reward[1] > 0) player.healHp(reward[1]);
            player.addScore(reward[0] * 10);
            System.out.println("\n=== " + currentEnemy.getName() + " DIKALAHKAN! ===");
            return;
        }

        // Devil — langsung mati tanpa phase 2
        if (!currentEnemy.isAlive() && currentEnemy instanceof Devil) {
            isBattleOver = true;
            battleResult = "WIN";
            int[] reward = currentEnemy.dropReward();
            player.gainXP(reward[0]);
            player.addScore(reward[0] * 10);
            System.out.println("\n=== DEVIL DIKALAHKAN! ===");
            return;
        }
    }

    // ── Getters ──────────────────────────────────────────────
    public boolean isBattleOver()    { return isBattleOver;  }
    public String  getBattleResult() { return battleResult;  }
    public String  getCurrentZone()  { return currentZone;   }
    public Enemy   getCurrentEnemy() { return currentEnemy;  }
    public Player  getPlayer()       { return player;        }
}