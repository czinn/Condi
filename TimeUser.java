/** All entities (units and effects) that use time extend this class */
public class TimeUser {
  int time; //game ticks (ms) until next move
  
  TimeUser() {
    time = 0;
  }
  
  public void addTime(int length) {
    time += length;
  }
  
  public void passTime(int length) {
    time -= length;
  }
  
  public boolean isReady() {
    return getTime() <= 0;
  }
  
  public int getTime() {
    return time;
  }
  
  /** Does nothing by default, should be overridden by all objects except for player */
  public void takeTurn() {
    //nothing
  }
}