package Goldsack.DiscoSheep;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * DiscoSettings allows to read a config file and call settings by its name.
 * @author goldsack
 *
 */
public class DiscoSettings { 
	
	private final DiscoSheep plugin;
	@Getter private final boolean dropItems;
	@Getter private final int sheepNumber;
	@Getter private final int maxSheepNumber;
	@Getter private final boolean explosion;
	@Getter private final int creeperNumber;
	@Getter private final int maxCreeperNumber;
	@Getter private final int ghastNumber;
	@Getter private final int maxGhastNumber;
	@Getter private final int defaultPartyTime;
	@Getter private final int maxPartyTime;
	@Getter private final int spawnDistance;
	@Getter private final int maxSpawnDistance;

	public DiscoSettings(DiscoSheep plugin)
	{
		this.plugin = plugin;
		FileConfiguration config = plugin.getConfig();
		File file = new File("plugins/DiscoSheep/config.yml");
		if(!file.exists())
			plugin.saveDefaultConfig();
		try {
			config.load(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		dropItems = config.getBoolean("dropItems");
		sheepNumber = config.getInt("sheepNumber");
		maxSheepNumber = config.getInt("maxSheepNumber");
		explosion = config.getBoolean("explosion");
		creeperNumber = config.getInt("creeperNumber");
		maxCreeperNumber = config.getInt("maxCreeperNumber");
		ghastNumber = config.getInt("ghastNumber");
		maxGhastNumber = config.getInt("maxGhastNumber");
		defaultPartyTime = config.getInt("defaultPartyTime");
		maxPartyTime = config.getInt("maxPartyTime");
		spawnDistance = config.getInt("spawnDistance");
		maxSpawnDistance = config.getInt("maxSpawnDistance");
		if(!settingsValidation())
		{
			throw new RuntimeException ("Malformed configuration. Make sure default options are below their maximum settings.");
		}
	}

	private boolean settingsValidation() {
		return getDefaultPartyTime() < getMaxPartyTime() &&
				getSheepNumber() < getMaxSheepNumber() &&
				getCreeperNumber() < getMaxCreeperNumber() &&
				getGhastNumber() < getMaxGhastNumber();
	}

	
}
