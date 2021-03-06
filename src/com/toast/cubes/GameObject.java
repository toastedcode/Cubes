package com.toast.cubes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.toast.xml.XmlNode;
import com.toast.xml.XmlNodeList;
import com.toast.xml.exception.XmlFormatException;

public class GameObject
{
   public enum Property
   {
      VISIBLE("visible", 0x0001, true),
      PORTABLE("portable", 0x0002, false);
      
      Property(String name, int mask, boolean defaultValue)
      {
         this.name = name;
         this.mask = mask;
         this.defaultValue = defaultValue;
      }
      
      String getName()
      {
         return (name);
      }
      
      int getMask()
      {
         return (mask);
      }
      
      boolean getDefaultValue()
      {
         return (defaultValue);
      }
      
      private String name;
      
      private int mask;
      
      private boolean defaultValue;
   };
   
   public static void addToDictionary(String object)
   {
      dictionary.add(object);
   }
   
   public static boolean inDictionary(String object)
   {
      return dictionary.contains(object);
   }
   
   public static GameObject load(XmlNode node) throws XmlFormatException
   {
      GameObject object = new GameObject(node.getAttribute("name").getValue());
      
      //
      // Aliases
      //
      
      XmlNodeList childNodes = node.getChildren("alias");
      
      for (XmlNode aliasNode : childNodes)
      {
         String alias = aliasNode.getValue().toLowerCase();
         
         object.aliases.add(alias);
         addToDictionary(alias);
      }

      //
      // Description, Details
      //
      
      if (node.hasChild("description"))
      {
         object.description = node.getChild("description").getValue();
      }
      else
      {
         object.description = "It's indiscribable.";
      }
      
      if (node.hasChild("details"))
      {
         object.details = node.getChild("details").getValue();
      }

      //
      // Actions
      //
      
      childNodes = node.getChildren("action");

      for (XmlNode actionNode : childNodes)
      {
         GameAction action = GameAction.load(actionNode);
         
         if (action != null)
         {
            object.actions.add(action);
            action.setParent(object);
         }
      }
      
      //
      // Child objects
      //
      
      childNodes = node.getChildren("object");

      for (XmlNode objectNode : childNodes)
      {
         GameObject childObject = GameObject.load(objectNode);
         
         if (childObject != null)
         {
            object.add(childObject);
         }
      }
      
      //
      // Variables
      //
      
      childNodes = node.getChildren("variable");

      for (XmlNode objectNode : childNodes)
      {
         Variable variable = Variable.load(objectNode);
         
         if (variable != null)
         {
            object.variables.put(variable.getName(), variable);
         }
      }
      
      //
      // Properties
      //
      
      for (Property property : Property.values())
      {
         boolean value = property.getDefaultValue();
         
         if (node.hasAttribute(property.getName()))
         {
            value = node.getAttribute(property.getName()).getBoolValue();
         }
         
         if (value)
         {
            object.setProperty(property);
         }
         else
         {
            object.clearProperty(property);
         }
      }
      
      return (object);
   }
   
   public GameObject(String name)
   {
      this.name = name;
      addToDictionary(name);
   }
   
   public String getName()
   {
      return (name);
   }
   
   public GameObject getParent()
   {
      return (parent);
   }
   
   public boolean match(String objectName)
   {
      return ((name.equalsIgnoreCase(objectName)) ||
              (aliases.contains(objectName.toLowerCase())));
   }
   
   public Response handleCommand(Command command)
   {
      Response response = null;
      
      // First, try to match this object, by name.
      if (match(command.getObject()))
      {
         for (GameAction action : actions)
         {
            if (action.canHandle(command))
            {
               response = action.getResponse();
               break;
            }
         }
      }
      
      // Next, see if any child objects can handle it.
      if (response == null)
      {
         for (GameObject object : objects.values())
         {
            response = object.handleCommand(command);
            
            if (response != null)
            {
               break;
            }
         }         
      }
      
      // Finally, if it's a top level object, try default actions.
      if ((response == null) && (parent == null))
      {
         for (GameAction action : actions)
         {
            if (action.canHandle(command))
            {
               response = action.getResponse();
               break;
            }
         }
      }
      
      return (response);
   }
   
   public String getDescription()
   {
      return (description);
   }
   
   public String getDetails()
   {
      return (details);
   }
   
   public boolean isProperty(Property property)
   {
      return ((properties & property.getMask()) > 0);
   }
   
   public void setProperty(Property property)
   {
      properties |= property.getMask();
   }
   
   public void clearProperty(Property property)
   {
      properties &= ~(property.getMask());
   }
   
   public boolean isVisible()
   {
      return (isProperty(Property.VISIBLE));
   }
   
   public void setVisible(boolean visible)
   {
      if (visible)
      {
         setProperty(Property.VISIBLE);
      }
      else
      {
         clearProperty(Property.VISIBLE);
      }
   }
   
   public String listContents()
   {
      StringBuilder builder = new StringBuilder();
      
      for (GameObject object : objects.values())
      {
         if (object.isProperty(Property.VISIBLE))
         {
            builder.append(object.getDescription());
            builder.append("\n");
         }
      }
      
      return (builder.toString());      
   }
   
   public <T> void setVar(String name, T value)
   {
      variables.put(name, new Variable(name, value));
   }
   
   public Variable getVar(String name)
   {
      Variable variable = new Variable("unknown");
      
      if (variables.containsKey(name))
      {
         variable = variables.get(name);
      }
      
      return (variable);
   }
   
   public boolean contains(String objectName)
   {
      boolean containsObject = false;
      
      for (GameObject object : objects.values())
      {
         if (object.match(objectName))
         {
            containsObject = true;
            break;
         }
      }
      
      return (containsObject);
   }
   
   public boolean contains(GameObject object)
   {
      return (objects.containsValue(object));
   }
   
   public GameObject get(String query)
   {
      GameObject foundObject = null;
      
      if (!query.isEmpty())
      {
         StringTokenizer tokenizer = new StringTokenizer(query, ".");
         
         String token = tokenizer.nextToken();
         
         for (GameObject object : objects.values())
         {
            if (object.match(token))
            {
               foundObject = object;
               break;
            }
         }
         
         if ((foundObject != null) && tokenizer.hasMoreTokens())
         {
            foundObject = foundObject.get(tokenizer.nextToken("")); 
         }
      }
      
      return (foundObject);
   }
   
   public void add(GameObject object)
   {
      objects.put(object.getName(), object);
      object.parent = this;
   }
   
   public void remove(GameObject object)
   {
      objects.remove(object.getName());
      object.parent = null;
   }
   
   public Map<String, GameObject> getObjects()
   {
      return (objects);
   }
   
   public List<GameAction> getActions()
   {
      return (actions);
   }
   
   private static Set<String> dictionary = new HashSet<>();
   
   private GameObject parent;
   
   private List<GameAction> actions = new ArrayList<>();
   
   private Map<String, GameObject> objects = new HashMap<>();
   
   private Map<String, Variable> variables = new HashMap<>();
   
   private String name = "";
   
   private List<String> aliases = new ArrayList<>();
   
   private String description = "";
   
   private String details = "";
   
   private int properties;
}
