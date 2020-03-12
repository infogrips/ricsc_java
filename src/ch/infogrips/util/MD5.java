package ch.infogrips.util;

import java.io.*;
import java.security.*;

public class MD5 {

   private static String byteToHex(byte[] data) 
   {
      String retvalue = "";
      char[] hextable = new char[16];
      hextable[0] = '0';
      hextable[1] = '1';
      hextable[2] = '2';
      hextable[3] = '3';
      hextable[4] = '4';
      hextable[5] = '5';
      hextable[6] = '6';
      hextable[7] = '7';
      hextable[8] = '8';
      hextable[9] = '9';
      hextable[10] = 'A';
      hextable[11] = 'B';
      hextable[12] = 'C';
      hextable[13] = 'D';
      hextable[14] = 'E';
      hextable[15] = 'F';
      for (int i = 0; i < data.length; i++) {
         int value = (data[i] + 256) % 256;
         retvalue += hextable[value >> 4];
         retvalue += hextable[value & 0x0f];
      }
      return retvalue;
   }

   public static String getFileDigest(String file) 
   {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         FileInputStream in = new FileInputStream(file);
         byte[] buffer = new byte[2000000];
         while (true) {
            int len = in.read(buffer);
            if (len < 0) {
               break;
            }
            md.update(buffer,0,len);
         }
         buffer = null;
         return byteToHex(md.digest());
      }
      catch (Exception e) {
         return null;
      }
   }

}
