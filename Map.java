import java.awt.*;

/** Contains map data
  * This includes references to all entities
  * items, and other stuff probably
  * 
  * @author Charles Zinn
  */
public class Map {
  Tile[][] tiles;
  
  Map(int rows, int cols) {
    tiles = new Tile[rows][cols];
    for(int i = 0; i < rows; i++) {
      for(int j = 0; j < cols; j++) {
        tiles[i][j] = new Tile(Game.rand(0, 10) == 0 ? Tile.WALL : Tile.FLOOR);
      }
    }
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
  
  /** Checks for lines of sight between two tiles using Bresenham's line algorithm */
  public boolean sight(int r0, int c0, int r1, int c1) {
    if(isOpaque(r0, c0)) return false;
    
    //Comment line to allow infinite sight
    //Uncomment to only allow sight within radius of 15
    if((r1 - r0) * (r1 - r0) + (c1 - c0) * (c1 - c0) > 15 * 15) return false;
    
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
    
    return true;
  }
  
  /** Checks whether the tile at (row, col) is opaque */
  public boolean isOpaque(int row, int col) {
    return getTile(row, col).isOpaque();
  }
  
  /** Draws part of the map, with the top left corner being positioned at (row, col)
    * The tile drawn at that position is the tile (srow, scol)
    * The box is (height, width) big
    * Things not visible from (vr, vc) are greyed out; eventually they may not be drawn at all or something idk
    */
  public void draw(TextPanel p, int row, int col, int srow, int scol, int height, int width, int vr, int vc) {
    for(int r = 0; r < height; r++) {
      for(int c = 0; c < width; c++) {
        Tile t = getTile(srow + r, scol + c);
        char tc = t.getChar();
        CharCol tcc = sight(vr, vc, srow + r, scol + c) ? t.getCol() : new CharCol(Color.WHITE, Color.GRAY);
        p.drawChar(tc, tcc, row + r, col + c);
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
  
  static int FLOOR = 0;
  static int WALL = 1;
  
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
  
  public char getChar() {
    return Tile.getChar(type);
  }
  
  public static char getChar(int type) {
    if(type == FLOOR)
      return '.';
    if(type == WALL)
      return '#';
    
    return '?';
  }
  
  public CharCol getCol() {
    return Tile.getCol(type);
  }
  
  public static CharCol getCol(int type) {
    if(type == FLOOR)
      return new CharCol();
    if(type == WALL)
      return new CharCol();
    
    return new CharCol();
  }
  
  public boolean isOpaque() {
    return Tile.isOpaque(type);
  }
  
  public static boolean isOpaque(int type) {
    if(type == FLOOR) return false;
    if(type == WALL) return true;
    
    return false;
  }
}