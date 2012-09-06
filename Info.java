import java.util.*;
import java.io.*;

/** Contains info about items, monsters, and adjectives loaded from config files */
public class Info {
  //Each file contains similar structure and data types, so we can load them all the same
  //No two items/monsters/adjectives have the same name; references are fine
  HashMap<String, HashMap<String, String>> stats; //some of the stats may be integers, but still stored as strings
  HashMap<String, Vector<String>> tags; //may be tags for the item, or tags usable by the monster/adjective; same dif
  HashMap<String, Vector<Effect>> effects; //adjectives only; list of effects
  
  Info() {
    stats = new HashMap<String, HashMap<String, String>>();
    tags = new HashMap<String, Vector<String>>();
    effects = new HashMap<String, Vector<Effect>>();
    try {
      loadFile("weapons.txt", "weapon");
      loadFile("armor.txt", "armor");
      loadFile("monsters.txt", "monster");
      loadFile("adjs.txt", "adj");
    } catch(Exception e) {
      System.out.println("Something went wrong with loading stuff.");
      e.printStackTrace();
    }
  }
  
  private void loadFile(String fn, String tp) throws Exception {
    Scanner s = new Scanner(new FileInputStream(fn));
    //load file into memory... might be a bad idea, but it's k
    String f = "";
    while(s.hasNext())
      f += s.nextLine().trim();
    
    String[] obj = f.split(";"); //split at delimiting ;s
    for(int i = 0; i < obj.length; i++) {
      if(!obj[i].equals("")) {
        String[] o = obj[i].split("="); //split at the = separating the name of the thing from the data
        String name = o[0];
        String[] data = o[1].split(","); //commas separate the data sections
        //Make some variables to store the stats/tags/effects for this thing (effects only if loading from "adjs.txt")
        HashMap<String, String> stat = new HashMap<String, String>();
        Vector<String> tag = new Vector<String>();
        Vector<Effect> effect = tp.equals("adj") ? new Vector<Effect>() : null;
        //Loop through the pieces of data
        for(int j = 0; j < data.length; j++) {
          String[] blah = data[j].split(":"); //separates the data type from the data itself
          if(blah[0].equals("tags")) {
            //divide blah[1] by "+" and then put it into "tags"
            String[] tagstring = blah[1].split("[+]");
            for(int k = 0; k < tagstring.length; k++) {
              tag.add(tagstring[k]);
            }
          } else if(blah[0].equals("effect")) {
            effect.add(new Effect(Integer.parseInt(blah[1]), blah[2], Integer.parseInt(blah[3]), Integer.parseInt(blah[4])));
          } else {
            stat.put(blah[0], blah[1]);
          }
        }
        //Add the variables where the stats have been stored to the actual global variable things
        stats.put(name, stat);
        tags.put(name, tag);
        effects.put(name, effect);
      }
    }
  }
}