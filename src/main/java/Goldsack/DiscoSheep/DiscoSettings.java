package Goldsack.DiscoSheep;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Supportclass to help control different settings in DiscoSetting. Cast to BoolS, IntS or FloS to use method getV();
 * @author goldsack
 */
class Setting{
	//String settingName, settingRaw, comment
	String sN, sR, c;
	public Setting(String sN, String sR, String c) {
		this.sN = sN;
		this.sR = sR;
		this.c = c;
	}
	/** Get settingName	 */
	public String getsN() { return sN;}
	/** Get settingRaw	 */
	public String getsR() { return sR;}
	/** Get comment	 */
	public String getC() {  return c; }
	
}
/**
 * Supportclass to help control different settings in DiscoSetting. Includes boolean value
 * @author goldsack
 */
class BoolS extends Setting{
	boolean v = false;
	public BoolS(String sN, String sR, String c) {
		super(sN, sR, c);
	}
	public void setV(boolean v) {this.v = v;};
	public boolean getV(){return v;}
}
/**
 * Supportclass to help control different settings in DiscoSetting. Includes int value
 * @author goldsack
 */
class IntS extends Setting{
	int v = 1;
	public IntS(String sN, String sR, String c) {
		super(sN, sR, c);
	}
	public void setV(int v) {this.v = v;};
	public int getV(){return v;}
}
/**
 * Supportclass to help control different settings in DiscoSetting. Includes float value
 * @author goldsack
 */
class FloS extends Setting{
	float v = 1;
	public FloS(String sN, String sR, String c) {
		super(sN, sR, c);
	}
	public void setV(float v) {this.v = v;};
	public float getV(){return v;}
}
/**
 * DiscoSettings allows to read a config file and call settings by its name.
 * @author goldsack
 *
 */
public class DiscoSettings { 
	
	private DiscoSheep plugin;
	private Properties configFile;
	private File file = new File("plugins/DiscoSheep.properties");
	public HashMap<String, Setting> hash = new HashMap<String, Setting>();

	Setting[] settings = {
		new BoolS("dropItems", 			"true", "#Set true or false if you want players to be able to loot items from creatures (Blocks is not yet affected by this variable)\n" +
												"#Be aware this is only for creatures the plugin spawns.\n" +
												"#It does not stop looting colored wool from natural spawned sheeps that has changed color with \"/ds color\"\n"),
		new IntS("sheepNumber", 		"15", 	"#Set how many sheeps a party gets by default\n"),
		new IntS("maxSheepNumber", 		"25", 	"#Set max number of sheeps a player can demand to be used in party.\n" +
												"#This is per player so if you have 20 people logged in it will spawn 20*n sheeps and could slow down servers with hundreds of sheeps\n"),
		new BoolS("explosion",			"false","#Creeper spawned will not explode. Be aware that naturally spawned creepers will blow up your face.\n"),
		new IntS("creeperNumber", 		"3", 	"#Set how many creepers a party gets by default\n"),
		new IntS("maxCreeperNumber", 	"10", 	"#Set max number of creeper a player can demand to be used in party. This is per player\n"),
		new IntS("ghastNumber", 		"0", 	"#(GHASTS DO NOT WORK. I will try to fix it later)Ghasts are set to 0 since they will spew exploding fireballs\n"),
		new IntS("maxGhastNumber", 		"0", 	"#(GHASTS DO NOT WORK. I will try to fix it later)Set max number of ghasts a player can demand to be used in party. This is per player\n"),
		new IntS("defaultPartyTime", 	"10", 	"#Sets time duration in seconds for how long a party will last on default\n"),
		new IntS("maxPartyTime", 		"120", 	"#Sets max time duration in seconds for how long a party can last\n"),
		new IntS("spawnDistance", 		"5", 	"#Sets how big the spawn distance around the player is in blocks. Creatures spawns in a square\n"),
		new IntS("maxSpawnDistance", 	"15", 	"#Sets the max spawn distance around the player is in blocks. Creatures spawns in a square\n"),
	};
	
	
	public DiscoSettings(DiscoSheep discoSheep) {
		plugin = discoSheep;
		configFile = new Properties();
		
		initProperties();

		
		
	}
	private void initProperties() {
		//Set default variables and put setting in hashmap
		for (Setting s: settings) {
			setData(s, s.sR);
			hash.put(s.sN, s);
		}

		//If file exists, read file
		if(checkSettingsFile()){
			readSettingsFile();
		}
		else{
			//Else, create new file
			createSettingsFile();
		}
		
		settingsValidation();
		configFile.clear();
	}
	/**
	 * Checks if some settings contradict each others 
	 */
	private void settingsValidation() {
		
		try {
			setUnderLimit((IntS) getSetting("defaultPartyTime"), (IntS) getSetting("maxPartyTime"));
			setUnderLimit((IntS) getSetting("sheepNumber"), (IntS) getSetting("maxSheepNumber"));
			setUnderLimit((IntS) getSetting("creeperNumber"), (IntS) getSetting("maxCreeperNumber"));
			setUnderLimit((IntS) getSetting("ghastNumber"), (IntS) getSetting("maxGhastNumber"));
			
		} catch (NullPointerException e) {
			System.out.println("[DiscoSheep] Nullpointer in settingsValidation. This is not your fault, but is plugin developers fault. Tell him you got this error");
		}

		
	}
	
