import java.util.*;

/** Contains a collection of items currently stored on a Unit
  * It also keeps track of which are held in a "backpack" and which are in use
  * If the unit is dual-wielding (player, monsters don't do this), then only
  * the bonuses and stats from the current weapon in use are counted
  * The advantage from dual wielding stems from being able to switch which weapon
  * one is using instantly
  */
public class Inventory {
  public static int MAX_MASS = 25;
  
  Vector<Item> items;
  Vector<Item> backpack; //inactive items
  
  Inventory() {
    items = new Vector<Item>();
    backpack = new Vector<Item>();
  }
  
  /** Returns whether there is space for an item */
  public boolean spaceFor(Item i) {
    return getMass() + i.getMass() <= MAX_MASS;
  }
  
  /** Attempts to add an item. Returns false if the item adding failed (too heavy) */
  public boolean addItem(Item i) {
    if(spaceFor(i)) {
      //Figure out whether to add to items or backpack (add to items by default)
      if(slotUse(i.getSlot()))
        backpack.add(i);
      else
        items.add(i);
      return true;
    } else
      return false;
  }
  
  /** Determines whether an item is already weilded in this slot */
  public boolean slotUse(String slot) {
    for(Item i : items) {
      if(i.getSlot().equals(slot))
        return true;
    }
    return false;
  }
  
  /** Gets the thing in the given slot */
  public Item getSlot(String slot) {
    for(Item i : items) {
      if(i.getSlot().equals(slot))
        return i;
    }
    return null;
  }
  
  /** Equips the given item (it must be in the backpack) */
  public void equipItem(Item i) {
    if(backpack.contains(i)) {
      backpack.remove(i);
      if(getSlot(i.getSlot()) != null) {
        unequipItem(getSlot(i.getSlot()));
      }
      items.add(i);
    }
  }
  
  /** Unequips the given item (it must be equipped [obviously]) */
  public void unequipItem(Item i) {
    if(items.contains(i)) {
      items.remove(i);
      backpack.add(i);
    }
  }
  
  /** Gets the effect of the active weapon */
  public Effect getEffect() {
    if(slotUse("weapon"))
      return getSlot("weapon").getEffect();
    return null;
  }
  
  public int getMass() {
    int val = 0;
    for(Item i : items) val += i.getMass();
    for(Item i : backpack) val += i.getMass();
    return val;
  }
  
  public int getRange() {
    int val = 0;
    for(Item i : items) val += i.getRange();
    return val;
  }
  
  public int getAttack() {
    int val = 0;
    for(Item i : items) val += i.getAttack();
    return val;
  }
  
  public int getASpeed() {
    int val = 0;
    for(Item i : items) val += i.getASpeed();
    return val;
  }
  
  public int getDamage() {
    int val = 0;
    for(Item i : items) val += i.getDamage();
    return val;
  }
  
  public int getDefense() {
    int val = 0;
    for(Item i : items) val += i.getDefense();
    return val;
  }
  
  public int getSpeed() {
    int val = 0;
    for(Item i : items) val += i.getSpeed();
    return val;
  }
  
  public int getSpeedMassDebuff() {
    return 30 * getMass();
  }
}