import java.awt.*;

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
  
  public Effect copy() {
    Effect e = new Effect(chance, type, durationLeft, power);
    return e;
  }
  
  public void applyEffect(Unit u) {
    victim = u;
  }
  
  public void takeTurn() {
    //Do something based on effect (only turn based effects, other effects applied in stat calculation)
    if(type.equals("stun")) {
      victim.addTime(durationLeft - victim.getTime());
    } else if(type.equals("slow")) {
      
    } else if(type.equals("poison")) {
      victim.damage(power);
    } else if(type.equals("fire")) {
      victim.damage(power);
    } else if(type.equals("weaken")) {
      
    } else if(type.equals("confuse")) {
      
    } else if(type.equals("snare")) {
      
    } else if(type.equals("bleed")) {
      victim.damage(power);
    } else if(type.equals("daze")) {
      
    }
    
    durationLeft -= 500;
    addTime(500);
  }
  
  public Color getColor() {
    if(type.equals("stun")) {
      return Color.GRAY;
    } else if(type.equals("slow")) {
      return Color.BLUE;
    } else if(type.equals("poison")) {
      return Color.GREEN;
    } else if(type.equals("fire")) {
      return Color.RED;
    } else if(type.equals("weaken")) {
      return Color.CYAN;
    } else if(type.equals("confuse")) {
      return Color.LIGHT_GRAY;
    } else if(type.equals("snare")) {
      return Color.ORANGE;
    } else if(type.equals("bleed")) {
      return Color.PINK;
    } else if(type.equals("daze")) {
      return Color.YELLOW;
    }
    return Color.BLACK;
  }
  
  public String getEnglish() {
    if(type.equals("stun")) {
      return "Stunned";
    } else if(type.equals("slow")) {
      return "Slowed";
    } else if(type.equals("poison")) {
      return "Poisoned";
    } else if(type.equals("fire")) {
      return "Enflamed";
    } else if(type.equals("weaken")) {
      return "Weakened";
    } else if(type.equals("confuse")) {
      return "Confused";
    } else if(type.equals("snare")) {
      return "Snared";
    } else if(type.equals("bleed")) {
      return "Bleeding";
    } else if(type.equals("daze")) {
      return "Dazed";
    }
    return "Something";
  }
}