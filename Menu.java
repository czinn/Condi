/** Displays a menu. Designed to be displayed on whole screen, but can be displayed on part of screen.
  * 
  * @author Charles Zinn
  */
public class Menu {
  String[] options;
  int select;
  
  Menu(String[] options) {
    this.options = options;
    select = 0;
  }
}