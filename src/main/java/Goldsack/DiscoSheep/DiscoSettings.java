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
		try {
			File file = new File("plugins/DiscoSheep/config.yml");
			if(!file.exists())
			{
				File folder = new File("plugins/DiscoSheep");
				if(!folder.exists())
					folder.mkdirs();

				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.yml")));
				while(reader.ready())
					writer.write(reader.readLine() + "\n");
	            writer.flush();
				reader.close();
				writer.close();
			}
			plugin.getConfig().load(new File("plugins/DiscoSheep/config.yml"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		dropItems = plugin.getConfig().getBoolean("dropItems");
		sheepNumber = plugin.getConfig().getInt("sheepNumber");
		maxSheepNumber = plugin.getConfig().getInt("maxSheepNumber");
		explosion = plugin.getConfig().getBoolean("explosion");
		creeperNumber = plugin.getConfig().getInt("creeperNumber");
		maxCreeperNumber = plugin.getConfig().getInt("maxCreeperNumber");
		ghastNumber = plugin.getConfig().getInt("ghastNumber");
		maxGhastNumber = plugin.getConfig().getInt("maxGhastNumber");
		defaultPartyTime = plugin.getConfig().getInt("defaultPartyTime");
		maxPartyTime = plugin.getConfig().getInt("maxPartyTime");
		spawnDistance = plugin.getConfig().getInt("spawnDistance");
		maxSpawnDistance = plugin.getConfig().getInt("maxSpawnDistance");
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
