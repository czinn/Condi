import javax.swing.*;
import java.awt.*;
import java.io.*;
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
  
  public static void main(String[] args) {
    Game g = new Game();
  }
  
  Game() {
    super("Roguelike");
    p = new TextPanel(50, 80);
    this.add(p);
    this.pack();
    this.addKeyListener(this);
    this.setVisible(true);
    
    //Init game varibles
    message = "";
    messageTime = 0;
    messageCol = new CharCol();
    
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
      p.drawBox(' ', new CharCol(Color.GRAY, Color.GRAY), 0, 0, 48, 80);
      p.drawBox(' ', new CharCol(Color.GRAY, Color.GRAY), 47, 0, 3, 80);
      
      //Draw the message if there is one, and tick down message timer
      if(messageTime > 0) {
        p.drawString(message, messageCol, 48, 3);
        messageTime--;
      }
      
      //Paint stuff
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
  }
  
  /** Handle the key-released event */
  public void keyReleased(KeyEvent e) {
    
  }
}