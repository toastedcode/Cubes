package com.toast.cubes;

import java.io.IOException;

import com.toast.xml.exception.XmlFormatException;
import com.toast.xml.exception.XmlParseException;

public class Cubes
{
   public static void main (String args[])
   {
      try
      {
         Game.load("/resources/game.xml");
         
         Game.setup();
         
         Game.run();
      }
      catch (IOException | XmlParseException | XmlFormatException e)
      {
         System.out.format("Failed to load game. [%s]", e.toString());
      }
   }
}
