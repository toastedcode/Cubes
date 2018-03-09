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
      Player player = new Player("Parzival");
      
      players.put(player.getName(), player);
      
      player.start();
   }
   
   public static void load(String filename) throws IOException, XmlParseException, XmlFormatException
   {
      String pathString = Game.class.getResource(filename).getFile();
      pathString = new File(pathString).getAbsolutePath();
      
      XmlDocument document = new XmlDocument();
      
      document.load(pathString);
      
      load(document.getRootNode());
   }
   
   public static GameObject loadRoom(String filename) throws IOException, XmlParseException, XmlFormatException
   {
      String pathString = Game.class.getResource(filename).getFile();
      pathString = new File(pathString).getAbsolutePath();

      XmlDocument document = new XmlDocument();
      
      document.load(pathString);
      
      GameObject room = GameObject.load(document.getRootNode());
      
      rooms.put(room.getName(), room);
      
      return (room);
   }
   
   public static void run()
   {
      while (Game.getVar("gameOver").asBool() != true)
      {
      }      
   }
   
   public static void stop()
   {
      Game.setVar("gameOver",  true);
   }
   
   
   public static Response handleCommand(Player player, Command command)
   {
      Response response = null;
      
      // Allow the player's room to handle the command.
      response = player.getRoom().handleCommand(command);
      
      // If unhandled, try the game's set of default actions.
      if (response == null)
      {
         response = gameObject.handleCommand(command);
      }
      
      processResponse(player, command, response);
      
      return (response);
   }   
   
   public static void echo(String text)
   {
      for (Player player : players.values())
      {
         player.echo(text);
      }
   }
   
   public static void echo(String format, Object ... objects)
   {
      for (Player player : players.values())
      {
         player.echo(format, objects);
      }
   }
   
   public static String getDescription()
   {
      return (gameObject.getDescription());
   }
   
   public static String getDetails()
   {
      return (gameObject.getDetails());
   }
  
   public static GameObject getRoom(String name)
   {
      return (rooms.get(name));
   }
   
   public static GameObject getStartRoom()
   {
      return (startRoom);
   }
   
   public static GameObject get(String query)
   {
      // get("rooms.kitchen.table.box")
      // get("players.rob.inventory.shield")
      // get("game.score")
      
      GameObject foundObject = null;
      
      StringTokenizer tokenizer = new StringTokenizer(query, ".");
      
      String token = tokenizer.nextToken();
      
      if (tokenizer.hasMoreTokens())
      {      
         switch (token)
         {
            case "players":
            {
               if (tokenizer.hasMoreTokens())
               {
                  GameObject player = players.get(tokenizer.nextToken());
                  if (player != null)
                  {
                     foundObject = player.get(tokenizer.nextToken(""));
                  }
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
            
            default:
            case "game":
            {
               foundObject = gameObject.get(tokenizer.nextToken(""));
               break;
            }
         }
      }
      else
      {
         // Assume game object.
         foundObject = gameObject.get(tokenizer.nextToken(""));
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
   
   private static void load(XmlNode node) throws IOException, XmlFormatException, XmlParseException
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
         if (roomNode.hasAttribute("src"))
         {
            // Load from external file.
            
            loadRoom(roomNode.getAttribute("src").getValue());
         }
         else
         {
            // Load fron internal room node.
            
            GameObject room = GameObject.load(roomNode);
            
            if (room != null)
            {
               rooms.put(room.getName(),  room);
            }
         }
      }
     
      //
      // Start
      //
      
      String start = node.getChild("start").getValue();
      if (rooms.containsKey(start))
      {
         setVar("startRoom", start);
      }
      else
      {
         // If not specified, get an arbitrary room.
         setVar("startRoom", rooms.keySet().toArray()[0]);
      }
   }
   
   private static void processResponse(Player player, Command command, Response response)
   {
      if (response != null)
      {
         echo(response.getText());
         
         executeCode(player, command, response.getCode());
      }
      else
      {
         echo("I don't know how to do that.");
      } 
   }
   
   private static void executeCode(Player player, Command command, String code)
   {
      final String IMPORTS = "import com.toast.cubes.*;";
      
      // Add default imports.
      code = IMPORTS + code;

      try
      {
         Interpreter interpreter = new Interpreter();
         
         GameObject room = player.getRoom();
         
         interpreter.set("player",  player);
         interpreter.set("room", room);
         interpreter.set("command",  command);
         interpreter.set("inventory", player.get("inventory"));
         
         interpreter.eval(code);
      } 
      catch (EvalError e)
      {
         e.printStackTrace();
      }
   }
   
   // Global objects, variables, actions.
   private static GameObject gameObject;
   
   private static Map<String, Player> players = new HashMap<>();
   
   private static Map<String, GameObject> rooms = new HashMap<>();
   
   private static GameObject startRoom;
}