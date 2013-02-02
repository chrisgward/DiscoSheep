package Goldsack.DiscoSheep;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DiscoListenerBlock implements Listener {
	private final DiscoSheep plugin;
	
	public DiscoListenerBlock(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!plugin.getSettings().isDropItems() && plugin.getDiscoParty().isOurEntity(event.getBlock())){
			event.setCancelled(true);
		}
	}
}
