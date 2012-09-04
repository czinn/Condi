import java.awt.*;

/** Contains information about a Monster */
public class Monster extends Unit {
  
  Monster(int level, int row, int col, Map map) {
    super(level, row, col, map);
  }
  
  //The monster's turn has come, it should perform an action (like moving or attacking)
  public void takeTurn() {
    //For now, move around randomly
    move(Game.rand(-1, 2), Game.rand(-1, 2));
  }
  
  /** Returns the character that should be used to represent the player */
  public char getChar() {
    return '&';
  }
  
  /** Returns the character colour that should be used to represent the player */
  public CharCol getCharCol() {
    return new CharCol(Color.GREEN);
  }
}