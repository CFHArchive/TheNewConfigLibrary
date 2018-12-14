package net.tnemc.config;

import com.hellyard.cuttlefish.grammar.yaml.YamlNode;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by creatorfromhell.
 *
 * The New Config Library Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class ConfigSection {

  private YamlNode baseNode;

  protected LinkedHashMap<String, ConfigSection> children = new LinkedHashMap<>();

  /**
   * Constructor for {@link ConfigSection}.
   * @param baseNode The YamlNode associated with this {@link ConfigSection}.
   */
  public ConfigSection(YamlNode baseNode) {
    this.baseNode = baseNode;
  }

  /**
   * Returns the {@link YamlNode node} associated with this {@link ConfigSection}.
   * @return The {@link YamlNode node} associated with this {@link ConfigSection}.
   */
  public YamlNode getBaseNode() {
    return baseNode;
  }

  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection}, which are
   * only 1 level deep in indentation from this {@link ConfigSection}.
   * @return A String Set of the child nodes.
   */
  public Set<String> getKeys() {
    return getKeys(false);
  }


  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection}.
   * @return A String Set of the child nodes.
   */
  public Set<String> getKeys(boolean deep) {
    LinkedHashSet<String> keys = new LinkedHashSet<>();

    for(YamlNode node : getNodeValues()) {
      String keyStr = (baseNode == null)? node.getNode() : node.getNode().replace(baseNode.getNode() + ".", "");

      if(!deep) {
        keyStr = keyStr.split("\\.")[0];
      }

      if(!keys.contains(keyStr)) keys.add(keyStr);
    }
    return keys;
  }

  /**
   * Used to check if this {@link ConfigSection} contains the specified child node.
   * @param node The node to check for.
   * @return True if the node exists, otherwise false.
   */
  public boolean contains(String node) {
    return getSection(node) != null;
  }


  /**
   * Returns the {@link ConfigSection section} associated with the specified string node if it exists, otherwise
   * returns null.
   * @param node The string node to use for the search.
   * @return The {@link ConfigSection section} associated with the specified string node if it exists, otherwise
   * returns null
   */
  public ConfigSection getSection(String node) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        return section.children.get(str);
      } else {
        if(section == null) break;
        section = section.children.get(str);
      }
    }
    return section;
  }

  public void set(String node, String... values) {
    getNode(node).set(values);
  }

  /**
   * Returns the {@link YamlNode node} associated with the specified string node if it exists, otherwise
   * returns null.
   * @param node The string node to use for the search.
   * @return The {@link YamlNode node} associated with the specified string node if it exists, otherwise
   * returns null
   */
  public YamlNode getNode(String node) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        return section.children.get(str).getBaseNode();
      } else {
        if(section == null) break;
        section = section.children.get(str);
      }
    }
    if(section == null) return null;
    return section.getBaseNode();
  }

  /**
   * Used to get all child {@link YamlNode nodes} of this one.
   * @return A LinkedList of all child {@link YamlNode nodes}.
   */
  public LinkedList<YamlNode> getNodeValues() {
    LinkedList<YamlNode> nodeValues = new LinkedList<>();

    for(ConfigSection node : children.values()) {
      nodeValues.add(node.getBaseNode());
      if(node.children.size() > 0) {
        nodeValues.addAll(node.getNodeValues());
      }
    }
    return nodeValues;
  }

  /**
   * Adds a new {@link ConfigSection section} under this one.
   * @param section The {@link ConfigSection section} to add.
   */
  public void createSection(ConfigSection section) {
    final String[] split = section.getBaseNode().getNode().split("\\.");

    ConfigSection parent = this;

    if(split.length > 1) {
      for(int i = 0; i < split.length; i++) {

        if(i == (split.length - 1)) {
          parent.children.put(split[i], section);
        } else {
          parent = parent.getSection(split[i]);
        }
      }
    } else {
      children.put(section.getBaseNode().getNode(), section);
    }
  }

  /**
   * Checks to see if a node is associated with a configuration section, i.e. a {@link YamlNode} with
   * no values, but rather only contains children.
   * @param node The string node to use in the check.
   * @return True if the {@link ConfigSection section} with the specified string node exists, and the
   * associated {@link YamlNode node} contains no values.
   */
  public boolean isConfigurationSection(String node) {
    final ConfigSection section = getSection(node);
    if(section == null) return false;

    return section.getBaseNode().getValues().size() == 0;
  }

  public int getInt(String node) {
    return getInt(node, 0);
  }

  public int getInt(String node, int def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Integer.valueOf(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public boolean getBool(String node) {
    return getBool(node, false);
  }

  public boolean getBool(String node, boolean def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Boolean.valueOf(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public double getDouble(String node) {
    return getDouble(node, 0.0);
  }

  public double getDouble(String node, double def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Double.valueOf(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public short getShort(String node) {
    return getShort(node, (short)0);
  }

  public short getShort(String node, short def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Short.valueOf(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public float getFloat(String node) {
    return getFloat(node, 0.0f);
  }

  public float getFloat(String node, float def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Float.valueOf(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public BigDecimal getBigDecimal(String node) {
    return getBigDecimal(node, BigDecimal.ZERO);
  }

  public BigDecimal getBigDecimal(String node, BigDecimal def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return new BigDecimal(section.getBaseNode().getValues().getFirst());
    } catch(Exception ignore) {
      return def;
    }
  }

  public String getString(String node) {
    return getString(node, "");
  }

  public String getString(String node, String def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;
    
    return section.getBaseNode().getValues().getFirst();
  }

  public LinkedList<String> getStringList(String node) {
    final ConfigSection section = getSection(node);
    if(section == null) return new LinkedList<>();
    
    return section.getBaseNode().getValues();
  }
}