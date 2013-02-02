package Goldsack.DiscoSheep;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;      */

/**
 * DiscoSheep is a plugin to minecraft servers running minecraft.
 * It changes sheep wool color for fun. 
 * @author goldsack
 *
 */
public class DiscoSheep extends JavaPlugin implements ActionListener{
	@Getter private DiscoListener entityListener = new DiscoListener(this);
	@Getter private DiscoParty discoParty;
	@Getter private DiscoSettings settings = new DiscoSettings(this);
	@Getter private DiscoCommandHandler commandHandler;

	@Getter private Timer timer;

	/**
	 * Called when plugin starts.
	 */
	//@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(entityListener, this);
		
		settings = new DiscoSettings(this);

		discoParty = new DiscoParty(this);
		discoParty.start();

		timer = new Timer(settings.getDefaultPartyTime(), this);
		timer.stop(); //Timer starts when its created, so it has to be stopped

		commandHandler = new DiscoCommandHandler(this);
		getCommand("ds").setExecutor(commandHandler);
	}
	
	/**
	 * Called when plugin ends.
	 * Cancels all threads, and wait for them to end.
	 */
	//@Override
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


	public void reload()
	{
		settings = null;
		reloadConfig();
		settings = new DiscoSettings(this);
	}

	private void errParseInt(CommandSender sender, String string, int def) {
		sender.sendMessage("Err: DiscoSheep could not convert " + string + " to a number. Using default " + def);

	}
	
	/**
	 * Start swapping colors on all sheeps
	 */
	public void toggleColor(){
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
	public void stopParty() {
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
	//@Override
	public void actionPerformed(ActionEvent arg0) {
		stopParty();		
	}
}
