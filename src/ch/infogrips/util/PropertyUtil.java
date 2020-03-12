package ch.infogrips.util;

import java.lang.*;
import java.util.*;

public class PropertyUtil 
{

   /*---------------------------------------------------------------------*/

   public static void setProperty(String pname,String pvalue)
   {
	   Properties p = System.getProperties();
	   p.put(pname,pvalue);
	   System.setProperties(p);
   }

   /*---------------------------------------------------------------------*/
	
}
