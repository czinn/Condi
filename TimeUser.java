/** All entities (units and effects) that use time extend this class */
public class TimeUser {
  int time; //game ticks (ms) until next move
  boolean waiting; //whether or not the unit is basically skipping its turn
  
  TimeUser() {
    time = 0;
    waiting = false;
  }
  
  public void addTime(int length) {
    time += length;
  }
  
  public void passTime(int length) {
    time -= length;
    if(time < 0)
      time = 0;
  }
  
  public boolean isReady() {
    return getTime() <= 0;
  }
  
  public boolean isWaiting() {
    return waiting;
  }
  
  public void setWaiting(boolean waiting) {
    this.waiting = waiting;
  }
  
  public int getTime() {
    return time;
  }
  
  /** Does nothing by default, should be overridden by all objects except for player */
  public void takeTurn() {
    //nothing
  }
}