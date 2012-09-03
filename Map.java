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
        tiles[i][j] = new Tile(Game.rand(0, 5) == 0 ? Tile.WALL : Tile.FLOOR);
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
  
  /** Draws part of the map, with the top left corner being positioned at (row, col)
    * The tile drawn at that position is the tile (srow, scol)
    * The box is (height, width) big
    * 
    * This is currently drawn without vision in mind; that will be changed soon
    */
  public void draw(TextPanel p, int row, int col, int srow, int scol, int height, int width) {
    for(int r = 0; r < height; r++) {
      for(int c = 0; c < width; c++) {
        Tile t = getTile(srow + r, scol + c);
        char tc = t.getChar();
        CharCol tcc = t.getCol();
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
}