import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

import ch.infogrips.util.MD5;
import ch.infogrips.util.WildCardMatcher;
import ch.infogrips.service.rics.client.*;

public class CommandControler 
{

   /*-------------------------------------------------------------------------------*/

   private static String RICS_SERVER = "http://www.infogrips.ch/servlet/redirector/checkservice";
   private static String fileSeparator = System.getProperty("file.separator");

   /*-------------------------------------------------------------------------------*/

   private static void displayTitle()
   {
      System.out.println("RICS Commander Version 1.1g, (c) infoGrips GmbH, 2007-2020");
      System.out.println("");
   }

   /*-------------------------------------------------------------------------------*/

   public static void command_help(Hashtable params)
   {
      displayTitle();
      System.out.println("usage: java -jar ricsc.jar -service <service> -user <e-mail>");
      System.out.println("   -command send -file \"<file-pattern>\" -param1 <value1> -param2 <value2> ...");
      System.out.println("   -command check_level -file <file>");
      System.out.println("   -command get_log -file <file>");
   }

   /*--------------------------------------------------------------------------------*/

   private static boolean sendFile(String server,String service,String user,String fname,Hashtable params)
   {

      ClientSession s = new ClientSession();
      boolean connected = false;
      int buffer_size = 2000000;

      try {

         s.connect(server,service,user);
         connected = true;

			BufferedInputStream requestedResource = new BufferedInputStream(
		   	new FileInputStream(fname)
         );

         String name = (new File(fname)).getName();

         // send file in chunks of size buffer_size
         while (true) {      
		      byte buffer[] = new byte[buffer_size];
			   int len = requestedResource.read(buffer);
            if (len == buffer_size) {
               // send full chunk
               s.sendFile(name,buffer,params);
            }
            else if (len > 0) {
               // send last chunk
               byte b[] = new byte[len];
               for (int i=0;i<len;i++) {
                  b[i] = buffer[i];
               }
               s.sendFile(name,b,params);
            }
            else {
               // close file by sending null
               s.sendFile(name,null,params);
               break;
            }
         }

			requestedResource.close();
         s.disconnect();
         connected = false;
         return true;

      }
      catch (Exception e) {
         String error = e.toString();
         error = error.substring(error.lastIndexOf(":")+1);
         System.out.println("   error:" + error);
         if (connected) {
            try {
               s.disconnect();
            }
            catch (Exception ee) {
            }
         }
         return false;
      }

   }

   /*-------------------------------------------------------------------------------*/

   public static int command_send(Hashtable params)
   {

      displayTitle();

      params.remove("command");

      String server = (String)params.get("server");
      params.remove("server");
      if (server == null) {
         server = RICS_SERVER;
      }

      String service = (String)params.get("service");
      params.remove("service");
      if (service == null) {
         System.out.println("parameter -service is missing");
         System.exit(1);
      }
      service = service.toUpperCase();
      String user = (String)params.get("user");
      params.remove("user");
      if (user == null) {
         System.out.println("parameter -user is missing");
         System.exit(1);
      }
      String file_pattern = (String)params.get("file");

      params.remove("file");
      if (file_pattern == null) {
         System.out.println("parameter -file is missing");
         System.exit(1);
      }

      // extract directory
      String directory;
      int ind = file_pattern.lastIndexOf(fileSeparator);
      if (ind >=0) {
         // extract directory from pattern
         directory = file_pattern.substring(0,ind);
         file_pattern = file_pattern.substring(ind+1);
      }
      else {
         // active directory
         directory = ".";
      }
      File f = new File(directory);
      String files[] = f.list();
      if (files == null) {
         System.out.println("directory " + directory + " not found.");
         System.exit(1);
      }

      // prepare wildcard matcher
      WildCardMatcher wm = new WildCardMatcher(file_pattern);

      int status = 0;

      for (int i=0;i<files.length;i++) {

         String fname = files[i];
         if (!wm.matches(fname)) {
            continue;
         }

         System.out.println("sending " + fname + " to service " + service + " ...");
         if (sendFile(server,service,user,directory + fileSeparator + fname,params)) {
            System.out.println("done.");
         }
         else {
            System.out.println("not done.");
            status = 1;
         }
         System.out.println("");

      }

      return status;

	}

   /*-------------------------------------------------------------------------------*/

