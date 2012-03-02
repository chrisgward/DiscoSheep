package Goldsack.DiscoSheep;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
/**
 * Class to listen to entity damage. 
 * If we own entity and they are not allowed to drop item then damage is disabled.
 * 
 * If they are in lava, they will be on FIRE! Untz untz!
 * @author goldsack
 *
 */
public class DiscoListenerEntity implements Listener {
	DiscoSheep plugin;
	public DiscoListenerEntity(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(!((BoolS)plugin.settings.getSetting("dropItems")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(entity));			
		}
		
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if(!((BoolS)plugin.settings.getSetting("explosion")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(entity));			
		}
	}

}
