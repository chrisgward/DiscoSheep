package Goldsack.DiscoSheep;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DiscoListenerBlock implements Listener {
	DiscoSheep plugin;
	
	public DiscoListenerBlock(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(!((BoolS)plugin.settings.getSetting("dropItems")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(block));			
		}
		
	}
}
