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
      return (Double.valueOf(value));
   }
   
   public int asInt()
   {
      return (Integer.valueOf(value));
   }
   
   public float asFloat()
   {
      return (Float.valueOf(value));
   }
   
   public String asString()
   {
      return (value);
   }

   private String name;
   
   private String value;
}
