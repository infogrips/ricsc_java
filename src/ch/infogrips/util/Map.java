package ch.infogrips.util;

import java.util.*;

public class Map extends Hashtable
{

   /*--------------------------------------------------------------------------------*/

   private Vector keys = null;

   /*--------------------------------------------------------------------------------*/

   public Map()
   {
      super();
      keys = new Vector(this.size());
   }

   /*--------------------------------------------------------------------------------*/

   public Map(int capacity)
   {
      super(capacity);
      keys = new Vector(capacity);
   }

   /*--------------------------------------------------------------------------------*/

   public Map(int capacity,float loadfactor)
   {
      super(capacity,loadfactor);
      keys = new Vector(capacity);
   }

   /*--------------------------------------------------------------------------------*/

   public void clear()
   {
      super.clear();
      keys.removeAllElements();
   }

   /*--------------------------------------------------------------------------------*/

/*   public Object clone()
   {
      Map m = new Map(this);
      m.keys = keys.clone();
      return m;
   }
*/

   /*--------------------------------------------------------------------------------*/

   public synchronized Enumeration keys()
   {
      return keys.elements();
   }

   /*--------------------------------------------------------------------------------*/

   public synchronized Object put(Object key,Object value)
   {
      for (int i=0;i<keys.size();i++) {
         if (keys.elementAt(i).equals(key)) {
            return super.put(key,value);
         }
      }
      keys.addElement(key);
      return super.put(key,value);
   }

   /*--------------------------------------------------------------------------------*/

   public synchronized Object remove(Object key)
   {
      for (int i=0;i<keys.size();i++) {
         if (keys.elementAt(i).equals(key)) {
            keys.removeElement(key);
            return super.remove(key);
         }
      }
      return null;
   }

   /*--------------------------------------------------------------------------------*/

   public String toString()
   {
      String result = "{";
      for (int i=0;i<keys.size();i++) {
         String key = (String)keys.elementAt(i);
         result += key + "=" + get(key);
         if (i<keys.size()-1) {
            result += "|";
         }
      }
      return result + "}";
   }

   /*--------------------------------------------------------------------------------*/

}
