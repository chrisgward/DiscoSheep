package Goldsack.DiscoSheep;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * Class to listen to entity damage. 
 * If we own entity and they are not allowed to drop item then damage is disabled.
 * 
 * If they are in lava, they will be on FIRE! Untz untz!
 * @author goldsack
 *
 */
public class DiscoListener implements Listener {
	private final DiscoSheep plugin;
	public DiscoListener(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(!plugin.getSettings().isDropItems() && plugin.getDiscoParty().isOurEntity(event.getEntity())){
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if (!plugin.getSettings().isExplosion() && plugin.getDiscoParty().isOurEntity(event.getEntity())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		if(!plugin.getSettings().isDropItems() && plugin.getDiscoParty().isOurEntity(event.getEntity())){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!plugin.getSettings().isDropItems() && plugin.getDiscoParty().isOurEntity(event.getBlock())){
			event.setCancelled(true);
		}
	}

}
