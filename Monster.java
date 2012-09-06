import java.awt.*;

/** Contains information about a Monster */
public class Monster extends Unit {
  int type;
  
  int playerRow;
  int playerCol;
  
  Monster(int level, int row, int col, Map map) {
    super(level, row, col, map);
    type = 0;
    playerRow = -1;
    playerCol = -1;
  }
  
  //The monster's turn has come, it should perform an action (like moving or attacking)
  public void takeTurn() {
    //Update the location of the player if we can see it
    if(getMap().sight(getRow(), getCol(), getMap().getPlayer().getRow(), getMap().getPlayer().getCol())) {
      playerRow = getMap().getPlayer().getRow();
      playerCol = getMap().getPlayer().getCol();
    }
    
    //Move towards the player or don't move at all
    if(playerRow != -1) {
      int rowChange = 0;
      int colChange = 0;
      if(playerRow > getRow()) rowChange = 1;
      if(playerRow < getRow()) rowChange = -1;
      if(playerCol > getCol()) colChange = 1;
      if(playerCol < getCol()) colChange = -1;
      int oldRow = getRow();
      int oldCol = getCol();
      move(rowChange, colChange);
      if(getRow() == oldRow && getCol() == oldCol) {
        addTime(1000);
      }
    } else {
      addTime(1000);
    }
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