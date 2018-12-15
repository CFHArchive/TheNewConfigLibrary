package test.config;

import net.tnemc.config.CommentedConfiguration;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by creatorfromhell.
 *
 * The New Config Library Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class MainTest {

  public static void main(String[] args) {
    final File defaults = Paths.get("C:\\Users\\Daniel\\Desktop\\Minecraft\\spigot2\\plugins\\TheNewEconomy\\worlds.yml").toFile();
    final File file = Paths.get("C:\\Users\\Daniel\\Desktop\\Minecraft\\spigot2\\plugins\\TheNewEconomy\\currencytest.yml").toFile();

    //We defined a new CommentedConfiguration instance with our file, and a file of our default configurations.
    CommentedConfiguration config = new CommentedConfiguration(file, defaults);

    //We now load our configurations.
    config.load();

    //Simple Contains call that should return false.
    System.out.println(config.isConfigurationSection("Worlds.world.Currencies"));
    System.out.println(config.getSection("Currencies").getKeys(false));
  }
}