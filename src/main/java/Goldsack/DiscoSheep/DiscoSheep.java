package Goldsack.DiscoSheep;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

/**
 * DiscoSheep is a plugin to minecraft servers running minecraft.
 * It changes sheep wool color for fun. 
 * @author goldsack
 *
 */
public class DiscoSheep extends JavaPlugin implements ActionListener{
	protected DiscoListenerEntity entityListener = new DiscoListenerEntity(this);
	protected DiscoListenerBlock blockListener = new DiscoListenerBlock(this);
	protected DiscoParty discoParty;
	protected DiscoSettings settings;
	protected DiscoPermission permit;
	
	protected Timer timer;

	/**
	 * Called when plugin starts.
	 */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();	
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.EXPLOSION_PRIMED, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( "[" + pdfFile.getName() + "] version " + pdfFile.getVersion() + " is enabled!" );
		
		settings = new DiscoSettings(this);
		discoParty = new DiscoParty(this);
		permit = new DiscoPermission(this);
		discoParty.start();
		timer = new Timer(((IntS)settings.getSetting("defaultPartyTime")).getV(), this);
		timer.stop(); //Timer starts when its created, so it has to be stopped
		
	}
	
	/**
	 * Called when plugin ends.
	 * Cancels all threads, and wait for them to end.
	 */
	@Override
	public void onDisable() {
		stopParty();
		discoParty.end();
		try {
			discoParty.join(2000);
		} catch (InterruptedException e) {
			System.out.println("[Discosheep] failed to end thread. Did not close down properly");
		}
		System.out.println("[DiscoSheep] closed down. Good bye");
	}
	
	/**
	 * Called when /discosheep is sent in game or server
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {
		
		if(args.length == 0){
			if(permit.isPermittet(sender, permit.HELP, true)){
				return false; //Use plugin.yml to display info
			}
			return true;
		}
		if(args.length == 1){
			if (args[0].equalsIgnoreCase(permit.COLOR)) {
				if(permit.isPermittet(sender, permit.COLOR, true)){
					toggleColor();
					sender.sendMessage("Rainbow sheeps " + (discoParty.isColorOn() ? "Activated":"Deactivated"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase(permit.DEBUG)) {
				if(permit.isPermittet(sender, permit.DEBUG, true)){
					sender.sendMessage(printDebug()); 					
				}
				return true;
			}
			if (args[0].equalsIgnoreCase(permit.RELOAD)) {
				if(permit.isPermittet(sender, permit.RELOAD, true)){
					settings.reload();
					sender.sendMessage("Reloaded DiscoSheep.properties file");				
				}
				return true;
			}
			if (args[0].equalsIgnoreCase(permit.STOP)) {
				if(permit.isPermittet(sender, permit.STOP, true)){
					if(discoParty.flagPartyEnabled){
						sender.sendMessage("Party Stopped, you little joykiller");						
					}
					else{
						sender.sendMessage("There is no party running, or it just ended. Nothing to stop");						
					}
					stopParty();				
				}
				return true;
			}
			if (args[0].equalsIgnoreCase(permit.HELP)) {
				if(permit.isPermittet(sender, permit.HELP, true)){
					return false; //Use plugin.yml to display info
				}
				return true;
			}	
		}
		if(args.length > 0){
			
			//Check permissions first
			if(!(permit.isPermittet(sender, permit.MANY, false) || permit.isPermittet(sender, permit.ONE, false))){
				
				//Player was not allowed to have even one party. Sender gets the errormessage from permit.isPermittet(sender, permit.ONE, true)
				permit.isPermittet(sender, permit.ONE, true);
				return true;
			}
			
			
			
			Player[] onlinePlayer = getServer().getOnlinePlayers();
			LinkedList<Player> getPartyPlayers = new LinkedList<Player>();
			
			// N = numbers
			// D = default
			// We use default to compare if it was any changes. We don't want to send denied use message if its not changed 
			int timeN = ((IntS)settings.getSetting("defaultPartyTime")).getV();
			int sheepsN = ((IntS)settings.getSetting("sheepNumber")).getV();
			int sheepsD = sheepsN;
			int creepersN = ((IntS)settings.getSetting("creeperNumber")).getV();
			int creepersD = creepersN;
			int ghastsN = ((IntS)settings.getSetting("ghastNumber")).getV();
			int ghastsD = ghastsN;
			int spawnDistance = ((IntS)settings.getSetting("spawnDistance")).getV();
			
			
			
			
			//Check player names
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 1){
					for (Player p : onlinePlayer) {
						if(p.getName().equalsIgnoreCase(temp[0])){
							getPartyPlayers.add(p);
						}
					}
				}
			}
			
			//If no player was added we check for keywords
			if (getPartyPlayers.size() == 0) {
				
				//Check if player wants a party here
				for(String s: args){
					String[] temp = s.split(":");
					if(temp.length == 1){
						if(temp[0].equalsIgnoreCase("here")){
							if(sender instanceof Player){
								getPartyPlayers.add((Player)sender);							
								sender.sendMessage("Party at your place. Good going!");
							}
							else{
								sender.sendMessage("Brother, you are the server. You can't spawn a party \"here\"! \nShame on you admin. For shame.");
							}
						}
					}
				}
				
				//check if player wants a party for everyone
				for(String s: args){
					String[] temp = s.split(":");
					if(temp.length == 1){
						if(temp[0].equalsIgnoreCase("all") || temp[0].equalsIgnoreCase("party")){
							getPartyPlayers.clear();
							for(Player p: onlinePlayer){
								getPartyPlayers.add(p);
							}
						}
					}
				}
			}
			
			//Check if player can have multiple party
			if(getPartyPlayers.size() > 1){
				if(permit.isPermittet(sender, permit.MANY, true)){
					//all good, do nothing
				}
				else{
					//User is not allowed to have multiple parties.
					//An error message is already sent from permit.isPermittet(sender, permit.MANY)
					if(permit.isPermittet(sender, permit.ONE, false)){
						//Send message to hint that player is allowed to have one party.
						sender.sendMessage("But you are allowed to create party for one player!");
					}
					
					return true;
				}
			}
			//Check if player can have one party
			if(getPartyPlayers.size() == 1){
				if(permit.isPermittet(sender, permit.MANY, false) || permit.isPermittet(sender, permit.ONE, true)){
					//all good, do nothing
				}
				else{
					//User is not allowed to have single parties.
					//An error message is already sent from permit.isPermittet(sender, permit.ONE)
					return true;
				}
			}
			if(getPartyPlayers.size() == 0){
				if(permit.isPermittet(sender, permit.MANY, false) || permit.isPermittet(sender, permit.ONE, false)){
					sender.sendMessage("DiscoSheep did not find any online players to give a party to. Bummer...");	
					return true;
				}
				else{
					//User is not allowed to have single parties.
					//An error message is already sent from permit.isPermittet(sender, permit.ONE)
					return true;
				}
			}
			
			//Check time
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					if(temp[0].equalsIgnoreCase("t") || temp[0].equalsIgnoreCase("time") || temp[0].equalsIgnoreCase("sec") || temp[0].equalsIgnoreCase("seconds")){
						try {
							timeN = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							errParseInt(sender, temp[1], timeN);
						}
					}
				}
			}
			//Check sheeps
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					if(temp[0].equalsIgnoreCase("s") || temp[0].equalsIgnoreCase("sheep") || temp[0].equalsIgnoreCase("sheeps")){
						try {
							sheepsN = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							errParseInt(sender, temp[1], sheepsN);
						}
					}
				}
			}
			
			//Check creepers
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					if(temp[0].equalsIgnoreCase("c") || temp[0].equalsIgnoreCase("creeper") || temp[0].equalsIgnoreCase("creepers")){
						try {
							creepersN = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							errParseInt(sender, temp[1], creepersN);
						}
					}
				}
			}
			
			//Check ghasts
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					if(temp[0].equalsIgnoreCase("g") || temp[0].equalsIgnoreCase("ghast") || temp[0].equalsIgnoreCase("ghasts")){
						try {
							ghastsN = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							errParseInt(sender, temp[1], ghastsN);
						}
					}
				}
			}
			
			//Check spawn
			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					if(temp[0].equalsIgnoreCase("d") || temp[0].equalsIgnoreCase("distance") || temp[0].equalsIgnoreCase("spawn")){
						try {
							spawnDistance = Integer.parseInt(temp[1]);
						} catch (NumberFormatException e) {
							errParseInt(sender, temp[1], spawnDistance);
						}
					}
				}
			}
			
			int max;
			//Max time
			max = ((IntS)settings.getSetting("maxPartyTime")).getV();
			if(max < timeN){
				sender.sendMessage("Max time limit exceeded, time set to max: " + max);
				timeN = max;
			}
			
			//Max sheeps and permission to spawn sheeps
			max = ((IntS)settings.getSetting("maxSheepNumber")).getV();
			if(sheepsN > 0 && permit.isPermittet(sender, permit.SHEEP, false)){
				if(max < sheepsN){
					sender.sendMessage("Max sheep limit exceeded, sheeps set to max: " + max);
					sheepsN = max;
				}
			}else{
				if(sheepsN != sheepsD){
					// We use permit to send denied message to user
					permit.isPermittet(sender, permit.SHEEP, true);
				}
				//If the user has not done any changes to sheeps we silently set sheeps to 0
				sheepsN = 0;
			}
			
			//Max creepers and permission to spawn creepers
			max = ((IntS)settings.getSetting("maxCreeperNumber")).getV();
			if(creepersN > 0 && permit.isPermittet(sender, permit.CREEPER, false)){
				if(max < creepersN){
					sender.sendMessage("Max creeper limit exceeded, creepers set to max: " + max);
					creepersN = max;
				}
			}else{
				if(creepersN != creepersD){
					// We use permit to send denied message to user
					permit.isPermittet(sender, permit.CREEPER, true);
				}
				//If the user has not done any changes to creepers we silently set creepers to 0
				creepersN = 0;
			}
			
			//Max ghasts and permission to spawn ghasts
			max = ((IntS)settings.getSetting("maxGhastNumber")).getV();
			if(ghastsN > 0 && permit.isPermittet(sender, permit.GHAST, false)){
				if(max < ghastsN){
					sender.sendMessage("Max ghast limit exceeded, ghasts set to max: " + max);
					ghastsN = max;
				}
			}else{
				if(ghastsN != ghastsD){
					// We use permit to send denied message to user
					permit.isPermittet(sender, permit.GHAST, true);
				}
				//If the user has not done any changes to ghasts we silently set ghasts to 0
				ghastsN = 0;
			}
			
			//Max spawn distance check
			max = ((IntS)settings.getSetting("maxSpawnDistance")).getV();
			if(max < spawnDistance){
				sender.sendMessage("Max spawn distance limit exceeded, spawn distance set to max: " + max);
				spawnDistance = max;
			}		
			
			Player[] partyAt = new Player[getPartyPlayers.size()];
			int i = 0;
			for(Player p: getPartyPlayers){
				partyAt[i] = p;
				i++;
			}
			
			startParty(partyAt, timeN, sheepsN, creepersN, ghastsN, spawnDistance);
			
			sender.sendMessage("Party ON!");
			return true;

		}
		return false;
	}

	private void errParseInt(CommandSender sender, String string, int def) {
		sender.sendMessage("Err: DiscoSheep could not convert " + string + " to a number. Using default " + def);
		
	}
	
	/**
	 * Start swapping colors on all sheeps
	 */
	private void toggleColor(){
		//Try to restart if crashed
		recover();
		
		discoParty.toggleColor();
	}
	
	/**
	 * Spawn objects and start party
	 */
	public void startParty(Player[] players, int partyTime, int sheeps, int creepers, int ghasts, int spawnRange){
		
		timer.stop();
		
		//Try to restart if crashed
		recover();
		
		timer.setInitialDelay(partyTime * 1000);
		timer.start();
		discoParty.enableParty(players, sheeps, creepers, ghasts, spawnRange);
	}
	
	/**
	 * Disables party
	 */
	private void stopParty() {
		timer.stop();
		discoParty.stopParty();
	}
	/**
	 * Method to restart thread if it ended unexpected
	 */
	private void recover(){
		if(!discoParty.isAlive()){
			System.out.println("[DiscoSheep] is trying to recover from unexpected thread ending");
			discoParty.cleanUp();
			discoParty = new DiscoParty(this);
			discoParty.start();
		}
	}
	
	/**
	 * Called when timer wants to stop party
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		stopParty();		
	}

	public String printDebug(){
		String ln = "\n";
		StringBuilder s = new StringBuilder();
		
		s.append("timer tick delay: " + timer.getInitialDelay()		+ ln);
		s.append("permissions on? : " + permit.usePermit			+ ln);
		s.append("Permissions obj : " + permit.permit				+ ln);
		

		for(Setting setting: settings.settings){
			if(setting instanceof BoolS){
				s.append(setting.sN + " : " + ((BoolS) setting).getV() + ln);
			}
			else if(setting instanceof IntS){
				s.append(setting.sN + " : " + ((IntS) setting).getV() + ln);
				
			}
			else if(setting instanceof FloS){
				s.append(setting.sN + " : " + ((FloS) setting).getV() + ln);
				
			}
			else{
				System.out.println("[DiscoSheep] The plugindeveloper has forgot to add support for " + setting.getClass().getName());
			}
			
		}
		
		s.append(discoParty.printDebug());
		
		return s.toString();
	}

}
