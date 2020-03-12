package ch.infogrips.service.rics.client;

import java.util.*;
import ch.infogrips.net.*;

public class ClientSession {

   /*----------------------------------------------------------------------------------------*/

   private static final String service = "RICS";
	private static final String port = "Client";

   /*----------------------------------------------------------------------------------------*/

   private int sessionid;
   private HTTPCallClient connectCall;
   private HTTPCallClient disconnectCall;
   private HTTPCallClient sendFileCall;
   private HTTPCallClient getInfoCall;
   private boolean connected = false;

   /*----------------------------------------------------------------------------------------*/

   private Vector arguments;
   private Vector results;

   /*----------------------------------------------------------------------------------------*/

   public long getID()
   {
      return (long)sessionid;
   }

   /*----------------------------------------------------------------------------------------*/

   public boolean isConnected()
   {
      return connected;
   }

   /*----------------------------------------------------------------------------------------*/

   private void assertConnected() throws 
      ClientException
   {
	   if (!connected) {
		   throw new ClientException
			   (ClientException.NO_ACTIVE_SESSION);
      }
   }

   /*----------------------------------------------------------------------------------------*/

   public void connect(String server, String usr, String passwd) 
      throws ClientException
   {
      
      connectCall = new HTTPCallClient(server,service,port,0,0);
      arguments = new Vector(2);
      arguments.addElement(usr);
      arguments.addElement(passwd);
      
      try {

         results = connectCall.execute(arguments);
         sessionid = ((Integer)results.elementAt(0)).intValue();
         int sessionid = ((Integer)results.elementAt(0)).intValue();

         disconnectCall = new HTTPCallClient(server,service,port,1,sessionid);
         sendFileCall = new HTTPCallClient(server,service,port,2,sessionid);
         getInfoCall = new HTTPCallClient(server,service,port,3,sessionid);

         connected = true;
         
	   }
      catch (HTTPCallException e) {
		   throw new ClientException(e.getErrorCode(),e.toString());
      }
	   catch (Exception e) {
		   throw new ClientException
			   (ClientException.UNKNOWN_ERROR,e.toString());
	   }

   }

   /*----------------------------------------------------------------------------------------*/

   public void disconnect()
      throws ClientException
   {
      try {
         assertConnected();
         disconnectCall.execute(null);
         connected = false;
      }
      catch (HTTPCallException e) {
		   throw new ClientException(e.getErrorCode());
      }
	   catch (Exception e) {
		   throw new ClientException
			   (ClientException.UNKNOWN_ERROR,e.toString());
	   }
   }

   /*----------------------------------------------------------------------------------------*/

   public void sendFile(String fname,byte[] buffer,Hashtable options)
      throws ClientException
   {
      try {
         arguments = new Vector(3);
         arguments.addElement(fname);
         arguments.addElement(buffer);
         arguments.addElement(options);
         sendFileCall.execute(arguments);
      }
      catch (HTTPCallException e) {
		   throw new ClientException(e.getErrorCode(),e.toString());
      }
      catch (Exception e) {
         throw new ClientException(HTTPCallException.UNKNOWN_ERROR,e.toString());
      }
   }

   /*----------------------------------------------------------------------------------------*/

   public Vector getInfo(String service,String md5)
      throws ClientException
   {
      try {
         arguments = new Vector(2);
         arguments.addElement(service);
         arguments.addElement(md5);
         results = getInfoCall.execute(arguments);
         return results;
      }
      catch (HTTPCallException e) {
		   throw new ClientException(e.getErrorCode());
      }
      catch (Exception e) {
         throw new ClientException(HTTPCallException.UNKNOWN_ERROR,e.toString());
      }
   }

   /*----------------------------------------------------------------------------------------*/

}
