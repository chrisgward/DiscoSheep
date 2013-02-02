package Goldsack.DiscoSheep;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class DiscoCommandHandler implements CommandExecutor
{
	private final DiscoSheep plugin;

	public DiscoCommandHandler(DiscoSheep plugin)
	{
		this.plugin = plugin;
	}
	/**
	 * Called when /discosheep is sent in game or server
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {

		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
			return false;
		}
		else if(args.length == 1){
			if (args[0].equalsIgnoreCase("color") || args[0].equalsIgnoreCase("colour"))
				return commandColor(sender);
			else if (args[0].equalsIgnoreCase("reload"))
				return commandReload(sender);
			if (args[0].equalsIgnoreCase("stop"))
				return commandStop(sender);
		}
		else if(args.length > 0){

			//Check permissions first
			if(!(sender.hasPermission("discosheep.many") || sender.hasPermission("discosheep.one"))){

				//Player was not allowed to have even one party. Sender gets the errormessage from plugin.getPermit().isPermitted(sender, plugin.getPermit().ONE, true)
				//TODO: Send error
				return true;
			}



			Player[] onlinePlayer = plugin.getServer().getOnlinePlayers();
			LinkedList<Player> partyPlayers = new LinkedList<Player>();

			// N = numbers
			// D = default
			// We use default to compare if it was any changes. We don't want to send denied use message if its not changed
			int timeN = plugin.getSettings().getDefaultPartyTime();
			int sheepsN = plugin.getSettings().getSheepNumber();
			int creepersN = plugin.getSettings().getCreeperNumber();
			int ghastsN = plugin.getSettings().getGhastNumber();
			int spawnDistance = plugin.getSettings().getSpawnDistance();




			//Check player names
			for(String s : args){
				if(s.split(":").length == 1){
					if(s.equalsIgnoreCase("here"))
					{
						if(sender instanceof Player) {
							partyPlayers.add((Player)sender);
							sender.sendMessage("Party at your place. Good going!");
						}
						else {
							sender.sendMessage("Brother, you are the server. You can't spawn a party \"here\"! \nShame on you admin. For shame.");
						}
					}
					else if(s.equalsIgnoreCase("all") || s.equalsIgnoreCase("party"))
					{
						partyPlayers.clear();
						for(Player p : plugin.getServer().getOnlinePlayers())
							partyPlayers.add(p);
					}
					List<Player> players = plugin.getServer().matchPlayer(s);
					if(players.size() == 0)
					{
						//TODO: Send player not found message
					}
					partyPlayers.addAll(players);
				}
			}

			//Check if player can have multiple party
			if(partyPlayers.size() > 1){
				if(!sender.hasPermission("discosheep.many")) {
					//User is not allowed to have multiple parties.
					//An error message is already sent from plugin.getPermit().isPermitted(sender, plugin.getPermit().MANY)
					// TODO: You may not start a sheep party :(
					sender.sendMessage("But you are allowed to create party for one player!");
					return true;
				}
			}

			if(partyPlayers.size() == 0){
				sender.sendMessage("DiscoSheep did not find any online players to give a party to. Bummer...");
				return true;
			}

			for(String s: args){
				String[] temp = s.split(":");
				if(temp.length == 2){
					try {
						if(temp[0].equalsIgnoreCase("t") || temp[0].equalsIgnoreCase("time") || temp[0].equalsIgnoreCase("sec") || temp[0].equalsIgnoreCase("seconds")){
							timeN = Integer.parseInt(temp[1]);
						}
						else if(temp[0].equalsIgnoreCase("s") || temp[0].equalsIgnoreCase("sheep") || temp[0].equalsIgnoreCase("sheeps")){
							sheepsN = Integer.parseInt(temp[1]);
						}
						else if(temp[0].equalsIgnoreCase("c") || temp[0].equalsIgnoreCase("creeper") || temp[0].equalsIgnoreCase("creepers")){
							creepersN = Integer.parseInt(temp[1]);
						}
						else if(temp[0].equalsIgnoreCase("g") || temp[0].equalsIgnoreCase("ghast") || temp[0].equalsIgnoreCase("ghasts")){
							ghastsN = Integer.parseInt(temp[1]);
						}
						else if(temp[0].equalsIgnoreCase("d") || temp[0].equalsIgnoreCase("distance") || temp[0].equalsIgnoreCase("spawn")){
							spawnDistance = Integer.parseInt(temp[1]);
						}
					} catch (NumberFormatException e)
					{
						// TODO: Malformed Number Error
					}
				}
			}

			int max;
			//Max time
			max = plugin.getSettings().getMaxPartyTime();
			if(max < timeN){
				sender.sendMessage("Max time limit exceeded, time set to max: " + max);
				timeN = max;
			}

			//Max sheeps and permission to spawn sheeps
			max = plugin.getSettings().getMaxSheepNumber();
			if(sender.hasPermission("discosheep.sheep")){
				if(max < sheepsN){
					sender.sendMessage("Max sheep limit exceeded, sheep set to max: " + max);
					sheepsN = max;
				}
			}
			else{
				if(sheepsN != plugin.getSettings().getSheepNumber()){
					// TODO: No perms :(
				}
				//If the user has not done any changes to sheeps we silently set sheeps to 0
				sheepsN = 0;
			}

			//Max creepers and permission to spawn creepers
			max = plugin.getSettings().getMaxCreeperNumber();
			if(sender.hasPermission("discosheep.creeper")){
				if(max < creepersN){
					sender.sendMessage("Max creeper limit exceeded, creepers set to max: " + max);
					creepersN = max;
				}
			}
			else{
				if(creepersN != plugin.getSettings().getMaxCreeperNumber()){
					// TODO: No perms :(
				}
				//If the user has not done any changes to creepers we silently set creepers to 0
				creepersN = 0;
			}

			//Max ghasts and permission to spawn ghasts
			max = plugin.getSettings().getMaxGhastNumber();
			if(sender.hasPermission("discosheep.ghast")){
				if(max < ghastsN){
					sender.sendMessage("Max ghast limit exceeded, ghasts set to max: " + max);
					ghastsN = max;
				}
			}
			else{
				if(ghastsN != plugin.getSettings().getGhastNumber()){
					// TODO: No perms :(
				}
				//If the user has not done any changes to ghasts we silently set ghasts to 0
				ghastsN = 0;
			}

			//Max spawn distance check
			max = plugin.getSettings().getMaxSpawnDistance();
			if(max < spawnDistance){
				sender.sendMessage("Max spawn distance limit exceeded, spawn distance set to max: " + max);
				spawnDistance = max;
			}

			Player[] partyAt = partyPlayers.toArray(new Player[partyPlayers.size()]);

			plugin.startParty(partyAt, timeN, sheepsN, creepersN, ghastsN, spawnDistance);

			sender.sendMessage("Party ON!");
			return true;

		}
		return false;
	}

	public boolean commandColor(CommandSender sender)
	{
		if(sender.hasPermission("discosheep.color")){
			plugin.toggleColor();
			sender.sendMessage("Rainbow sheep: " + (plugin.getDiscoParty().isColorOn() ? "enabled" : "disabled"));
		}
		return true;
	}

	public boolean commandReload(CommandSender sender)
	{
		if(sender.hasPermission("discosheep.reload")){
			plugin.reload();
			sender.sendMessage("Reloaded DiscoSheep configuration file");
		}
		return true;
	}

	public boolean commandStop(CommandSender sender)
	{
		if(sender.hasPermission("discosheep.stop"))
			if(plugin.getDiscoParty().flagPartyEnabled)
				sender.sendMessage("Party Stopped, you little joykiller");
			else
				sender.sendMessage("There is no party running, or it just ended. Nothing to stop");
		plugin.stopParty();
		return true;
	}
}
