import java.awt.*;
import java.util.*;

/** Contains information about a Monster */
public class Monster extends Unit {
  String type;
  
  int playerRow;
  int playerCol;
  
  Monster(int level, int row, int col, Map map, Info info) {
    super(level, row, col, map, info);
    playerRow = -1;
    playerCol = -1;
    //Choose a monster of the correct level
    Vector<String> possible = info.listOf("monster");
    Vector<String> goodones = new Vector<String>();
    for(String m : possible) {
      int lolev = Integer.parseInt(info.stats.get(m).get("lolev"));
      int hilev = Integer.parseInt(info.stats.get(m).get("hilev"));
      if(lolev <= level && level <= hilev)
        goodones.add(m);
    }
    type = goodones.get(Game.rand(0, goodones.size()));
    
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
        setWaiting(true);
      }
    } else {
      setWaiting(true);
    }
  }
  
  public String getName() {
    return type;
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