import java.awt.*;
import java.util.Vector;

/** Contains map data
  * This includes references to all entities
  * items, and other stuff probably
  * 
  * @author Charles Zinn
  */
public class Map {
  Tile[][] tiles;
  Vector<TimeUser> tus;
  Vector<Item> items; //that have been dropped
  
  Info info;
  
  static int SPLIT_MIN = 20;
  static int MIN_ROOM_SIZE = 8;
  
  Map(int rows, int cols, Info info) {
    this.info = info;
    
    tiles = new Tile[rows][cols];
    clear();
    
    generateDungeon(0, 0, rows, cols);
  }
  
  public void clear() {
    tiles = new Tile[getRows()][getCols()];
    for(int i = 0; i < getRows(); i++) {
      for(int j = 0; j < getCols(); j++) {
        tiles[i][j] = new Tile(Tile.WALL);
      }
    }
    
    //Empty the TimeUser array
    tus = new Vector<TimeUser>();
    //Empty the items array
    items = new Vector<Item>();
  }
  
  public int getRows() {
    return tiles.length;
  }
  
  public int getCols() {
    return tiles[0].length;
  }
  
  public Tile getTile(int row, int col) {
    return tiles[row][col];
  }
  
  /** Recursively generates a dungeon map inside the given box
    * Map should be clear()ed beforehand
    */
  public void generateDungeon(int row, int col, int height, int width) {
    //At least one dimension must be over SPLIT_MIN (dimensions under SPLIT_MIN should not be split)
    if(height >= SPLIT_MIN || width >= SPLIT_MIN) {
      boolean vertSplit = Game.rand(0, 2) == 0; //whether the seperation line is vertical or horizontal (random)
      if(height < SPLIT_MIN) vertSplit = true; //must split vertical if less than 30 tall
      if(width < SPLIT_MIN) vertSplit = false; //must split horizontal if less than 30 wide
      
      if(vertSplit) { //splitting vertically
        int splitLine = Game.rand(MIN_ROOM_SIZE, width - MIN_ROOM_SIZE);
        generateDungeon(row, col, height, splitLine);
        generateDungeon(row, col + splitLine, height, width - splitLine);
        //connect them
        int pathPos = Game.rand(MIN_ROOM_SIZE / 2, height - (MIN_ROOM_SIZE / 2)); //somewhere where there is definitely room on both sides
        int colIndex = col + splitLine;
        while(getTile(pathPos + row, colIndex).getType() == Tile.WALL) {
          getTile(pathPos + row, colIndex).setType(Tile.FLOOR);
          colIndex++;
        }
        colIndex = col + splitLine - 1;
        while(getTile(pathPos + row, colIndex).getType() == Tile.WALL) {
          getTile(pathPos + row, colIndex).setType(Tile.FLOOR);
          colIndex--;
        }
      } else { //splitting horizontally
        int splitLine = Game.rand(MIN_ROOM_SIZE, height - MIN_ROOM_SIZE);
        generateDungeon(row, col, splitLine, width);
        generateDungeon(row + splitLine, col, height - splitLine, width);
        //connect them
        int pathPos = Game.rand(MIN_ROOM_SIZE / 2, width - (MIN_ROOM_SIZE / 2)); //somewhere where there is definitely room on both sides
        int rowIndex = row + splitLine;
        while(getTile(rowIndex, pathPos + col).getType() == Tile.WALL) {
          getTile(rowIndex, pathPos + col).setType(Tile.FLOOR);
          rowIndex++;
        }
        rowIndex = row + splitLine - 1;
        while(getTile(rowIndex, pathPos + col).getType() == Tile.WALL) {
          getTile(rowIndex, pathPos + col).setType(Tile.FLOOR);
          rowIndex--;
        }
      }
    } else {
      //Create a single room inside this box
      int upad = Game.rand(1, MIN_ROOM_SIZE / 2);
      int dpad = Game.rand(1, MIN_ROOM_SIZE / 2);
      int lpad = Game.rand(1, MIN_ROOM_SIZE / 2);
      int rpad = Game.rand(1, MIN_ROOM_SIZE / 2);
      for(int r = row + upad; r < row + height - dpad; r++) {
        for(int c = col + lpad; c < col + width - rpad; c++) {
          getTile(r, c).setType(Tile.FLOOR);
        }
      }
      //Put extra features in rooms to make them less boring
      int rmheight = height - upad - dpad;
      int rmwidth = width - lpad - rpad;
      int rmrow = row + upad;
      int rmcol = col + lpad;
      //Add pillars if room is big enough
      if(rmwidth > 10 && rmheight > 10) {
        getTile(rmrow + 3, rmcol + 3).setType(Tile.PILLAR);
        getTile(rmrow + 3, rmcol + rmwidth - 4).setType(Tile.PILLAR);
        getTile(rmrow + rmheight - 4, rmcol + rmwidth - 4).setType(Tile.PILLAR);
        getTile(rmrow + rmheight - 4, rmcol + 3).setType(Tile.PILLAR);
      }
      //Spawn monsters and/or loot in the room (depending on size)
      //For now, spawn one monster right in the middle
      spawnMonster(0, rmrow + rmheight / 2, rmcol + rmwidth / 2);
    }
  }
  
  
  /** Checks for lines of sight between two tiles using Bresenham's line algorithm */
  public boolean sight(int r0, int c0, int r1, int c1) {
    if(isOpaque(r0, c0)) return false;
    
    //Comment line to allow infinite sight
    //Uncomment to only allow sight within radius of 15
    if((r1 - r0) * (r1 - r0) + (c1 - c0) * (c1 - c0) > 15 * 15) return false;
    
    if(!isOpaque(r1, c1)) { //target is floor
      boolean steep = (int)Math.abs(r1 - r0) > (int)Math.abs(c1 - c0);
      if(steep) {
        int temp = r0;
        r0 = c0;
        c0 = temp;
        temp = r1;
        r1 = c1;
        c1 = temp;
      }
      if(c0 > c1) {
        int temp = c0;
        c0 = c1;
        c1 = temp;
        temp = r0;
        r0 = r1;
        r1 = temp;
      }
      int dc = c1 - c0;
      int dr = (int)Math.abs(r1 - r0);
      
      int d = 2 * dr - dc;
      int r = r0;
      int rstep = -1;
      if(r0 < r1) rstep = 1;
      
      for(int c = c0 + 1; c < c1; c++) {
        if(d > 0) {
          r += rstep;
          if(steep) {
            if(isOpaque(c, r)) return false;
          } else {
            if(isOpaque(r, c)) return false;
          }
          d = d + (2 * dr - 2 * dc);
        } else {
          if(steep) {
            if(isOpaque(c, r)) return false;
          } else {
            if(isOpaque(r, c)) return false;
          }
          d = d + (2 * dr);
        }
      }
    } else {
      //for walls, check vision of each of the 8 tiles around it
      for(int i = -1; i <= 1; i++) {
        for(int j = -1; j <= 1; j++) {
          if(r1 + i >= 0 && r1 + i < getRows() && c1 + j >= 0 && c1 + j < getCols()) { //not looking outside borders
            if(!isOpaque(r1 + i, c1 + j) && sight(r0, c0, r1 + i, c1 + j))
              return true;
          }
        }
      }
      return false;
    }
    return true;
  }
  
