import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

/** Main class for Condi
 * 
 * @author Charles Zinn
 */
public class Game extends JFrame implements KeyListener {
  public static int FPS = 60;
  
  TextPanel p;
  
  String message;
  CharCol messageCol;
  int messageTime;
  
  int gs;
  int sgs;
  static int GS_MAIN_MENU = 0;
  static int GS_GAME = 1;
  static int GS_SELECT_SAVE = 2;
  
  Menu menuMain;
  Menu menuInvEquip;
  Menu menuInvBag;
  Menu menuEscape;
  Menu menuSaveSelect;
  
  Map dungeon;
  Map vault;
  
  Player player;
  
  int passTimeWait;
  
  int currow; //cursor row (on the map, not the screen)
  int curcol; //cursor col (on the map, not the screen)
  int selectType; //what sort of selection is happening
  static int SELECT_NONE = 0;
  static int SELECT_LOOK = 1;
  static int SELECT_ATTACK = 2;
  static int SELECT_INV = 3;
  static int SELECT_ESCAPE = 4;
  
  int scrollrow;
  int scrollcol;
  
  Info info;
  
  String gameName; //a randomly generated name for the current game (also name of save file)
  
  boolean inDungeon;
  
  public static void main(String[] args) {
    Game g = new Game();
  }
  
  Game() {
    super("Condi");
    p = new TextPanel(40, 80);
    this.add(p);
    this.pack();
    this.addKeyListener(this);
    this.setVisible(true);
    
    //Init game varibles
    message = "";
    messageTime = 0;
    messageCol = new CharCol();
    
    gs = GS_MAIN_MENU;
    sgs = 0;
    
    passTimeWait = 0;
    
    currow = 0;
    curcol = 0;
    selectType = SELECT_NONE;
    
    scrollrow = 0;
    scrollcol = 0;
    
    inDungeon = false;
    
    //Load the info!
    info = new Info();
    
    //Init menus
    menuMain = new Menu(new String[]{"New Game", "Continue Game", "test", "four", "wut", "Debug Option"});
    menuMain.setActive(true);
    menuInvEquip = new Menu(new String[]{"Empty"});
    menuInvBag = new Menu(new String[]{"Empty"});
    menuEscape = new Menu(new String[]{"Empty"});
    menuEscape.setActive(true);
    menuSaveSelect = new Menu(new String[]{"Empty"});
    menuSaveSelect.setActive(true);
    
    //Start the game
    run();
  }
  
