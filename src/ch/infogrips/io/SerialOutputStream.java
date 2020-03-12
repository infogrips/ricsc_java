package ch.infogrips.io;

import java.lang.*;
import java.util.*;
import java.io.*;

public class SerialOutputStream {

   /*----------------------------------------------------------------------*/

   private OutputStream o = null;
   private int level = 0;
   private String buffer = "";
   private boolean pretty = false;
	private boolean ignoreFirstPrefix = false; 

   /*----------------------------------------------------------------------*/

   private SerialOutputStream()
   {
   }

   /*----------------------------------------------------------------------*/

   public SerialOutputStream(OutputStream o)
   {
      this.o = o;
   }

   /*----------------------------------------------------------------------*/

   public void setPretty(boolean pretty)
	{
	   this.pretty = pretty;
	}

   /*----------------------------------------------------------------------*/

   public void flush()
   {
      try {
         o.flush();
      }
      catch (Exception e) {
         System.out.println("SerialOutputStream::flush() " + e.toString()); 
      }
   }

   /*----------------------------------------------------------------------*/

   public void writeBuffer(String b)
   {
      buffer += b;
   }

   /*----------------------------------------------------------------------*/

   public void openComplexObject(String label)
   {
      writeBuffer(label);writeln();level++;
   }

   /*----------------------------------------------------------------------*/

   public void closeComplexObject()
   {
      level--;writeBuffer("}");writeln();
   }

   /*----------------------------------------------------------------------*/

   public void write2(String t,Object obj)
   {
      if (obj != null) {
         buffer = buffer + t + " ";
         write(obj);
      }
   }

   /*----------------------------------------------------------------------*/

   public void writeLevelPrefix()
	   throws IOException
	{
	   if (ignoreFirstPrefix) {
			ignoreFirstPrefix = false;
		}
		else if (pretty) {
       	for (int i=0;i<level;i++) {
           	o.write("   ".getBytes());
        	}
      }
	}

   /*----------------------------------------------------------------------*/

   public void writeln()
   {
      
      try {
		   writeLevelPrefix();
			//20100920/mg: in android default getBytes() encoding is UTF-8
			//             under windows (ie. geoshop server) default getBytes() encoding is ISO-8859-1
         o.write(buffer.getBytes("iso-8859-1"));
         o.write("\r\n".getBytes());
         // System.out.println("buffer=" + buffer);
         buffer = "";
      }
      catch (IOException e) {
         System.out.println("SerialOutputStream::writeln(): " + e.toString());
      }

      buffer = "";

   }

   /*----------------------------------------------------------------------*/

   private void writeNull()
   {
      writeBuffer("NULL");
      writeln();
   }

   /*----------------------------------------------------------------------*/

   private void writeBytes(byte[] b)
   {
	   if (b == null) {
		   writeBuffer("BYTES 0");
         writeln();
		}
		else {
			try {
            o.write(("BYTES " + b.length + " ").getBytes());
		      o.write(b);
			   o.write("\r\n".getBytes());
			}
			catch (Exception e) {
			}
		}
   }

   /*----------------------------------------------------------------------*/

   private void writeBoolean(Boolean b)
   {
      writeBuffer("BOOLEAN " + b.toString());
      writeln();
   }

   /*----------------------------------------------------------------------*/

   private void writeInt(Integer i)
   {
      writeBuffer("INT " + i.toString());
      writeln();
   }

   /*----------------------------------------------------------------------*/

   private void writeLong(Long l)
   {
      writeBuffer("LONG " + l.toString());
      writeln();
   }

   /*----------------------------------------------------------------------*/

   private void writeReal(Double r)
   {
      writeBuffer("REAL " + r.toString());
      writeln();
   }

   /*----------------------------------------------------------------------*/

   public void writeStringValue(String s)
   {
      // mask ' with \' for StringTokenizer

      if (s.indexOf("'") != -1) {
         String sn = "";
         for (int i=0;i<s.length();i++) {
            if (s.charAt(i) == '\'') {
               sn = sn + "\\";
            }
            sn = sn + s.charAt(i);

         }
         s = sn;
      }
      
      if ((s.indexOf(" ") != -1) ||
          (s.indexOf("'") != -1) ||
          (s.indexOf("!") != -1)) {
         writeBuffer("'");
         writeBuffer(s);
         writeBuffer("'");
      }
      else if (s.compareTo("") == 0) {
         writeBuffer("''");
      }
      else {
         writeBuffer(s);
      }

   }

   /*----------------------------------------------------------------------*/

   private void writeString(String s)
   {
      writeBuffer("STRING ");
      writeStringValue(s);
      writeln();
   }

   /*----------------------------------------------------------------------*/

   private void writeList(Vector v)
   {
      openComplexObject("LIST");
      for (Enumeration e=v.elements();e.hasMoreElements();) {
         write(e.nextElement());
      }
      closeComplexObject();
   }
 
   /*----------------------------------------------------------------------*/

   private void writeMap(Hashtable h)
   {
      openComplexObject("MAP");
      Enumeration k=h.keys();
      while (true) {
         if (!k.hasMoreElements()) {
            break;
         }
         String key = (String)k.nextElement();
         try {
			   writeLevelPrefix();
            o.write((key + " ").getBytes());
         }
         catch (IOException ex) {
            System.out.println("SerialOutputStream::writeMap(): " + ex.toString());
         }
			ignoreFirstPrefix = true;
         write(h.get(key));
      }
      closeComplexObject();
   }
 
   /*----------------------------------------------------------------------*/

   public void write(Object obj)
	{
	   if (obj == null) {
	      writeNull();
	      return;
	   }

      String type = obj.getClass().getName();
      //System.out.println("write " + type);

      if (type.equals("java.lang.String")) {
         writeString((String)obj);
      }
      else if (type.equals("java.lang.Integer")) {
         writeInt((Integer)obj);
      }
      else if (type.equals("java.lang.Long")) {
         writeLong((Long)obj);
      }
      else if (type.equals("java.lang.Double")) {
         writeReal((Double)obj);
      }
      else if (type.equals("java.lang.Boolean")) {
         writeBoolean((Boolean)obj);
      }
      else if (type.equals("java.util.Vector")) {
         writeList((Vector)obj);
      }
      else if (type.equals("java.util.Hashtable")) {
         writeMap((Hashtable)obj);
      }
      else if (type.equals("ch.infogrips.util.Map")) {
         writeMap((Hashtable)obj);
      }
      else if (type.equals("[B")) {
         writeBytes((byte[])obj);
      }
      else {
         try {
            ((Serial)obj).writeTo(this);
         }
         catch (Exception e) {
            System.out.println("unknown object type <" + type + ">");
         }
      }

	}

   /*----------------------------------------------------------------------*/

	public static synchronized boolean saveObject(String fname,Object obj) 
	{
	   
	   File fout = new File(fname);
      FileOutputStream fo;
	   SerialOutputStream so;
	   
      try {
         fo = new FileOutputStream(fout);
	      so = new SerialOutputStream(fo);
	      so.pretty = true;
			so.ignoreFirstPrefix = false;
         so.write(obj);
         fo.close();
         return true;
      }
      catch (Exception e) {
         System.out.println(e.toString());
         return false;
      }
      
	}

   /*----------------------------------------------------------------------*/
	
}
