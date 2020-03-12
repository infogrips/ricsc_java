import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ch.infogrips.util.*;

public class RicsCommander {

   /*-------------------------------------------------------------------------------*/

	public static void main(String[] args) 
	{

      int status = 0;

		try {

		   Hashtable a = ArgumentUtil.getArguments(args);
		   ArgumentUtil.setProxy(a);
         String command = (String)a.get("command");
         if (command == null) {
            CommandControler.command_help(a);
            status = 1;
         }
         else if (command.equals("send")) {
            status = CommandControler.command_send(a);
         }
         else if (command.equals("check_level")) {
            status = CommandControler.command_check_level(a);
         }
         else if (command.equals("get_log")) {
            status = CommandControler.command_get_log(a);
         }
         else {
            CommandControler.command_help(a);
            status = 1;
         }

		}
		catch (Exception exc) { 
		   exc.printStackTrace(); 
         status = 1;
		}

      System.exit(status);

	}

   /*-------------------------------------------------------------------------------*/

}
