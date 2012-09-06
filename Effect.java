/** An effect that can be applied to a Unit */
public class Effect extends TimeUser {
  String type;
  int durationLeft;
  int power;
  int chance;
  
  Unit victim;
  
  Effect(int chance, String type, int duration, int power) {
    this.chance = chance;
    this.type = type;
    this.durationLeft = duration;
    this.power = power;
    
    victim = null;
  }
  
  public void applyEffect(Unit u) {
    victim = u;
  }
  
  public void takeTurn() {
    //Do something based on effect
  }
}