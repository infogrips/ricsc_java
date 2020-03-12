package ch.infogrips.service.rics.client;

import java.lang.*;

public class ClientException extends Exception 
{

   /*----------------------------------------------------------------------------------------*/

   public static final int UNKNOWN_ERROR = 1;
   public static final int NETWORK_ERROR = 2;
   public static final int INTERFACE_ERROR = 3;

   public static final int INVALID_USER_PASSWORD = 1000;
   public static final int INSUFFICIENT_PRIVILEGE = 1001;
   public static final int NO_ACTIVE_SESSION = 1002;
   public static final int INVALID_USER = 1005;
   
   /*----------------------------------------------------------------------------------------*/

   private int errorcode;
   private String errormessage = null;

   /*----------------------------------------------------------------------------------------*/

   private ClientException()
	{
   }

   /*----------------------------------------------------------------------------------------*/

   ClientException(int errorcode)
   {
      if (errorcode >= 100 && errorcode <= 999) {
         this.errorcode = INTERFACE_ERROR;
      }
      else {
	      this.errorcode = errorcode;
	   }
      this.errormessage = null;
	}

   /*----------------------------------------------------------------------------------------*/

   ClientException(int errorcode,String message)
   {
      if (errorcode >= 100 && errorcode <= 999) {
         this.errorcode = INTERFACE_ERROR;
      }
      else {
	      this.errorcode = errorcode;
	   }
      this.errormessage = message;
	}

   /*----------------------------------------------------------------------------------------*/

	public String getMessage()
	{
      if (errormessage != null) {
         return errormessage;
      }
	   String message;
	   switch (errorcode) {
	      case NETWORK_ERROR:
	         message = "network error";
	         break;
	      case INTERFACE_ERROR:
	         message = "interface error";
	         break;
         case INVALID_USER_PASSWORD:
	         message = "invalid user / password";
	         break;
         case NO_ACTIVE_SESSION:
	         message = "no active session";
	         break;
         case INVALID_USER:
	         message = "invalid user";
	         break;
	      default:
	         message = "unknown error";
	   }
	   return message;
	}
	
   /*----------------------------------------------------------------------------------------*/

	public int getErrorCode()
	{
	   return errorcode;
	}

   /*----------------------------------------------------------------------------------------*/

}

