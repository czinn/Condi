import java.util.*;

/** Represents an item, either on the map or in a Unit's inventory */
public class Item {
  Info info; //a reference to the info
  String name;
  String adj; //null if no adj (half of weapons have no adj)
  int row;
  int col;
  
  Item(Info info, String n) {
    this.info = info;
    row = 0;
    col = 0;
    String[] s = n.split("[+]");
    if(s.length == 2) {
      adj = s[0];
      name = s[1];
    } else {
      adj = null;
      name = s[0];
    }
  }
  
  Item(Info info, String tp, Vector<String> tags) { //chooses a random weapon from the available tags and of this type
    this.info = info;
    row = 0;
    col = 0;
    Vector<String> possible = info.listOf(tp); 
    Vector<String> goodones = new Vector<String>();
    for(String key : possible) {
      Vector<String> keytags = info.tags.get(key);
      //make sure it has at least one similar tag and it has no tags that begin with -
      boolean good = true;
      boolean hasatag = false;
      for(String tag : tags) {
        if(tag.charAt(0) == '-') {
          tag = tag.substring(1);
          for(String othertag : keytags) {
            if(tag.equals(othertag)) {
              good = false;
              break;
            }
          }
        } else {
          for(String othertag : keytags) {
            if(tag.equals(othertag))
              hasatag = true;
          }
        }
      }
      if(good && hasatag) {
       goodones.add(key);
      }
    }
    //Randomly select from goodones
    name = goodones.get(Game.rand(0, goodones.size()));
    //Choose an adjective randomly of ones that fit if random chance
    adj = null;
    if(tp.equals("weapon") && Game.rand(0, 5) == 0) {
      possible = info.listOf("adj"); 
      goodones = new Vector<String>();
      for(String key : possible) { //for each adjective
        Vector<String> keytags = info.tags.get(key); //get tags for the adjective
        //make sure this weapon has at least one tag in common with the adjective and none of the -
        boolean good = true;
        boolean hasatag = false;
        for(String tag : keytags) { //for each of the adjective tags
          if(tag.charAt(0) == '-') { //if it starts with -
            tag = tag.substring(1); //trim
            for(String othertag : info.tags.get(name)) { //for each tag for this weapon
              if(tag.equals(othertag)) { //if we find that tag
                good = false; //this is a bad adjective and it should feel bad
                break;
              }
            }
          } else { //it didn't start with a -
            for(String othertag : info.tags.get(name)) { //for each adjective in this weapon
              if(tag.equals(othertag)) { //if they match 
                hasatag = true; //yay! we have something in common!
              }
            }
          }
        }
        if(good && hasatag) {
          goodones.add(key);
        }
      }
      if(goodones.size() > 0) {
        adj = goodones.get(Game.rand(0, goodones.size()));
      }
    }
  }
  
  public String toString() {
    if(adj != null)
      return adj + " " + name;
    else
      return name;
  }
  
  public String getSaveName() {
    if(adj != null)
      return adj + "+" + name;
    else
      return name;
  }
  
  public String getSlot() {
    if(info.types.get(name).equals("armor"))
      return info.stats.get(name).get("slot");
    else
      return "weapon";
  }
  
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }
  
  public void setRow(int row) {
    this.row = row;
  }
  public void setCol(int col) {
    this.col = col;
  }
  
  public int getMass() {
    int mass = 0;
    if(info.stats.get(name).containsKey("mass"))
      mass += Integer.parseInt(info.stats.get(name).get("mass"));
    if(adj != null && info.stats.get(adj).containsKey("mass"))
      mass += Integer.parseInt(info.stats.get(adj).get("mass"));
    return mass;
  }
  
  public int getRange() {
    int range = 0;
    if(info.stats.get(name).containsKey("range"))
      range += Integer.parseInt(info.stats.get(name).get("range"));
    if(adj != null && info.stats.get(adj).containsKey("range"))
      range += Integer.parseInt(info.stats.get(adj).get("range"));
    return range;
  }
  
  public int getAttack() {
    int attack = 0;
    if(info.stats.get(name).containsKey("attack"))
      attack += Integer.parseInt(info.stats.get(name).get("attack"));
    if(adj != null && info.stats.get(adj).containsKey("attack"))
      attack += Integer.parseInt(info.stats.get(adj).get("attack"));
    return attack;
  }
  
  public int getASpeed() {
    int aspeed = 0;
    if(info.stats.get(name).containsKey("aspeed"))
      aspeed += Integer.parseInt(info.stats.get(name).get("aspeed"));
    if(adj != null && info.stats.get(adj).containsKey("aspeed"))
      aspeed += Integer.parseInt(info.stats.get(adj).get("aspeed"));
    return aspeed;
  }
  
  public int getDamage() {
    int damage = 0;
    if(info.stats.get(name).containsKey("damage"))
      damage += Integer.parseInt(info.stats.get(name).get("damage"));
    if(adj != null && info.stats.get(adj).containsKey("damage"))
      damage += Integer.parseInt(info.stats.get(adj).get("damage"));
    return damage;
  }
  
  public int getDefense() {
    int defense = 0;
    if(info.stats.get(name).containsKey("defense"))
      defense += Integer.parseInt(info.stats.get(name).get("defense"));
    if(adj != null && info.stats.get(adj).containsKey("defense"))
      defense += Integer.parseInt(info.stats.get(adj).get("defense"));
    return defense;
  }
  
  public int getSpeed() {
    int speed = 0;
    if(info.stats.get(name).containsKey("speed"))
      speed += Integer.parseInt(info.stats.get(name).get("speed"));
    if(adj != null && info.stats.get(adj).containsKey("speed"))
      speed += Integer.parseInt(info.stats.get(adj).get("speed"));
    return speed;
  }
  
  //Returns a random effect from possible effects, null if no effect happens this attack
  public Effect getEffect() {
    if(adj == null)
      return null;
    
    Vector<Effect> possible = new Vector<Effect>();
    for(Effect e : info.effects.get(adj)) {
      if(Game.rand(0, 100) < e.chance)
        possible.add(e);
    }
    
    if(possible.size() == 0)
      return null;
    
    return possible.get(Game.rand(0, possible.size())).copy();
  }
}