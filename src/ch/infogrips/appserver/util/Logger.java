package ch.infogrips.appserver.util;

import java.io.*;
import java.util.*;

public interface Logger
{
   
   /*--------------------------------------------------------------------------------*/

   public void setModule(String module);
	public String getModule();

   public void setNestLevel(int level);
   public int getNestLevel();
   public void incNestLevel();
   public void decNestLevel();

   /*--------------------------------------------------------------------------------*/

   public void clear();

   public void debug(String message);
   public void message(String message);
   public void warning(String message);
   public void error(String message);
   public void error(String message,Exception e);
   public void syserr(String message);
   public void syserr(String message,String email);
   public void syserr(String message,Exception e);
   public void syserr(String message,String email,Exception e);
   public void security(String message);

   /*--------------------------------------------------------------------------------*/

   public Vector getLogTail();

   /*--------------------------------------------------------------------------------*/
   
}