  /** Main game loop */
  public void run() {
    boolean doLoop = true;
    long curTime;
    while(doLoop) {
      //Start the frame timer
      curTime = System.currentTimeMillis();
      
      //Clear drawing surface
      p.clear();
      
      //Draw a border, with a spot at the bottom for messages
      p.drawBox(' ', new CharCol(Color.GRAY, Color.GRAY), 0, 0, 38, 80);
      p.drawBox(' ', new CharCol(Color.GRAY, Color.GRAY), 37, 0, 3, 80);

      
      //Draw the message if there is one, and tick down message timer
      if(messageTime > 0) {
        p.drawString(message, messageCol, 38, 3);
        messageTime--;
      }      
      
      if(gs == GS_MAIN_MENU) {
        //Draw the menu
        menuMain.draw(p, new CharCol(), 0, 0, 38, 80);
      } else if(gs == GS_SELECT_SAVE) {
        //Draw the menu
        menuSaveSelect.draw(p, new CharCol(), 0, 0, 38, 80);
      } else if(gs == GS_GAME) {
        /* Does time well
        //Tick down passTimeWait if it's more than 0; if it is 0, make a move if we're not waiting for the player
        if(passTimeWait > 0)
          passTimeWait = (int)Math.max(passTimeWait - 1000 / FPS, 0);
        if(passTimeWait == 0 && !test.waitingForPlayer()) {
          passTimeWait = test.passTime();
        }*/
        
        //Does time fast
        //if(!test.waitingForPlayer())
        //  test.passTime();
        
        //Does time really fast
        while(!currentMap().waitingForPlayer())
         currentMap().passTime();
        
        //Figure out the width and height of the map display
        int mapDisplayHeight = 36;
        int mapDisplayWidth = 78;
        if(selectType == SELECT_LOOK)
          mapDisplayWidth = 39;
        
        //Scroll the map to the player if not selecting anything, or to the cursor if something like that is doing
        if(selectType == SELECT_NONE) {
          rowScroll(player.getRow(), mapDisplayHeight);
          colScroll(player.getCol(), mapDisplayWidth);
        } else if(selectType == SELECT_LOOK || selectType == SELECT_ATTACK) {
          rowScroll(currow, mapDisplayHeight);
          colScroll(curcol, mapDisplayWidth);
        }
        
        //Update the map elements (kill stuff)
        currentMap().updateDead();
        
        //Draw the map
        currentMap().draw(p, 1, 1, 0 + scrollrow, 0 + scrollcol, mapDisplayHeight, mapDisplayWidth, player.getRow(), player.getCol());
        
        //Draw a cursor if one should be drawn
        if(selectType == SELECT_ATTACK || selectType == SELECT_LOOK) {
          if(Game.flash())
            p.drawChar('X', new CharCol(Color.YELLOW), 1 + currow - scrollrow, 1 + curcol - scrollcol);
        }
        
        //Draw the look information screen
        if(selectType == SELECT_LOOK) {
          //Lines should be drawn at column 41, and starting at row 3
          //First, figure out of there are units on this tile
          if(currentMap().sight(player.getRow(), player.getCol(), currow, curcol)) {
            Unit here = currentMap().getLocationUnit(currow, curcol);
            if(here != null) {
              if(here instanceof Player) {
                //It's the player! Do a 'status screen'
                p.drawString("Player", 3, 41);
                p.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 5, 41);
              } else {
                //It's a monster... draw its name and some stuff about it
                Monster mon = (Monster)here;
                p.drawString(mon.getName(), 3, 41);
                p.drawString("Health: " + mon.getHealth() + "/" + mon.getMaxHealth(), 5, 41);
                p.drawString("Weapon: " + mon.getInv().getSlot("weapon"), 7, 41);
                p.drawString("Head: " + (mon.getInv().slotUse("head") ? mon.getInv().getSlot("head") : "None"), 8, 41);
                p.drawString("Body: " + (mon.getInv().slotUse("body") ? mon.getInv().getSlot("body") : "None"), 9, 41);
                p.drawString("Legs: " + (mon.getInv().slotUse("legs") ? mon.getInv().getSlot("legs") : "None"), 10, 41);
                p.drawString("Feet: " + (mon.getInv().slotUse("feet") ? mon.getInv().getSlot("feet") : "None"), 11, 41);
              }
            }
            //Look for an item
            Item onTile = currentMap().getLocationItem(currow, curcol);
            if(onTile != null) {
              p.drawString(onTile.toString(), here == null ? 3 : 20, 41);
            }
          }
        } else if(selectType == SELECT_INV) {
          menuInvEquip.draw(p, new CharCol(), 0, 0, 38, 40);
          menuInvBag.draw(p, new CharCol(), 0, 40, 38, 40);
          p.drawString("Equipped", new CharCol(Color.WHITE, Color.GRAY), 0, 10);
          p.drawString("Bag", new CharCol(Color.WHITE, Color.GRAY), 0, 50);
          p.drawString("Enter: Equip/unequip item       D: Drop item", 36, 20);
        } else if(selectType == SELECT_ESCAPE) {
          menuEscape.draw(p, new CharCol(), 0, 0, 38, 80);
          p.drawString(WordGen.fCap(gameName), new CharCol(Color.RED), 2, 30);
        }
      }
        
      //Flip buffer and repaint
      p.flip();
      this.repaint();
      
      //End of loop
      //Do a terrible stall-timer loop thing to maintain FPS
      while(System.currentTimeMillis() - curTime < 1000 / FPS) {
        //Do nothing
        
      }
    }
  }
  
  /** Posts a message in the given colour to the message box at bottom of screen */
  public void postMessage(String m, CharCol c) {
    message = m;
    messageCol = c;
    messageTime = 5 * FPS;
  }
  
  /** Handle the key typed event */
  public void keyTyped(KeyEvent e) {
    //Do nothing
  }
  
  /** Handle the key-pressed event */
  public void keyPressed(KeyEvent e) {
    int k = e.getKeyCode();
    postMessage("Pressed " + k, new CharCol());
    
    if(gs == GS_MAIN_MENU) {
      if(k == 38) { //UP
        menuMain.selectUp();
      }
      if(k == 40) { //DOWN
        menuMain.selectDown();
      }
      if(k == 10) { //ENTER (select)
        int sel = menuMain.getSelect();
        if(sel == 0) { //"New Game"
          //Make the vault
          createVault();
          
          player = new Player(0, 0, currentMap(), info);
          
          //Spawn the player into the vault
          vault.spawnPlayer(player);
          
          inDungeon = false;
          
          //Make a random name
          gameName = WordGen.ss(2);
          
          gs = GS_GAME;
        } else if(sel == 1) { //"Continue Game"
          //Get a list of all the saves
          File folder = new File("saves");
          File[] fileList = folder.listFiles();
          Vector<String> saveFiles = new Vector<String>();
          for(int i = 0; i < fileList.length; i++) {
            if(fileList[i].isFile()) {
              String fn = fileList[i].getName();
              if(fn.endsWith(".txt")) {
                saveFiles.add(WordGen.fCap(fn.substring(0, fn.length() - 4)));
              }
            }
          }
          menuSaveSelect.updateOptions(saveFiles.toArray(new String[1]));
          if(saveFiles.size() > 0) {
            gs = GS_SELECT_SAVE;
          } else {
            postMessage("There aren't any saved games.", new CharCol(Color.RED));
          }
        }
      }
    } else if(gs == GS_SELECT_SAVE) {
      if(k == 38) { //UP
        menuSaveSelect.selectUp();
      }
      if(k == 40) { //DOWN
        menuSaveSelect.selectDown();
      }
      if(k == 10) { //ENTER
        String fn = menuSaveSelect.options.get(menuSaveSelect.getSelect());
        loadGame(fn.toLowerCase());
        gs = GS_GAME;
      }
      if(k == 27) {
        gs = GS_MAIN_MENU;
      }
    } else if(gs == GS_GAME) {
      if(selectType == SELECT_NONE) { //Moving around the map; essentially, not paused
        if(k == 37) { //LEFT
          if(player.isReady())
            player.move(0, -1);
        }
        if(k == 38) { //UP
          if(player.isReady())
            player.move(-1, 0);
        }
        if(k == 39) { //RIGHT
          if(player.isReady())
            player.move(0, 1);
        }
        if(k == 40) { //DOWN
          if(player.isReady())
            player.move(1, 0);
        }
        if(k == 36) { //HOME (NUMPAD UPLEFT)
          if(player.isReady())
            player.move(-1, -1);
        }
        if(k == 33) { //PAGEUP (NUMPAD UPRIGHT)
          if(player.isReady())
            player.move(-1, 1);
        }
        if(k == 35) { //END (NUMPAD DOWNLEFT)
          if(player.isReady())
            player.move(1, -1);
        }
        if(k == 34) { //PAGEDOWN (NUMPAD DOWNRIGHT)
          if(player.isReady())
            player.move(1, 1);
        }
        if(k == 46) { //PERIOD (WAIT)
          if(player.isReady())
            player.setWaiting(true);
        }
        if(k == 76) { //L (LOOK)
          selectType = SELECT_LOOK;
          currow = player.getRow();
          curcol = player.getCol();
        }
        if(k == 65) { //A (ATTACK)
          selectType = SELECT_ATTACK;
          currow = player.getRow();
          curcol = player.getCol();
        }
        if(k == 71) { //G (GET/PICKUP)
          //Check whether there is an item at player's location
          Item onTile = currentMap().getLocationItem(player.getRow(), player.getCol());
          if(onTile != null) {
            //If there is space in player inventory
            if(player.getInv().spaceFor(onTile)) {
              player.getInv().addItem(currentMap().pickupItem(player.getRow(), player.getCol()));
              postMessage("You got: " + onTile, new CharCol(Color.GREEN));
            } else {
              postMessage("You can't carry that!", new CharCol(Color.ORANGE));
            }
          }
        }
        if(k == 73) { //I (INVENTORY)
          selectType = SELECT_INV;
          menuInvEquip.setActive(true);
          menuInvBag.setActive(false);
          updateInventoryMenus();
        }
        if(k == 69) { //E (ENTER)
          Tile t = currentMap().getTile(player.getRow(), player.getCol());
          if(t.getType() == Tile.ENTER_DUNGEON) {
            //Create a dungeon at the player's level and move them into it
            dungeon = new Map(80, 80, info);
            dungeon.generateDungeon(player.getLevel(), 0, 0, 80, 80);
            player.changeMap(dungeon);
            dungeon.spawnPlayer(player);
            inDungeon = true;
          } else if(t.getType() == Tile.EXIT_DUNGEON) {
            //Move the player back into the vault
            player.changeMap(vault);
            vault.spawnPlayer(player);
            inDungeon = false;
          }
        }
        if(k == 27) { //ESCAPE (ESCAPE MENU)
          selectType = SELECT_ESCAPE;
          if(inDungeon) {
            menuEscape.updateOptions(new String[]{"Surrender to Death", "Return to Game"});
          } else {
            menuEscape.updateOptions(new String[]{"Save", "Return to Game"});
          }
        }
      } else { //In a menu or using a cursor of some sort; essentially, paused
        if(selectType == SELECT_LOOK || selectType == SELECT_ATTACK) { //cursor stuff
          if(k == 37) { //LEFT
            if(curcol > 0) curcol--;
          } 
          if(k == 38) { //UP
            if(currow > 0) currow--;
          }
          if(k == 39) { //RIGHT
            if(curcol < currentMap().getCols() - 1) curcol++;
          }
          if(k == 40) { //DOWN
            if(currow < currentMap().getRows() - 1) currow++;
          }
          if(k == 10) { //ENTER
            if(selectType == SELECT_ATTACK) {
              Unit u = currentMap().getLocationUnit(currow, curcol);
              if(u != null && !(u instanceof Player)) {
                player.attack(u);
                selectType = SELECT_NONE;
              }
            }
          }
          if(k == 27) { //ESCAPE
            selectType = SELECT_NONE;
          }
        } else if(selectType == SELECT_INV) { //in the inventory
          if(k == 27) { //ESCAPE
            selectType = SELECT_NONE;
          }
          if(k == 37) { //LEFT
            menuInvEquip.setActive(true);
            menuInvBag.setActive(false);
          }
          if(k == 38) { //UP
            if(menuInvEquip.isActive())
              menuInvEquip.selectUp();
            else
              menuInvBag.selectUp();
          }
          if(k == 39) { //RIGHT
            menuInvEquip.setActive(false);
            menuInvBag.setActive(true);
          }
          if(k == 40) { //DOWN
            if(menuInvEquip.isActive())
              menuInvEquip.selectDown();
            else
              menuInvBag.selectDown();
          }
          if(k == 10) { //ENTER
            if(menuInvEquip.isActive()) {
              if(menuInvEquip.options.size() > 0)
                player.getInv().unequipItem(player.getInv().items.get(menuInvEquip.getSelect()));
            } else {
              if(menuInvBag.options.size() > 0)
                player.getInv().equipItem(player.getInv().backpack.get(menuInvBag.getSelect()));
            }
            updateInventoryMenus();
          }
          if(k == 68) { //D (DROP)
            Item toDrop = null;
            if(menuInvEquip.isActive() && menuInvEquip.options.size() > 0) {
              toDrop = player.getInv().items.get(menuInvEquip.getSelect());
              player.getInv().items.remove(toDrop);
            } else if(menuInvBag.isActive() && menuInvBag.options.size() > 0) {
              toDrop = player.getInv().backpack.get(menuInvBag.getSelect());
              player.getInv().backpack.remove(toDrop);
            }
            if(toDrop != null) {
              currentMap().dropItem(toDrop, player.getRow(), player.getCol());
              postMessage("You dropped: " + toDrop, new CharCol(Color.ORANGE));
            }
            updateInventoryMenus();
          }
        } else if(selectType == SELECT_ESCAPE) { //In the escape menu
          if(k == 38) { //UP
            menuEscape.selectUp();
          }
          if(k == 40) { //DOWN
            menuEscape.selectDown();
          }
          if(k == 27) { //ESCAPE
            selectType = SELECT_NONE;
          }
          if(k == 10) { //ENTER
            if(menuEscape.getSelect() == 1)
              selectType = SELECT_NONE;
            else {
              if(inDungeon) {
                //Delete the save file
                //...
              } else {
                postMessage("Saving...", new CharCol());
                saveGame();
                postMessage("Save successful.", new CharCol());
              }
              gs = GS_MAIN_MENU;
              selectType = SELECT_NONE;
            }
          }
        }
      }
    }
  }
  
  /** Updates the inventory menus so that they reflect the current inventory of the player */
  public void updateInventoryMenus() {
    Inventory inv = player.getInv();
    String[] equip = new String[inv.items.size()];
    for(int i = 0; i < equip.length; i++) {
      equip[i] = inv.items.get(i).toString();
    }
    String[] bag = new String[inv.backpack.size()];
    for(int i = 0; i < bag.length; i++) {
      bag[i] = inv.backpack.get(i).toString();
    }
    menuInvEquip.updateOptions(equip);
    menuInvBag.updateOptions(bag);
  }
  
  /** Modifies "scrollrow" such that the row "r" can be seen on the map display which is "display" rows high */
  public void rowScroll(int r, int display) {
    //keep it 10 from any edge
    scrollrow = Math.min(scrollrow, r - 10);
    scrollrow = Math.max(scrollrow, r - display + 10);
    scrollrow = Math.max(scrollrow, 0);
    scrollrow = Math.min(scrollrow, currentMap().getRows() - display);
  }
  
  /** Modifies "scrollcol" such that the col "c" can be seen on the map display which is "display" rows wide */
  public void colScroll(int c, int display) {
    //keep it 10 from any edge
    scrollcol = Math.min(scrollcol, c - 10);
    scrollcol = Math.max(scrollcol, c - display + 10);
    scrollcol = Math.max(scrollcol, 0);
    scrollcol = Math.min(scrollcol, currentMap().getCols() - display);
  }

  /** Returns a reference to the current map */
  public Map currentMap() {
    if(inDungeon)
      return dungeon;
    else
      return vault;
  }
  
  /** Handle the key-released event */
  public void keyReleased(KeyEvent e) {
    
  }
  
  /** Generates a random integer value between 'a' and 'b' - 1 */
  public static int rand(int a, int b) {
    return (int)Math.floor(Math.random() * (b - a)) + a;
  }
  
  /** If something should be flashing on the display, it should be drawn whenever this is true and not when this is false */
  public static boolean flash() {
    return System.currentTimeMillis() % 800 < 400;
  }
  
  /** Creates an empty vault (created when a new game is started, or when a game is reloaded) */
  public void createVault() {
    vault = new Map(80, 80, info);
    for(int i = 0; i < vault.getRows(); i++) {
      for(int j = 0; j < vault.getCols(); j++) {
        if(i != 0 && i != vault.getRows() - 1 && j != 0 && j != vault.getCols() - 1)
          vault.getTile(i, j).setType(Tile.FLOOR);
      }
    }
    vault.getTile(vault.getRows() / 2, vault.getCols() / 2).setType(Tile.ENTER_DUNGEON);
  }
  
  /** Saves the game to the game file */
  public void saveGame() {
    try {
      PrintStream out = new PrintStream(new FileOutputStream("saves/" + gameName + ".txt"));
      //Sections are delimited by a new line
      //Sections must be in the right order or bad things will happen because bad things should happen when files
      // get corrupted
      //Section 1: Player level,player xp
      out.println(player.getLevel() + "," + player.getXp());
      //Section 2: Player equip inventory. Items separated by commas
      String inv = "";
      for(Item i : player.getInv().items) {
        inv += i.getSaveName() + ",";
      }
      System.out.println(inv);
      out.println(inv.substring(0, inv.length() - (inv.length() > 0 ? 1 : 0)));
      //Section 3: Player backpack. See Section 2 for format.
      inv = "";
      for(Item i : player.getInv().backpack) {
        inv += i.getSaveName() + ",";
      }
      System.out.println(inv);
      out.println(inv.substring(0, inv.length() - (inv.length() > 0 ? 1 : 0)));
      //Section 4: All items in the vault. Stored like "Item Name:row:col,"
      inv = "";
      for(Item i : vault.items) {
        inv += i.getSaveName() + ":" + i.getRow() + ":" + i.getCol() + ",";
      }
      System.out.println(inv);
      out.println(inv.substring(0, inv.length() - (inv.length() > 0 ? 1 : 0)));
      //Done
      out.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /** Loads the game from the game file */
  public void loadGame(String name) {
    gameName = name;
    try {
      Scanner s = new Scanner(new FileInputStream("saves/" + gameName + ".txt"));
      //Make the vault
      createVault();
      
      player = new Player(0, 0, currentMap(), info);
      
      //Load the player's level and xp
      String[] sec1 = s.nextLine().split(",");
      player.setLevel(Integer.parseInt(sec1[0]));
      player.setXp(Integer.parseInt(sec1[1]));
      
      //Load the player's equipment
      String[] sec2 = s.nextLine().split(",");
      for(int i = 0; i < sec2.length; i++) {
        if(!sec2[i].equals(""))
          player.getInv().items.add(new Item(info, sec2[i]));
      }
      
      //Load the player's backpack
      String[] sec3 = s.nextLine().split(",");
      for(int i = 0; i < sec3.length; i++) {
        if(!sec3[i].equals(""))
          player.getInv().backpack.add(new Item(info, sec3[i]));
      }
      
      //Load the items in the vault
      String[] sec4 = s.nextLine().split(",");
      for(int i = 0; i < sec4.length; i++) {
        if(!sec4[i].equals("")) {
          String[] it = sec4[i].split(":");
          vault.dropItem(new Item(info, it[0]), Integer.parseInt(it[1]), Integer.parseInt(it[2]));
        }
      }
      
      //Spawn the player into the vault
      vault.spawnPlayer(player);
      
      inDungeon = false;
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}