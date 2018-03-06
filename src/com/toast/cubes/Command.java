package com.toast.cubes;

import java.util.StringTokenizer;

public class Command
{
   public static Command parse(String commandString)
   {
      Command command = null;
      
      // Format: action (object) (indirectObject)
      // Ex:
      // smell the flower
      // swing the sword
      // put the jewel in the staff
      // open the door with the key
      
      String action = "";
      String object = "";
      String indirectObject = "";
      
      // Replace all punctuation with spaces.
      commandString = commandString.replaceAll("[,;.?!]", " ");
      
      StringTokenizer tokenizer = new StringTokenizer(commandString);
      while (tokenizer.hasMoreTokens())
      {
          String token = tokenizer.nextToken().toLowerCase();
          
          if (GameAction.inDictionary(token))
          {
             action = token;
          }
          else if (GameObject.inDictionary(token))
          {
             if (object.isEmpty())
             {
                object = token;
             }
             else if (indirectObject.isEmpty())
             {
                indirectObject = token;
                break;
             }
          }
      }
      
      command = new Command(action, object, indirectObject);
      
      return (command);   
   }
   
   public Command()
   {
      action = "";
      object = "";
      indirectObject = "";
      isValid = false;
   }
   
   public Command(String action)
   {
      this.action = action;
      object = "";
      indirectObject = "";
      isValid = !this.action.isEmpty();
   }
   
   public Command(String action, String object)
   {
      this.action = action;
      this.object = object;
      indirectObject = "";
      isValid = !this.action.isEmpty();
   }
   
   public Command(String action, String object, String indirectObject)
   {
      this.action = action;
      this.object = object;
      this.indirectObject = indirectObject;
      isValid = !this.action.isEmpty();
   }
   
   public boolean isValid()
   {
      return (isValid);
   }
   
   public String getAction()
   {
      return (action);
   }
   
   public String getObject()
   {
      return (object);
   }
   
   public String getIndirectObject()
   {
      return (indirectObject);
   }
   
   private String action;
   
   private String object;
   
   private String indirectObject;
   
   private boolean isValid;
}
