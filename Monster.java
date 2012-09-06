import java.awt.*;

/** Contains information about a Monster */
public class Monster extends Unit {
  int type;
  
  Monster(int level, int row, int col, Map map) {
    super(level, row, col, map);
    type = 0;
  }
  
  //The monster's turn has come, it should perform an action (like moving or attacking)
  public void takeTurn() {
    //For now, move around randomly
    move(Game.rand(-1, 2), Game.rand(-1, 2));
  }
  
  /** Returns the name of the monster */
  public String getName() {
    return Monster.getName(type);
  }
  
  public static String getName(int type) {
    if(type == 0)
      return "A Monster";
    
    return "You don't know what this is.";
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