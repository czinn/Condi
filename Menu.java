import java.awt.*;

/** Displays a menu. Designed to be displayed on whole screen, but can be displayed on part of screen.
  * 
  * @author Charles Zinn
  */
public class Menu {
  String[] options;
  int select;
  boolean active;
  
  Menu(String[] options) {
    this.options = options;
    select = 0;
    active = false;
  }
  
  /** Draws the menu
    * It must have at least (options * 2) + 3 rows and (longest option length) + 6 cols
    * Drawing of smaller menus may be improved later
    */
  public void draw(TextPanel p, CharCol c, int row, int col, int height, int width) {
    //Ensure size is big enough
    if(height >= numOptions() * 2 + 3 && width >= longOption() + 6) {
      //Draw border in gray
      p.drawBox(' ', new CharCol(Color.GRAY, Color.GRAY), row, col, height, width);
      //Draw each menu element
      for(int i = 0; i < numOptions(); i++) {
        String op = getOption(i);
        p.drawString(op, c, row + 2 + i * 2, col + 4);
      }
      //Draw the selector
      p.drawChar('>', c, row + 2 + select * 2, col + 2);
    }
  }
  
  public boolean isActive() {
    return active;
  }
  
  public int numOptions() {
    return options.length;
  }
  
  public String getOption(int n) {
    return options[n];
  }
  
  /** Moves the selection down one if possible */
  public void selectDown() {
    if(select < numOptions() - 1)
      select++;
  }
  
  /** Moves the select up one if possible */
  public void selectUp() {
    if(select > 0)
      select--;
  }
  
  /** Sets the menu as active or not */
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public int getSelect() {
    return select;
  }
  
  /** Returns the length of the longest option */
  public int longOption() {
    int longest = 0;
    for(int i = 0; i < numOptions(); i++) {
      int size = getOption(i).length();
      if(size > longest)
        size = longest;
    }
    return longest;
  }
}