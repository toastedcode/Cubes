package com.toast.cubes;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Variable
{
   public static Variable load(XmlNode node) throws XmlFormatException
   {
      Variable variable = new Variable(node.getAttribute("name").getValue(), node.getAttribute("value").getValue());
      
      return (variable);
   }
   
   public Variable(String name)
   {
      this.name = name;
      value = "";
   }
   
   public <T> Variable(String name, T value)
   {
      this.name = name;
      this.value = value.toString();
   }
   
   public String getName()
   {
      return (name);
   }
   
   public boolean asBool()
   {
      return (Boolean.valueOf(value));
   }
   
   public double asDouble()
   {
      double doubleValue = 0;
      
      try
      {
         doubleValue = Double.valueOf(value);
      }
      catch (NumberFormatException e)
      {
         // TODO.
      }
      
      return (doubleValue);
   }
   
   public int asInt()
   {
      int intValue = 0;
      
      try
      {
         intValue = Integer.valueOf(value);
      }
      catch (NumberFormatException e)
      {
         // TODO.
      }
      
      return (intValue);
   }
   
   public float asFloat()
   {
      float floatValue = 0;
      
      try
      {
         floatValue = Float.valueOf(value);
      }
      catch (NumberFormatException e)
      {
         // TODO.
      }
      
      return (floatValue);
   }
   
   public String asString()
   {
      return (value);
   }

   private String name;
   
   private String value;
}
