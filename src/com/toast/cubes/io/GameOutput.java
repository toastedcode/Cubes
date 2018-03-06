package com.toast.cubes.io;

public interface GameOutput
{
   void echo(String text);
   
   void echo(String format, Object ... objects);
}
