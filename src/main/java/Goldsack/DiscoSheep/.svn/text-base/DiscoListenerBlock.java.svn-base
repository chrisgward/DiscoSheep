package Goldsack.DiscoSheep;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class DiscoListenerBlock extends BlockListener{
	DiscoSheep plugin;
	
	public DiscoListenerBlock(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		super.onBlockBreak(event);
		Block block = event.getBlock();
		if(!((BoolS)plugin.settings.getSetting("dropItems")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(block));			
		}
		
	}
}
