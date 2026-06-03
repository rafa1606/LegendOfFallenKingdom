package abstract_;

public abstract class Character {

    //  Atribut
    private String name;
    private int    hp;
    private int    maxHp;
    private int    attack;
    private int    defense;
    private int    level;

    //  Constructor
    public Character(String name, int hp, int attack, int defense) {
        this.name    = name;
        this.hp      = hp;
        this.maxHp   = hp;
        this.attack  = attack;
        this.defense = defense;
        this.level   = 1;
    }

    // Abstract methods
    public abstract void attack();
    public abstract String getStatus();

    //  Concrete methods
    public void takeDamage(int dmg) {
        int damage = dmg - this.defense;
        if (damage < 0) damage = 0;
        this.hp -= damage;
        if (this.hp < 0) this.hp = 0;
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public void healHp(int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) this.hp = this.maxHp;
    }

    //  Getters
    public String getName()    { return name;    }
    public int    getHp()      { return hp;      }
    public int    getMaxHp()   { return maxHp;   }
    public int    getAttack()  { return attack;  }
    public int    getDefense() { return defense; }
    public int    getLevel()   { return level;   }

    //  Setters
    public void setName(String name)       { this.name = name; }
    public void setHp(int hp)              { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public void setMaxHp(int maxHp)        { if (maxHp > 0) this.maxHp = maxHp; }
    public void setAttack(int attack)      { if (attack >= 0) this.attack = attack; }
    public void setDefense(int defense)    { if (defense >= 0) this.defense = defense; }
    public void setLevel(int level)        { if (level > 0) this.level = level; }
}
