package ch.infogrips.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;
import java.util.StringTokenizer;

public class WildCardMatcher {

   /*-------------------------------------------------------------------------*/

   String wildPattern = null;
   Vector pattern = new Vector();

   final String FIND     = "find";
   final String EXPECT   = "expect";
   final String ANYTHING = "anything";
   final String NOTHING  = "nothing";

   /*-------------------------------------------------------------------------*/

   public WildCardMatcher(String wildString)
   {

      wildPattern = wildString;
      wildString = wildString.toLowerCase();

      // remove duplicate asterisks

      int i = wildString.indexOf("**");
      while (i >= 0) {
         wildString = wildString.substring(0, i+1) + wildString.substring(i+2);
         i = wildString.indexOf("**");
      }

      // parse the input string

      StringTokenizer tokens = new StringTokenizer(wildString, "*", true);
      String token = null;
      while (tokens.hasMoreTokens()) {

         token = tokens.nextToken();

         if (token.equals("*")) {
            pattern.addElement(FIND);
            if (tokens.hasMoreTokens()) {
               token = tokens.nextToken();
               pattern.addElement(token);
            }
            else {
               pattern.addElement(ANYTHING);
            }
         }
         else {
            pattern.addElement(EXPECT);
            pattern.addElement(token);
         }
      }

      if (!token.equals("*")) {
         pattern.addElement(EXPECT);
         pattern.addElement(NOTHING);
      }

   }

   /*-------------------------------------------------------------------------*/

   public boolean matches(String name)
   {
 
      name = name.toLowerCase();

      // start processing the pattern vector

      boolean acceptName = true;

      String command = null;
      String param = null;

      int currPos = 0;
      int cmdPos = 0;

      while (cmdPos < pattern.size()) {

         command = (String) pattern.elementAt(cmdPos);
         param = (String) pattern.elementAt(cmdPos + 1);

         if ( command.equals(FIND) ) {

            // if we are to find 'anything'
            // then we are done
                   
            if ( param.equals(ANYTHING) ) {
               break;
            }

            // otherwise search for the param
            // from the curr pos

            int nextPos = name.indexOf(param, currPos);
            if (nextPos >= 0) {
               // found it
               currPos = nextPos + param.length();
            }
            else {
               acceptName = false;
               break;
            }

         }
         else {
            if ( command.equals(EXPECT) ) {

               // if we are to expect 'nothing'
               // then we MUST be at the end of the string

               if ( param.equals(NOTHING) ) {
                  if ( currPos != name.length() ) {
                     acceptName = false;
                  }
                  // since we expect nothing else,
                  // we must finish here
                  break;
               }
               else { 
                  // otherwise, check if the expected string
                  // is at our current position
                  int nextPos = name.indexOf(param, currPos);
                  if ( nextPos != currPos ) {
                     acceptName = false;
                     break;
                  }
                  // if we've made it this far, then we've
                  // found what we're looking for
                  currPos += param.length();
               }
            }
         }

         cmdPos += 2;
      }

      return acceptName;

   }

   /*-------------------------------------------------------------------------*/

   public String toString()
   {
      return wildPattern;
   }

   /*-------------------------------------------------------------------------*/

   public String toPattern()
   {

      StringBuffer out = new StringBuffer();

      int i=0;
      while (i<pattern.size()) {
         out.append( "(" );
         out.append( (String) pattern.elementAt(i) );
         out.append( " " );
         out.append( (String) pattern.elementAt(i+1) );
         out.append( ") " );
         i += 2;
      }

      return out.toString();

   }

   /*-------------------------------------------------------------------------*/

}


