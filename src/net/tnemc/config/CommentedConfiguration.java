package net.tnemc.config;

import com.hellyard.cuttlefish.CuttlefishBuilder;
import com.hellyard.cuttlefish.composer.yaml.YamlComposer;
import com.hellyard.cuttlefish.grammar.yaml.YamlNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by creatorfromhell.
 *
 * The New Config Library Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class CommentedConfiguration extends ConfigSection {

  private File realFile = null;
  private Reader file = null;
  private Reader defaults = null;

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   */
  public CommentedConfiguration(final File file, final File defaults) {
    this(file, defaults, false);
  }

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   * @param debug Whether or not to enter debug mode.
   */
  public CommentedConfiguration(final File file, final File defaults, boolean debug) {
    super(null);
    this.realFile = file;
    try {
      this.file = new FileReader(file);
    } catch(FileNotFoundException ignore) { }
    if(defaults != null) {
      try {
        this.defaults = new FileReader(defaults);
      } catch(FileNotFoundException ignore) { }
    }

    this.debug = debug;
  }

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   */
  public CommentedConfiguration(final File file, Reader defaults) {
    this(file, defaults, false);
  }

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   * @param debug Whether or not to enter debug mode.
   */
  public CommentedConfiguration(final File file, Reader defaults, boolean debug) {
    super(null);

    //System.out.println("Constructor");
    this.realFile = file;
    //System.out.println("Constructor1");
    try {
      //System.out.println("Constructor2");
      this.file = new FileReader(file);
    } catch(FileNotFoundException ignore) { }
    //System.out.println("Constructor3");
    this.defaults = defaults;
    //System.out.println("Constructor4");
    this.debug = debug;
  }

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   */
  public CommentedConfiguration(Reader file, Reader defaults) {
    this(file, defaults, false);
  }

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   * @param debug Whether or not to enter debug mode.
   */
  public CommentedConfiguration(Reader file, Reader defaults, boolean debug) {
    super(null);
    this.file = file;
    this.defaults = defaults;
    this.debug = debug;
  }

  /**
   * Loads our configurations, reading the defaults file if needed.
   */
  public void load() {

    //System.out.println("Preparing loader");
    load(true);
  }

  protected void decodeNodes(LinkedList<YamlNode> nodes) {

    for(YamlNode node : nodes) {

      ConfigSection finished = new ConfigSection(node);
      final String[] split = node.getNode().split("\\.");

      ConfigSection parent = this;

      if(split.length > 1) {
        for(int i = 0; i < split.length; i++) {

          if(i == (split.length - 1)) {
            parent.children.put(split[i], finished);
          } else {
            parent = parent.getSection(split[i]);
          }
        }
      } else {
        children.put(node.getNode(), finished);
      }
    }
  }

  /**
   * Loads our configurations, copying over defaults that are not present in our file if needed.
   */
  public void load(boolean copyDefaults) {

    //System.out.println("Preparing loader");
    load(copyDefaults, new ArrayList<>());
  }

  public void load(boolean copyDefaults, List<String> ignore) {
    Reader load = file;

    //System.out.println("Preparing loader");

    final LinkedList<YamlNode> loaded = (file == null)? new LinkedList<>() : (LinkedList<YamlNode>)new CuttlefishBuilder(load, "yaml").build().getNodes();

    //System.out.println("Loaded");

    if(copyDefaults && defaults != null) {

      //System.out.println("file == null?: " + (file == null));
      //System.out.println("Loaded: " + loaded.size());

      if(file == null || loaded.size() == 0) {

        //System.out.println("wut major");
        LinkedList<YamlNode> copied = new LinkedList<>();
        final CommentedConfiguration defaultConfig = new CommentedConfiguration(defaults, null);
        defaultConfig.load(false);
        //System.out.println("copied: " + copied.size());

        for(YamlNode yamlNode : defaultConfig.getNodeValues()) {
          //System.out.println("Node: " + yamlNode.getNode());
          if(!loaded.contains(yamlNode)) {
            //System.out.println("wut");
            if(!ignored(ignore, yamlNode.getNode())) {
              //System.out.println("wut2");
              copied.add(yamlNode);
            }
          } else {
            //System.out.println("wut3");
            copied.add(loaded.get(loaded.indexOf(yamlNode)));
          }
        }
        decodeNodes(copied);
      } else {
        decodeNodes(loaded);
      }
    } else {
      decodeNodes(loaded);
    }
    if(realFile != null) {
      save(realFile);
    }
  }

  private boolean ignored(List<String> ignore, String node) {
    for(String str : ignore) {
      if(node.contains(str) || node.equalsIgnoreCase(str)) return true;
    }
    return false;
  }

  /**
   * Used to save our configuration file.
   * @param file The file to save our configuration to.
   * @return True if saved, otherwise false.
   */
  public boolean save(File file) {
    if(!file.exists()) {
      try {
        file.createNewFile();
      } catch(Exception ignore) {
        return false;
      }
    }
    return new YamlComposer().compose(file, getNodeValues());
  }
}