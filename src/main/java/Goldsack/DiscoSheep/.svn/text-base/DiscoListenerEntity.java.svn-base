package Goldsack.DiscoSheep;

import org.bukkit.entity.Entity;

import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimedEvent;
/**
 * Class to listen to entity damage. 
 * If we own entity and they are not allowed to drop item then damage is disabled.
 * 
 * If they are in lava, they will be on FIRE! Untz untz!
 * @author goldsack
 *
 */
public class DiscoListenerEntity extends EntityListener{
	DiscoSheep plugin;
	public DiscoListenerEntity(DiscoSheep discoSheep) {
		plugin = discoSheep;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		super.onEntityDamage(event);
		Entity entity = event.getEntity();
		if(!((BoolS)plugin.settings.getSetting("dropItems")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(entity));			
		}
		
	}
	
	@Override
	public void onExplosionPrimed(ExplosionPrimedEvent event) {
		super.onExplosionPrimed(event);
		Entity entity = event.getEntity();
		if(!((BoolS)plugin.settings.getSetting("explosion")).getV()){
			event.setCancelled(plugin.discoParty.isOurEntity(entity));			
		}
	}

}
