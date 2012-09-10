import java.awt.*;

/** Contains information about the player */
public class Player extends Unit {
  int xp;
  int surges;
  
  Player(int row, int col, Map map, Info info) {
    super(0, row, col, map, info);
    
    surges = 0;
  }
  
  /** Player is super fast for testing */
  public int getSpeed() {
    return 1000;
  }
  
  /** This still needs to account for items and other bonuses */
  public int getMaxHealth() {
    return 1000 + 50 * level;
  }
  
  /** Returns the character that should be used to represent the player */
  public char getChar() {
    return '@';
  }
  
  /** Returns the character colour that should be used to represent the player */
  public CharCol getCharCol() {
    return new CharCol(Color.RED);
  }
  
  public void giveXp(int xp) {
    if(level < 100) {
      this.xp += xp;
      if(this.xp >= Player.xpLevel(getLevel())) {
        this.xp -= Player.xpLevel(getLevel());
        setLevel(getLevel() + 1);
        info.g.postMessage("Level up! You are now level " + getLevel(), new CharCol(Color.GREEN));
      }
    } else {
      xp = 0;
    }
  }
  public int getXp() {
    return xp;
  }
  
  public void spendSurge() {
    if(surges > 0) {
      if(getHealth() < getMaxHealth()) {
        heal(getMaxHealth() - getHealth());
        surges--;
        info.g.postMessage("Healed to full health.", new CharCol(Color.GREEN));
      } else
        info.g.postMessage("You're already at full health.", new CharCol(Color.GREEN));
    } else {
      info.g.postMessage("You're out of health surges.", new CharCol(Color.RED));
    }
  }
  
  public void setSurges(int s) {
    surges = s;
  }
  
  public static int xpLevel(int lev) {
    return (int)Math.round(100 * Math.pow(2, lev / 20.0));
  }
}