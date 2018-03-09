package com.toast.cubes;

import java.util.Random;

import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class Response
{
   public static Response load(XmlNode node) throws XmlFormatException
   {
      Response response = new Response();
      
      XmlNodeList childNodes = node.getChildren("text");
      
      response.text = new String[childNodes.size()];
      
      int i = 0;
      for (XmlNode childNode : childNodes)
      {
         response.text[i] = childNode.getValue();
         i++;
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
      String responseText = "";
      
      if (text.length > 0)
      {
         int index = new Random().nextInt(text.length);
         responseText = text[index];
      }
      
      return (responseText);
   }
   
   public String getCode()
   {
      return (code);
   }
   
   private GameAction parent;
   
   private String[] text;
   
   private String code = "";
}