  /** Checks whether the tile at (row, col) is opaque */
  public boolean isOpaque(int row, int col) {
    return getTile(row, col).isOpaque();
  }
  
  /** Gets a reference to the player (first player object in tus) */
  public Player getPlayer() {
    for(TimeUser t : tus) {
      if(t instanceof Player)
        return (Player)t;
    }
    
    //No player object
    return null;
  }
  
  /** Spawns a player into the map (only works if there is no player in the map currently (there can only be one) */
  public void spawnPlayer(Player p) {
    if(getPlayer() == null)
      tus.add(p);
  }
  
  /** Spawns a random monster on the map at the given position */
  public void spawnMonster(int level, int row, int col) {
    tus.add(new Monster(level, row, col, this, info));
  }
  
  /** Passes time for all TimeUsers equal to the smallest time among them.
    * Asks the first TimeUser for a move (unless that TimeUser is a Player)
    * Returns the length of time passed */
  public int passTime() {
    TimeUser smallUser = null;
    for(TimeUser t : tus) {
      if((smallUser == null || t.getTime() < smallUser.getTime()) && !t.isWaiting()) {
        smallUser = t;
      }
    }
    
    //If there weren't any TimeUsers? just to prevent some random error that could happen possibly maybe
    if(smallUser == null)
      return 0;
    
    int timePass = smallUser.getTime(); //can't keep using smallUser.getTime() because he'll have his time passed also
    
    //Pass time for all users, and make moves if they reached 0
    for(TimeUser t : tus) {
      t.setWaiting(false); //only waits for one turn
      t.passTime(timePass);
      if(t.isReady() && !(t instanceof Player)) {
        t.takeTurn();
      }
    }
    
    //Return time passed
    return timePass;
  }
  
  /** If waiting for the player to make a move, returns true */
  public boolean waitingForPlayer() {
    return getPlayer().isReady() && !getPlayer().isWaiting();
  }
  
  /** Returns the unit on the given tile, or null otherwise */
  public Unit getLocationUnit(int row, int col) {
    for(TimeUser t : tus) {
      if(t instanceof Unit) {
        if(((Unit)t).getRow() == row && ((Unit)t).getCol() == col)
          return (Unit)t;
      }
    }
    return null;
  }
  
