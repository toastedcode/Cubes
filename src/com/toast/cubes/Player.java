package com.toast.cubes;

import com.toast.cubes.io.Console;
import com.toast.cubes.io.GameInput;
import com.toast.cubes.io.GameOutput;

public class Player extends GameObject implements Runnable
{
   public Player(String name)
   {
      super(name);
      
      add(new GameObject("inventory"));
      
      Console console = new Console();
      
      input = console;
      output = console;
      
      room = Game.getRoom(Game.getVar("startRoom").asString());
   }
   
   public void start()
   {
      echo(Game.getDescription());
      echo("Your journey ... begins.");
      echo(room.getDescription());
      
      // Start the player loop.
      new Thread(this).start();
   }
   
   public void echo(String text)
   {
      output.echo(text);
   }
   
   public void echo(String format, Object ... objects)
   {
      output.echo(format, objects);
   }
   
   public GameObject getRoom()
   {
      return (room);
   }
   
   public GameObject getInventory()
   {
      return (get("inventory"));
   }
   
   public boolean moveTo(String roomName)
   {
      boolean moved = false;
      
      GameObject nextRoom = Game.getRoom(roomName);
      
      if (nextRoom != null)
      {
         room = nextRoom;
         
         echo(room.getDescription());
         
         moved = true;
      }
      
      return (moved);
   }
   
   // **************************************************************************
   //                          Runnable interface

   @Override
   public void run()
   {
      while (getVar("gameOver").asBool() == false)
      {
         String commandString =  input.read();
         
         Command command = Command.parse(commandString);
         
         Game.handleCommand(this, command);
      }
      
      Game.removePlayer(getName());
   }
   
   private static GameObject room;
   
   private static GameInput input;
   
   private static GameOutput output;
}
