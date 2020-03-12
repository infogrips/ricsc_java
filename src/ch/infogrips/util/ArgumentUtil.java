package ch.infogrips.util;

import java.lang.*;
import java.util.*;
import java.net.*;

public class ArgumentUtil 
{

   /*---------------------------------------------------------------------*/

   public static Hashtable getArguments(String argv[])
   {
      if (argv == null) {
         System.out.println("argv is null");
      }
      boolean nameisnext = true;
      String name = "";
      String value = "";
      int argc = 0;

      Hashtable arguments = new Hashtable(10);
      for (int i=0;i<argv.length;i++) {

         if (nameisnext) {
            name = argv[i].trim();
            if (name.startsWith("-")) {
               name = name.substring(1);
               nameisnext = false;
            }
            else {
               argc++;
               arguments.put("arg" + argc,name);
            }
         }
         else {
            value = argv[i].trim();
            if (value.startsWith("-")) {

               boolean doublevalue = false;
 
               // check is a minus int or double value
               try {
                   Double d = new Double((String)value);
                   if (d != null) doublevalue = true;  
               } 
               catch (Exception e) {
                  doublevalue = false;
               }

               if (doublevalue) {
                  arguments.put(name,value);
               }
               else {
                  arguments.put(name,"");
                  i--;
               }
            }
            else {
               arguments.put(name,value);
            }
            nameisnext = true;
         }
      }
      if (!nameisnext) {
         arguments.put(name,"");
      }
      return arguments;
   }

   /*---------------------------------------------------------------------*/
	
	private static final String ProxyDebug = "proxyDebug";
	private static final String ProxyHost = "proxyHost";
	private static final String ProxyPort = "proxyPort";
	private static final String ProxyUser = "proxyUser";
	private static final String ProxyPassword = "proxyPassword";
	
	private static final String SocksProxyHost = "socksProxyHost";
	private static final String SocksProxyPort = "socksProxyPort";

   public static boolean setProxy(Hashtable args)
   {

      final String proxyDebug = (String)args.get(ProxyDebug);
      String proxyHost = (String)args.get(ProxyHost);
      String socksProxyHost = (String)args.get(SocksProxyHost);

      // 20170329/mg Authenticator added
      // Java ignores http.proxyUser / http.proxyPassword, here come's the workaround.
      Authenticator.setDefault(
         new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
               if (getRequestorType() == RequestorType.PROXY) {
                  String host = System.getProperty("http.proxyHost","");
                  String port = System.getProperty("http.proxyPort","");
                  String user = System.getProperty("http.proxyUser","");
                  String password = System.getProperty("http.proxyPassword","");
                  if (proxyDebug != null) {
                     System.out.println("PROXY: authentication is <" + user + "/" + password + ">");
                  }
                  if (getRequestingHost().toLowerCase().equals(host.toLowerCase())) {
                     if (Integer.parseInt(port) == getRequestingPort()) {
                        // access seems to be OK.
                        return new PasswordAuthentication(user, password.toCharArray());  
                     }
                  }
               }
               return null;
            }  
         }
      );

      if (proxyHost != null) {

         if (proxyDebug != null) {
            System.out.println("PROXY: proxyHost is <" + proxyHost + ">");
         }

         PropertyUtil.setProperty("http." + ProxyHost,proxyHost);
         PropertyUtil.setProperty("https." + ProxyHost,proxyHost);
         String proxyPort = (String)args.get(ProxyPort);
         if (proxyPort == null) {
            if (proxyDebug != null) {
               System.out.println("PROXY: proxyPort is missing");
            }
            return false;
         }
         PropertyUtil.setProperty("http." + ProxyPort,proxyPort);
         PropertyUtil.setProperty("https." + ProxyPort,proxyPort);
         if (proxyDebug != null) {
            System.out.println("PROXY: proxyPort is <" + proxyPort + ">");
         }

         String proxyUser = (String)args.get(ProxyUser);
         if (proxyUser != null) {
            if (proxyDebug != null) {
               System.out.println("PROXY: proxyUser is <" + proxyUser + ">");
            }
            PropertyUtil.setProperty("http." + ProxyUser,proxyUser);
            PropertyUtil.setProperty("https." + ProxyUser,proxyUser);
            String proxyPassword = (String)args.get(ProxyPassword);
            if (proxyPassword != null) {
               if (proxyDebug != null) {
                  System.out.println("PROXY: proxyPassword is <" + proxyPassword + ">");
               }
               PropertyUtil.setProperty("http." + ProxyPassword,proxyPassword);
               PropertyUtil.setProperty("https." + ProxyPassword,proxyPassword);
            }
         }

      }
      else if (socksProxyHost != null) {

         if (proxyDebug != null) {
            System.out.println("PROXY: socksProxyHost is <" + socksProxyHost + ">");
         }

         PropertyUtil.setProperty(SocksProxyHost,socksProxyHost);
         String socksProxyPort = (String)args.get(SocksProxyPort);
         if (socksProxyPort == null) {
            if (proxyDebug != null) {
               System.out.println("PROXY: socksProxyPort is missing");
            }
            return false;
         }
         PropertyUtil.setProperty(SocksProxyPort,socksProxyPort);
         if (proxyDebug != null) {
            System.out.println("PROXY: socksProxyPort is <" + socksProxyPort + ">");
         }

         String proxyUser = (String)args.get(ProxyUser);
         if (proxyUser != null) {
            if (proxyUser != null) {
               System.out.println("PROXY: proxyUser is <" + proxyUser + ">");
            }
            PropertyUtil.setProperty("java.net.socks.username",proxyUser);
            String proxyPassword = (String)args.get(ProxyPassword);
            if (proxyPassword != null) {
               if (proxyDebug != null) {
                  System.out.println("PROXY: proxyPassword is <" + proxyPassword + ">");
               }
               PropertyUtil.setProperty("java.net.socks.password",proxyPassword);
            }
         }
      }

      return true;

   }

   /*---------------------------------------------------------------------*/
	
}
