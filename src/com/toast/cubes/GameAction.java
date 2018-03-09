package com.toast.cubes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class GameAction
{
   public static void addToDictionary(String action)
   {
      dictionary.add(action);
   }
   
   public static boolean inDictionary(String action)
   {
      return dictionary.contains(action);
   }
   
   public static GameAction load(XmlNode node) throws XmlFormatException
   {
      GameAction action = new GameAction(node.getAttribute("name").getValue());
      
      XmlNodeList childNodes = node.getChildren("alias");

      for (XmlNode aliasNode : childNodes)
      {
         String alias = aliasNode.getValue().toLowerCase();
         
         action.aliases.add(alias);
         addToDictionary(alias);
      }
      
      XmlNode responseNode = node.getChild("response");
      action.response = Response.load(responseNode);
      action.response.setParent(action);
      
      return (action);
   }
   
   public GameAction(String name)
   {
      this.name = name;
      addToDictionary(name);
   }
   
   public GameObject getParent()
   {
      return (parent);
   }
   
   public void setParent(GameObject parent)
   {
      this.parent = parent;
   }
   
   public boolean canHandle(Command command)
   {
      return (command.getAction().equals(name) || aliases.contains(command.getAction()));
   }
   
   public Response getResponse()
   {
      return (response);
   }
   
   private static Set<String> dictionary = new HashSet<>();
   
   private String name;
   
   private List<String> aliases = new ArrayList<>();
   
   private Response response;
   
   private GameObject parent;
}
