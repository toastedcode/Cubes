package com.toast.cubes.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console implements GameInput, GameOutput
{
   public Console()
   {
      
   }

   @Override
   public void echo(String text)
   {
      if (!text.isEmpty())
      {
         System.out.format("%s\n", text);
      }      
   }
   
   @Override
   public void echo(String format, Object ... objects)
   {
      if (!format.isEmpty())
      {
         System.out.format(format, objects);
      } 
   }

   @Override
   public String read()
   {
      String input = "";
      
      try
      {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
         System.out.print(">>>");
         input = bufferedReader.readLine();
      }
      catch (IOException e)
      {
         
      }
      
      return (input);
   }

}