   public static int command_check_level(Hashtable params)
   {

      displayTitle();

      String server = (String)params.get("server");
      params.remove("server");
      if (server == null) {
         server = RICS_SERVER;
      }

      String service = (String)params.get("service");
      params.remove("service");
      if (service == null) {
         System.out.println("parameter -service is missing");
         System.exit(-1);
      }
      String user = (String)params.get("user");
      params.remove("user");
      if (user == null) {
         System.out.println("parameter -user is missing");
         System.exit(-1);
      }
      String file = (String)params.get("file");
      params.remove("file");
      if (file == null) {
         System.out.println("parameter -file is missing");
         System.exit(-1);
      }
      File f = new File(file);
      if (!f.exists()) {
         System.out.println("file " + file + " not found.");
         return -1;
      }
      String fname = f.getName();

      try {

         ClientSession s = new ClientSession();
         s.connect(server,service,user);
         Vector results = s.getInfo(service,MD5.getFileDigest(file));
         Hashtable filtered_results = new Hashtable(results.size());

         for (int i=0;i<results.size();i++) {
            Hashtable rec = (Hashtable)results.elementAt(i);
            if ((String)rec.get("check_level") == null) {
               continue;
            }
            filtered_results.put(rec.get("service"),rec);
         }

         for (Enumeration e=filtered_results.keys();e.hasMoreElements();) {
            String key = (String)e.nextElement();
            Hashtable rec = (Hashtable)filtered_results.get(key);
            String check_level = null;
            if (rec.get("end_time").equals("")) {
               continue;
            }
            if (rec.get("check_level") == null) {
               check_level = "unknown";
            }
            else {
               check_level = "" + rec.get("check_level");
            }
            System.out.println(
               fname + ":" +
               " checked with service " + rec.get("service") +
               " at " + rec.get("end_time") + 
               ", check_level=" + check_level
            );
         }

         s.disconnect();
         return 0; // to do !!!

      }
      catch (Exception e) {
         System.out.println("info: " + e.toString());
         e.printStackTrace();
         return -1;
      }

	}

   /*-------------------------------------------------------------------------------*/

   public static int command_get_log(Hashtable params)
   {

      displayTitle();

      String server = (String)params.get("server");
      params.remove("server");
      if (server == null) {
         server = RICS_SERVER;
      }

      String service = (String)params.get("service");
      params.remove("service");
      if (service == null) {
         System.out.println("parameter -service is missing");
         System.exit(-1);
      }
      String user = (String)params.get("user");
      params.remove("user");
      if (user == null) {
         System.out.println("parameter -user is missing");
         System.exit(-1);
      }
      String file = (String)params.get("file");
      params.remove("file");
      if (file == null) {
         System.out.println("parameter -file is missing");
         System.exit(-1);
      }
      File f = new File(file);
      if (!f.exists()) {
         System.out.println("file " + file + " not found.");
         return -1;
      }
      String fname = f.getName();

      try {

         ClientSession s = new ClientSession();
         s.connect(server,service,user);
         Vector results = s.getInfo(service,MD5.getFileDigest(file));
         Hashtable filtered_results = new Hashtable(results.size());
         String start_time = "";
         String output_dir_http = "";

         for (int i=0;i<results.size();i++) {

            Hashtable rec = (Hashtable)results.elementAt(i);

            if ((String)rec.get("output_dir_http") == null) {
               continue;
            }
            String output_dir_http2 = (String)rec.get("output_dir_http");

            if ((String)rec.get("start_time") == null) {
               continue;
            }
            String start_time2 = (String)rec.get("start_time");

            if (start_time2.compareTo(start_time) > 0) {
               start_time = start_time2;
               if (rec.get("end_time").equals("")) {
                  output_dir_http = "";
                  continue;
               }
               if (rec.get("output_file") != null) {
                  output_dir_http = output_dir_http2 + "/" + rec.get("output_file");
               }
               else {
                  output_dir_http = output_dir_http2 + "/output.zip";
               }
            }

         }

         s.disconnect();

         if (!output_dir_http.equals("")) {
            System.out.println(output_dir_http);
            return 0;
         }
         else {
            return 1;
         }

      }
      catch (Exception e) {
         System.out.println("info: " + e.toString());
         e.printStackTrace();
         return -1;
      }

	}

   /*-------------------------------------------------------------------------------*/

}
