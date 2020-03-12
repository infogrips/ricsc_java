package ch.infogrips.net;

import java.lang.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import ch.infogrips.io.*;

public class HTTPCallClient {

   /*---------------------------------------------------------------------------------------*/
   
   private String url;
      
   /*---------------------------------------------------------------------------------------*/

   public HTTPCallClient(String url,String service,String port,int requestid,int sessionid)
   {
	   if (!url.endsWith("/" + service)) {
         this.url = url + "/" + service + "?" + port + ":" + sessionid + ":" + requestid;
      }
		else {
         this.url = url + "?" + port + ":" + sessionid + ":" + requestid;
		}
   }
   
   /*---------------------------------------------------------------------------------------*/
   
   private void sendArguments(URLConnection uc,Vector arguments)
      throws IOException
   {
      
      uc.setDoOutput(true);
      uc.setRequestProperty("Content-Type","text/x-zip-compressed");
      uc.setRequestProperty("Pragma","no-cache");

      // for testing of HTTP Athenification only (project NISZH/OIZ)
      // uc.setRequestProperty("Authorization","Basic d2lraTpwZWRpYQ==");

      DeflaterOutputStream zip = new DeflaterOutputStream(uc.getOutputStream());
      SerialOutputStream so = new SerialOutputStream(zip);
      so.write(arguments);
      so.flush();
      zip.finish();
      
   }
      
   /*---------------------------------------------------------------------------------------*/
   
   private Vector receiveResults(URLConnection uc)
      throws IOException,HTTPCallException
   {
      // call uc.getContent() before uc.getContentLength() 
      // because under opera only uc.getContentLength() causes a stop

      try {
         uc.getContent();      
      }
		catch (Exception e) {
         throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,"no content (1)");
      }

      if (uc.getContentLength() < 0) {
         // 20141015/mg: Test for ContentLength deactivated because auf Android issues with geodaten.llv.li
         // throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,"no content (2) " + uc.getConentLength());
      }

		SerialInputStream si = 
		   new SerialInputStream(new InflaterInputStream(uc.getInputStream()));

      if (si == null) {
         throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,"no content (3)");
		}

      Integer error = (Integer)si.read();
		if (error == null) {
         throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,"no content (4)");
		}
		
      Object results = si.read();
      
		if (error > 0) {
		   throw new HTTPCallException(error,(String)results);
		}

		return (Vector)results;
		
   }      

   /*---------------------------------------------------------------------------------------*/
   
   public Vector executeWithLogger(Vector arguments,ch.infogrips.appserver.util.Logger logger)
      throws HTTPCallException
   {
      try {
         long timestamp = System.currentTimeMillis();
		   URLConnection uc = (new URL(url + ":" + timestamp)).openConnection();
         sendArguments(uc,arguments);
         return receiveResults(uc);
		}
		catch (HTTPCallException e) {
         if (logger != null) {
            logger.error("HTTPCallException",e);
         }
         throw e;
		}
		catch (IOException e) {
         if (logger != null) {
            logger.error("HTTPCallException",e);
         }
         throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,e.toString());
		}
		catch (Exception e) {
         if (logger != null) {
            logger.error("HTTPCallException",e);
         }
         //clientg java version
         throw new HTTPCallException(HTTPCallException.NETWORK_ERROR,e.toString());
		}

   }
            
   /*---------------------------------------------------------------------------------------*/
   
   public Vector execute(Vector arguments)
      throws HTTPCallException
   {
      return executeWithLogger(arguments,null);
   }
            
   /*---------------------------------------------------------------------------------------*/
      
}
