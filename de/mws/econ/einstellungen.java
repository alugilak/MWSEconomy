package de.mws.econ;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class einstellungen
{
  static einstellungen instanz = new einstellungen();
  Plugin p;
  FileConfiguration data;
  File dfile;
  FileConfiguration vdata;
  File vfile;
  
  public static einstellungen getInstance()
  {
    return instanz;
  }
  
  public void setup(Plugin p)
  {
    if (!p.getDataFolder().exists()) {
      p.getDataFolder().mkdir();
    }
    this.dfile = new File(p.getDataFolder(), "pItembank.yml");
    this.vfile = new File(p.getDataFolder(), "vItembank.yml");
    if (!this.dfile.exists()) {
      try
      {
        this.dfile.createNewFile();
      }
      catch (IOException d)
      {
        Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann die datei nicht lesen lang.yml!");
      }
    }
    this.data = YamlConfiguration.loadConfiguration(this.dfile);
    {
  if (!this.vfile.exists()) {
      try
      {
        this.vfile.createNewFile();
      }
      catch (IOException d)
      {
        Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann die datei nicht lesen lang.yml!");
      }
    }
    this.vdata = YamlConfiguration.loadConfiguration(this.vfile);
  }}
  
  public FileConfiguration getData()
  {
    return this.data;
  }
  
  public void saveData()
  {
    try
    {
      this.data.save(this.dfile);
    }
    catch (IOException d)
    {
      Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann Datei nicht speichern lang.yml!");
    }
  }
  public FileConfiguration getvData()
  {
    return this.vdata;
  }
  
  public void savevData()
  {
    try
    {
      this.vdata.save(this.vfile);
    }
    catch (IOException d)
    {
      Bukkit.getServer().getLogger().severe(ChatColor.RED + "Kann Datei nicht speichern lang.yml!");
    }
  }
  
  public PluginDescriptionFile getDesc()
  {
    return this.p.getDescription();
  }
  
  public void reloadData()
  {
    this.data = YamlConfiguration.loadConfiguration(this.dfile);
  }
  public void reloadvData()
  {
    this.vdata = YamlConfiguration.loadConfiguration(this.vfile);
  }
}
