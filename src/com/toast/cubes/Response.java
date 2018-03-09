package com.toast.cubes;

import com.toast.xml.XmlNode;
import com.toast.xml.exception.XmlFormatException;

public class Response
{
   public static Response load(XmlNode node) throws XmlFormatException
   {
      Response response = new Response();
      
      if (node.hasChild("text"))
      {
         response.text = node.getChild("text").getValue();
      }
      
      if (node.hasChild("code"))
      {
         response.code = node.getChild("code").getValue();
      }
      
      return (response);
   }
   
   public GameAction getParent()
   {
      return (parent);
   }
   
   public void setParent(GameAction parent)
   {
      this.parent = parent;
   }
   
   public String getText()
   {
      return (text);
   }
   
   public String getCode()
   {
      return (code);
   }
   
   private GameAction parent;
   
   private String text = "";
   
   private String code = "";
}