	private void setUnderLimit(IntS normal, IntS max){
		if(normal.getV() > max.getV()){
			System.out.println(
					"[DiscoSheep] " + normal.getsN() + " is bigger then " + max.getsN() + " in DiscoSheep.properties. " +
					normal.getsN() + " is set to " + max.getsN() );
			normal.setV(max.getV());
		}
	}
	private void createSettingsFile() {
		
		try {
			StringBuilder writeString = new StringBuilder();
			
			//Add config title
			writeString.append("#Discosheep config file\n\n");
			
			//Add comments, variableName and variableRaw
			for (Setting s: settings) {
				writeString.append("\n" + s.getC() + s.getsN() + "=" + s.getsR());
			}
			
			//Write file
			FileWriter fWrit = new FileWriter(file);
			fWrit.write(writeString.toString());
			fWrit.close();
			System.out.println("[DiscoSheep] Created a new DiscoSheep.properties file.");
			
		} catch (IOException e) {
			System.out.println("[Discosheep] Failed to create DiscoSheep.properties file. Has plugin folder been moved or don't have permission to write?");
		}
		
		
	}
	
	/**
	 * Read in settings from configFile. 
	 */
	private void readSettingsFile() {
		FileReader fRead = null;
		
		try {
			fRead = new FileReader(file);
			configFile.load(fRead);
			
			for(Setting s : settings){
				if(configFile.containsKey(s.sN)){
					setData(hash.get(s.sN), configFile.getProperty(s.sN));
				}
				else{
					System.out.println("DiscoSheep.properties does not contain setting \"" + s.sN + "\". Will use built in default: " + s.sR);
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("[Discosheep] DiscoSheep.properties file not found. Will use default settings");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[Discosheep] IOException on reading settings file. Will use default settings");
			e.printStackTrace();
		}
		try {
			if(fRead != null){
				fRead.close();				
			}
		} catch (IOException e) {
			System.out.println("[Discosheep] IOException on closing settings file. File can not be moved while program is on");

		}
		
	}

	private boolean checkSettingsFile() {

		if(file.exists()){
			if(file.canRead()){
				return true;
			}else{
				System.out.println("[DiscoSheep] Is not allowed to read DiscoSheep.properties file.");
				return false;
			}
		}
		System.out.println("[DiscoSheep] Did not find DiscoSheep.properties file.");
		return false;
	}
	public void reload() {
		initProperties();
	}
	/**
	 * Sets the correct data into BoolS, IntS and FloS
	 */
	public void setData(Setting setting, String settingRaw){
		
		if(setting == null){
			System.out.println("[DiscoSheep] recived a Setting that was null in DiscoSettings.setData(), this will make [DiscoSheep] malfunction. " +
			"If you get this message, tell the plugindeveloper what you did so he can fix it.");
			return;
		}
		
		if(settingRaw == null){
			System.out.println("[DiscoSheep] recived a String that was null in DiscoSettings.setData(), this will make [DiscoSheep] malfunction. " +
			"If you get this message, tell the plugindeveloper what you did so he can fix it.");
			return;
		}
		
		if(setting instanceof BoolS){	
			if(settingRaw.equalsIgnoreCase("true") || settingRaw.equalsIgnoreCase("1") || settingRaw.equalsIgnoreCase("on")){
				((BoolS) setting).setV(true);
			}
			else if(settingRaw.equalsIgnoreCase("false") || settingRaw.equalsIgnoreCase("0") || settingRaw.equalsIgnoreCase("off")){
				((BoolS) setting).setV(false);
			}
			else{
				System.out.println("[DiscoSheep] Failed to convert " + settingRaw + " from " + setting.getsN() + " value into a boolean from file DiscoSheep.properties. " +
						"Use on/off, true/false or 1/0 as value. " + setting.getsN() + " has been set to default value " + setting.getsR().equalsIgnoreCase("true") +".");
				((BoolS) setting).setV(setting.getsR().equalsIgnoreCase("true"));
			}
		}
		else if(setting instanceof IntS){
			try {
				((IntS)setting).setV(Integer.parseInt(settingRaw));
			} catch (NumberFormatException e) {
				System.out.println("[DiscoSheep] Failed to convert " + settingRaw + " from " + setting.getsN() + " value into a whole number from file DiscoSheep.properties");
				
			}	
		}
		else if(setting instanceof FloS){
			try {
				((FloS)setting).setV(Float.parseFloat(settingRaw));
			} catch (NumberFormatException e) {
				System.out.println("[DiscoSheep] Failed to convert " + settingRaw + " from " + setting.getsN() + " value into a decimal number from file DiscoSheep.properties");
				
			}	
		}
		else{
			System.out.println("[DiscoSheep] error. Please tell the plugindeveloper that he has forgot to include support for " + setting.getClass().getName());
		}		
	}
	
	public Setting getSetting(String settingName){
		Setting result = hash.get(settingName);
		if(result == null){
			System.out.println("[DiscoSheep] error! getSetting in DiscoSheep can't find setting \"" + settingName + "\n");
		}
		return result;
	}
	
}
