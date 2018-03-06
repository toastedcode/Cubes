package com.toast.cubes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.toast.cubes.io.Console;
import com.toast.cubes.io.GameInput;
import com.toast.cubes.io.GameOutput;
import com.toast.xml.XmlDocument;
import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;

import bsh.EvalError;
import bsh.Interpreter;

public class Game
{
   // **************************************************************************
   //                              Public interface
   
   public static void setup()
   {
      Console console = new Console();
      
      input = console;
      output = console;
      
      player = new Player("me");
      player.add(new GameObject("inventory"));
   }
   
   public static void load(String filename) throws IOException, XmlParseException, XmlFormatException
   {
      String pathString = Game.class.getResource("/resources/testGame.xml").getFile();
      pathString = new File(pathString).getAbsolutePath();
      
      XmlDocument document = new XmlDocument();
      
      document.load(pathString);
      
      load(document.getRootNode());
   }
   
   public static void loadRoom(String filename) throws IOException, XmlParseException, XmlFormatException
   {
      XmlDocument document = new XmlDocument();
      
      document.load(filename);
      
      GameObject room = GameObject.load(document.getRootNode());
      
      setCurrentRoom(room);
   }
   
   public static void run()
   {
      Command command = null;
      
      echo(gameObject.getDescription());
      echo("You start in %s.\n", currentRoom.getName());
      
      while (Game.getVar("gameOver").asBool() != true)
      {
         String commandString =  input.read();
         
         command = Command.parse(commandString);
         
         handleCommand(command);
         
      }      
   }
   
   public static void stop()
   {
      Game.setVar("gameOver",  true);
   }
   
   public static void echo(String text)
   {
      output.echo(text);
   }
   
   public static void echo(String format, Object ... objects)
   {
      output.echo(format, objects);
   }
   
   public static GameObject getCurrentRoom()
   {
      return (currentRoom);
   }
   
   public static void setCurrentRoom(GameObject room)
   {
      currentRoom = room;
   }
   
   public static GameObject getRoom(String name)
   {
      return (rooms.get(name));
   }
   
   public static GameObject get(String query)
   {
      // get("rooms.kitchen.table.box")
      // get("here.cupboard.coat")
      // get("self.inventory.sword")
      // get("players.rob.inventory.shield")
      // get("game.score")
      // get("potion")  // assumes current room
      // get("fridge.apple")  // assumes current room
      // get("*.banana");  // look everywhere
      
      GameObject foundObject = null;
      
      StringTokenizer tokenizer = new StringTokenizer(query, ".");
      
      String token = tokenizer.nextToken();
      
      if (tokenizer.hasMoreTokens())
      {      
         switch (token)
         {
            case "game":
            {
               foundObject = gameObject.get(tokenizer.nextToken(""));
               break;
            }
            
            case "players":
            {
               if (tokenizer.hasMoreTokens())
               {
                  /*
                  GameObject player = players.get(tokenizer.nextToken());
                  if (player != null)
                  {
                     foundObject = player.get(tokenizer.nextToken(""));
                  }
                  */
               }
               break;
            }
            
            case "rooms":
            {
               if (tokenizer.hasMoreTokens())
               {
                  GameObject room = rooms.get(tokenizer.nextToken());
                  if (room != null)
                  {
                     foundObject = room.get(tokenizer.nextToken(""));
                  }
               }
               break;
            }
            
            case "self":
            {
               foundObject = player.get(tokenizer.nextToken(""));
               break;
            }
            
            default:
            {
               // Assume current room.
               foundObject = currentRoom.get(query);
               break;
            }
         }
      }
      else
      {
         // Assume current room.
         foundObject = currentRoom.get(token);
      }
      
      return (foundObject);
   }
   
   public static List<GameObject> getAll(String name)
   {
      // get("kitchen.table.*")
      // get("rooms.*.door")
      
      List<GameObject> objects = new ArrayList<>();
      
      // TODO: Implement a object.var retrieval format.
      
      return (objects);
   }
   
   public static void move(GameObject object, GameObject destination)
   {
      if ((object != null) && (destination != null))
      {
         GameObject parent = object.getParent();
         if (parent != null)
         {
            object.getParent().remove(object);
         }
         
         destination.add(object);
      }
   }
   
   public static Variable getVar(String name)
   {
      Variable variable = null;
      
      // TODO: Implement a object.var retrieval format.
      variable = gameObject.getVar(name);
      
      return (variable);
   }
   
   public static <T> void setVar(String name, T value)
   {
      gameObject.setVar(name,  value);
   }
   
   // **************************************************************************
   
   private static void load(XmlNode node) throws XmlFormatException
   {
      //
      // Global objects, variables, actions
      //
      
      gameObject = GameObject.load(node);
      
      //
      // Rooms
      //
      
      XmlNodeList childNodes = node.getChildren("room");

      for (XmlNode roomNode : childNodes)
      {
         GameObject room = GameObject.load(roomNode);
         
         if (room != null)
         {
            rooms.put(room.getName(),  room);
         }
      }
     
      //
      // Start
      //
      
      String start = node.getChild("start").getValue();
      if (rooms.containsKey(start))
      {
         currentRoom = getRoom(start);
      }
      else
      {
         // If not specified, get an arbitrary room.
         currentRoom = getRoom((String)rooms.keySet().toArray()[0]);
      }
   }
   
   private static Response handleCommand(Command command)
   {
      Response response = null;
      
      // Allow the room to handle the command.
      response = currentRoom.handleCommand(command);
      
      // If unhandled, try the game's set of default actions.
      if (response == null)
      {
         response = gameObject.handleCommand(command);
      }
      
      processResponse(command, response);
      
      return (response);
   }
   
   private static void processResponse(Command command, Response response)
   {
      if (response != null)
      {
         echo(response.getText());
         
         executeCode(command, response.getCode());
      }
      else
      {
         echo("I don't know how to do that.");
      } 
   }
   
   private static void executeCode(Command command, String code)
   {
      final String IMPORTS = "import com.toast.cubes.*;";
      
      // Add default imports.
      code = IMPORTS + code;

      try
      {
         Interpreter interpreter = new Interpreter();
         
         interpreter.set("player",  player);
         interpreter.set("room",  currentRoom);
         interpreter.set("command",  command);
         interpreter.set("inventory", player.get("inventory"));
         
         interpreter.eval(code);
      } 
      catch (EvalError e)
      {
         e.printStackTrace();
      }
   }
   
   private static GameInput input;
   
   private static GameOutput output;
   
   // Global objects, variables, actions.
   private static GameObject gameObject;
   
   private static Map<String, GameObject> rooms = new HashMap<>();
   
   private static GameObject currentRoom;
   
   private static Player player;
}