package ch.infogrips.net;

import java.lang.*;

public class HTTPCallException extends Exception 
{

   /*----------------------------------------------------------------------------------------*/

   public static final int UNKNOWN_ERROR   = 1;
   public static final int NETWORK_ERROR   = 2;
   public static final int INTERFACE_ERROR = 3;
   public static final int SERVER_ERROR    = 4;

   /*----------------------------------------------------------------------------------------*/

   protected int errorcode;
   protected String message;

   /*----------------------------------------------------------------------------------------*/

   private HTTPCallException()
	{
	}

   /*----------------------------------------------------------------------------------------*/

   public HTTPCallException(int error)
	{
      errorcode = error;
	   switch (errorcode) {
	      case NETWORK_ERROR:
	         message = "network error";
	         break;
	      case INTERFACE_ERROR:
	         message = "interface error";
	         break;
	      case SERVER_ERROR:
	         message = "server error";
	         break;
	      default:
	         message = "unknown error";
	         break;
	   }
	}

   /*----------------------------------------------------------------------------------------*/

   public HTTPCallException(int error, String msg)
	{
	   errorcode = error;
	   message = msg;
	}

   /*----------------------------------------------------------------------------------------*/

	public String getMessage()
	{
	   return message;
	}
	
   /*----------------------------------------------------------------------------------------*/

	public int getErrorCode()
	{
	   return errorcode;
	}

   /*----------------------------------------------------------------------------------------*/

}