  /** Removes dead units */
  public void updateDead() {
    Vector<TimeUser> deadThings = new Vector<TimeUser>();
    for(TimeUser t : tus) {
      if(t instanceof Unit) {
        Unit u = (Unit)t;
        if(u.isDead()) {
          if(u instanceof Monster) {
            deadThings.add(t);
            //Drop loot
            if(Game.rand(0, 5) == 0) {
              //Choose a random piece of loot from the inventory
              Item loot = u.getInv().items.get(Game.rand(0, u.getInv().items.size()));
              dropItem(loot, u.getRow(), u.getCol());
            }
            continue;
          } else { //it's a player
            //Deal with game loss here somehow, or mark the map as lost, idk
            //For now, heal to full hp
            u.heal(u.getMaxHealth() - u.getHealth());
          }
        }
      }
    }
    for(TimeUser t : deadThings) {
      tus.remove(t);
    }
  }
  
  /** Drops/spawns the given item at the given location */
  public void dropItem(Item i, int row, int col) {
    items.add(i);
    i.setRow(row);
    i.setCol(col);
  }
  
  /** Finds the item at the given location */
  public Item getLocationItem(int row, int col) {
    for(Item i : items) {
      if(i.getRow() == row && i.getCol() == col)
        return i;
    }
    return null;
  }
  
  /** Picks up (removes from the map) the item at location and returns it */
  public Item pickupItem(int row, int col) {
    Item i = getLocationItem(row, col);
    if(i != null) {
      items.remove(i);
      return i;
    } else
      return null;
  }
  
  /** Draws part of the map, with the top left corner being positioned at (row, col)
    * The tile drawn at that position is the tile (srow, scol)
    * The box is (height, width) big
    * Things not visible from (vr, vc) are greyed out
    */
  public void draw(TextPanel p, int row, int col, int srow, int scol, int height, int width, int vr, int vc) {
    for(int r = 0; r < height; r++) {
      for(int c = 0; c < width; c++) {
        boolean visible = sight(vr, vc, srow + r, scol + c);
        Tile t = getTile(srow + r, scol + c);
        if(!t.hasSeen() && visible) t.see();
        char tc = visible || t.hasSeen() ? t.getChar() : ' ';
        CharCol tcc = visible ? t.getCol() : new CharCol(Color.WHITE, Color.GRAY);
        p.drawChar(tc, tcc, row + r, col + c);
      }
    }
    
    //Draw items if they are visible
    for(Item i : items) {
      if(sight(vr, vc, i.getRow(), i.getCol())) {
        p.drawChar('$', new CharCol(Color.YELLOW), row + i.getRow() - srow, col + i.getCol() - scol);
      }
    }
    
    //Draw units if they are visible
    for(TimeUser t : tus) {
      if(t instanceof Unit) {
        Unit u = (Unit)t;
        if(sight(vr, vc, u.getRow(), u.getCol()))
          p.drawChar(u.getChar(), u.getCharCol(), row + u.getRow() - srow, col + u.getCol() - scol);
      }
    }
  }
}

/** Contains details about a single tile
  * Its type and "state" (open or closed, etc.)
  * 
  * @author Charles Zinn
  */
class Tile {
  int type;
  int data;
  
  boolean seen;
  
  static int FLOOR = 0;
  static int WALL = 1;
  static int PILLAR = 2;
  
  Tile(int type) {
    this(type, 0);
  }
  
  Tile(int type, int data) {
    this.type = type;
    this.data = data;
  }
  
  public int getType() {
    return type;
  }
  
  public int getData() {
    return data;
  }
  
  public void setType(int type) {
    this.type = type;
    data = 0; //remove data; different types of tiles have different meanings for values, should clear by default
  }
  
  public void setData(int data) {
    this.data = data;
  }
  
  public char getChar() {
    return Tile.getChar(type);
  }
  
  public static char getChar(int type) {
    if(type == FLOOR)
      return '.';
    if(type == WALL)
      return '#';
    if(type == PILLAR)
      return 'O';
    
    return '?';
  }
  
  public CharCol getCol() {
    return Tile.getCol(type);
  }
  
  public static CharCol getCol(int type) {
    if(type == FLOOR)
      return new CharCol();
    if(type == WALL)
      return new CharCol(Color.BLUE);
    if(type == PILLAR)
      return new CharCol();
    
    return new CharCol();
  }
  
  public boolean isOpaque() {
    return Tile.isOpaque(type);
  }
  
  public static boolean isOpaque(int type) {
    if(type == FLOOR) return false;
    if(type == WALL) return true;
    if(type == PILLAR) return true;
    
    return false;
  }
  
  public boolean isWalkable() {
    return Tile.isWalkable(type);
  }
  
  public static boolean isWalkable(int type) {
    if(type == FLOOR) return true;
    if(type == WALL) return false;
    if(type == PILLAR) return false;
    
    return true;
  }
  
  public boolean hasSeen() {
    return seen;
  }
  
  public void see() {
    seen = true;
  }
}