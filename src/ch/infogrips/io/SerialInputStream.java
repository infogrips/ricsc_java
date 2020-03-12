package ch.infogrips.io;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;

import ch.infogrips.util.*;

public class SerialInputStream {

   /*----------------------------------------------------------------------*/

   private InputStream i = null;
   private String buffer = null;
   private StreamTokenizer st = null;

   /*----------------------------------------------------------------------*/

   private void initTokenizer()
   {
      st = new StreamTokenizer(i);
      st.resetSyntax();
      st.whitespaceChars(0,32);
      st.wordChars(33,255);
      st.commentChar('!');
      st.quoteChar('\'');
      st.eolIsSignificant(false);
   }

   /*----------------------------------------------------------------------*/

   private SerialInputStream()
   {
   }

   /*----------------------------------------------------------------------*/

   public SerialInputStream(InputStream i)
   {
      this.i = i;
      initTokenizer();
   }

   /*----------------------------------------------------------------------*/

   public String readNextToken() 
   {
      try {
         if (st.nextToken() == StreamTokenizer.TT_EOF) {
            return null;
         }
         else {
            return st.sval;
         }
      }
      catch (Exception e) {
         return null;
      }
   }

   /*----------------------------------------------------------------------*/

   public void reReadNextToken() 
   {
      st.pushBack();
   }

   /*----------------------------------------------------------------------*/

   public byte[] readBytes()
   {
	   int length = Integer.parseInt(readNextToken());
		if (length == 0) {
			return null;
		}
		try {
		   byte b[] = new byte[length];
			int index = 0;
			while (length > 0) {
			   int len = i.read(b,index,length);
				index += len;
				length -= len;
		   }
		   return b;
		}
		catch (IOException e) {
         System.out.println("invalid byte array");
		   return null;
		}
   }

   /*----------------------------------------------------------------------*/

   public String readString()
   {
	   String value = readNextToken();
      if (value.equals("NULL")) {
		   return null;
		}
		else {
		   return value;
		}
   }

   /*----------------------------------------------------------------------*/

   public Integer readInt()
   {
      String value = readNextToken();
      if (value == null) {
         return null;
      }
		else if (value.equals("NULL")) {
		   return null;
		}
      else {
         return new Integer(value);
      }
   }

   /*----------------------------------------------------------------------*/

   public Long readLong()
   {
      String value = readNextToken();
      if (value == null) {
         return null;
      }
		else if (value.equals("NULL")) {
		   return null;
		}
      else {
         return new Long(value);
      }
   }

   /*----------------------------------------------------------------------*/

   public Boolean readBoolean()
   {
      String value = readNextToken();
      if (value == null) {
         return null;
      }
		else if (value.equals("NULL")) {
         return null;
		}
      else {
         return new Boolean(value);
      }
   }

   /*----------------------------------------------------------------------*/

   public Double readReal()
   {
      String value = readNextToken();
      try {
         if (value == null) {
            return null;
         }
			else if (value.equals("NULL")) {
			   return null;
			}
         else {
            return new Double(value);
         }
      }
      catch (Exception e) {
         System.out.println("invalid real value " + value);
         return null;
      }
   }

   /*----------------------------------------------------------------------*/

   public Vector readList()
   {

      Vector value = new Vector(20);
      String t;
 
      while (true) {

         t = readNextToken();
         if (t == null) {
            break;
         }
         else if (t.equals("}")) {
            break;
         }

         reReadNextToken();
         value.addElement(read());

      }

      return value;

   }
 
   /*----------------------------------------------------------------------*/

   public Hashtable readMap()
   {

      ch.infogrips.util.Map value = new ch.infogrips.util.Map(20);
      String t;
 
      while (true) {

         t = readNextToken();
         if (t == null) {
            break;
         }
         else if (t.equals("}")) {
            break;
         }
         
			Object o = read();
			if (o != null) {
			   // 2001.08.03 mg
            value.put(t,o);
			}

      }

      return (Hashtable)value;

   }
 
   /*----------------------------------------------------------------------*/

   private Object readObject(String type)
	{
	   try {
		   Class c = Thread.currentThread().getContextClassLoader().loadClass(type);
		   return ((Serial)c.newInstance()).readFrom(this);
		}
		catch (Exception e1) {
		}
	   try {
		   Class c = Class.forName(type);
		   return ((Serial)c.newInstance()).readFrom(this);
		}
		catch (Exception e2) {
		   System.out.println(e2.toString());
		   System.out.println("class loader is " + Thread.currentThread().getContextClassLoader());
			return null;
		}
	}

   /*----------------------------------------------------------------------*/

   public Object read()
	{
	   
      String type = readNextToken();
      Object value = null;
      
      try {

      if (type == null) {
      }
		else if (type.equals("NULL")) {
		}
      else if (type.equals("L")) {
         value = readObject("ch.infogrips.geoshop.server.IClientLine");
      }
      else if (type.equals("A")) {
         value = readObject("ch.infogrips.geoshop.server.IClientArc");
      }
      else if (type.equals("T")) {
         value = readObject("ch.infogrips.geoshop.server.IClientText");
      }
      else if (type.equals("STRING")) {
         value = readString();
      }
      else if (type.equals("INT")) {
         value = readInt();
      }
      else if (type.equals("LONG")) {
         value = readLong();
      }
      else if (type.equals("REAL")) {
         value = readReal();
      }
      else if (type.equals("BOOLEAN")) {
         value = readBoolean();
      }
      else if (type.equals("LIST")) {
         value = readList();
      }
      else if (type.equals("MAP")) {
         value = readMap();
      }
      else if (type.equals("BYTES")) {
         value = readBytes();
      }
      else {
         if (type.equals("MODEL")) {
            type = "ch.infogrips.geoshop.server.IServerModel";
         }
         else if (type.equals("PRODUCT")) {
            type = "ch.infogrips.geoshop.server.IServerProduct";
         }
         else if (type.equals("SELECTION")) {
            type = "ch.infogrips.geoshop.server.IServerSelection";
         }
         else if (type.equals("ORDER")) {
            type = "ch.infogrips.geoshop.server.IServerOrder";
         }
         else if (type.equals("USER")) {
            type = "ch.infogrips.geoshop.server.IServerUser";
         }
         else if (type.equals("SESSION")) {
            type = "ch.infogrips.geoshop.server.IServerSession";
         }
         else if (type.equals("JOB")) {
            type = "ch.infogrips.geoshop.server.IServerJob";
         }
         else if (type.equals("EVENT")) {
            type = "ch.infogrips.geoshop.server.IServerEvent";
         }
         else if (type.equals("SERVICE")) {
            type = "ch.infogrips.geoshop.server.IServerService";
         }
         try {
            value = readObject(type);
         }
         catch (Exception e) {
            System.out.println("unknown type " + type + ": " + e.toString());
            e.printStackTrace();
         }
      }
      
      }
      catch (Exception e) {
      }

      return value;

	}
	
   /*----------------------------------------------------------------------*/

	public static Object loadObject(String fname) 
	{
	   File fin = new File(fname);
	   if (fin.canRead() == false) {
	      return null;
	   }

      FileInputStream fi;
	   SerialInputStream si;
      try {
         fi = new FileInputStream(fin);
	      si = new SerialInputStream(fi);
         Object obj = si.read();
         fi.close();
         return obj;
      }
      catch (Exception e) {
         return null;
      }

	}

   /*----------------------------------------------------------------------*/

}
