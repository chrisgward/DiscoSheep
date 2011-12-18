package Goldsack.DiscoSheep;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.*;

import java.util.LinkedList;

/**
 * Contains the entitys for each party that belongs to a player.
 * @author goldsack
 *
 */
public class DiscoTeam {
	DiscoParty discoParty;
	LinkedList<Sheep> sheepList = new LinkedList<Sheep>();
	LinkedList<Creeper> creeperList = new LinkedList<Creeper>();
	LinkedList<Ghast> ghastList = new LinkedList<Ghast>();
	
	Player player;
	Block soundBlock;
	Block stoneBlock;
	Block torches[];
	boolean light;
	
	public DiscoTeam(Player p, DiscoParty discoParty) {
		this.discoParty = discoParty;
		player = p;
		light = false;
		torches = new Block[4];
	}
	public void buildMusicArea(Block loc){
		if(loc == null){
			return;
		}
		soundBlock = loc.getRelative(1, 1, 1);
		soundBlock.setType(Material.NOTE_BLOCK);
		if(soundBlock.getState() instanceof NoteBlock){
			((NoteBlock)soundBlock.getState()).setRawNote((byte) 0x11);
		}
		
		stoneBlock = loc.getRelative(1, 0, 1);
		stoneBlock.setType(Material.STONE);	
		
		torches[0] = loc.getRelative(1, 0, 0);
		torches[1] = loc.getRelative(0, 0, 1);
		torches[2] = loc.getRelative(1, 0, 2);
		torches[3] = loc.getRelative(2, 0, 1);
		
		//turn torches on
		toggleTorches();
		light = !light; //To avid flicker 

		//add to hash
		discoParty.blockHash.put(soundBlock.getLocation(), soundBlock);
		discoParty.blockHash.put(stoneBlock.getLocation(), stoneBlock);
		
		
		for (Block torch: torches) {
			discoParty.blockHash.put(torch.getLocation(), torch);
		}
		
	}
	/**
	 * Return player connected to this team
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	/**
	 * Removes all sheeps, boxes and torches in this team.
	 */
	public void cleanUp(){
		cleanUpEntitys();
		cleanUpBoxes();
	}
	/**
	 * Turn torches on or off
	 */
	public void toggleTorches(){
		light = !light;
		for(Block torch: torches){
			if(torch != null && (torch.getType() == Material.AIR || torch.getType() == Material.TORCH)){
				if(light){
					if(stoneBlock.getType() == Material.STONE){
						torch.setType(Material.TORCH);
					}
				}
				else{
					torch.setType(Material.AIR);	
				}
			}
		}
	}
	private void cleanUpEntitys(){
		for(Sheep sheep: sheepList){
			if(sheep != null){
				discoParty.creaturesHash.remove(sheep.getEntityId());
				sheep.remove();
			}
		}
		sheepList.clear();
		
		for(Creeper creeper: creeperList){
			if(creeper != null){
				discoParty.creaturesHash.remove(creeper.getEntityId());
				creeper.remove();
			}
		}
		creeperList.clear();
		
		for(Ghast ghast: ghastList){
			if(ghast != null){
				discoParty.creaturesHash.remove(ghast.getEntityId());
				ghast.remove();
			}
		}
		ghastList.clear();
	}
	private void cleanUpBoxes(){
		for (Block torch : torches) {
			if(torch != null){
				discoParty.blockHash.remove(torch.getLocation());
				torch.setType(Material.AIR);
			}
		}
		if(soundBlock != null){
			discoParty.blockHash.remove(soundBlock.getLocation());
			soundBlock.setType(Material.AIR);
		}
		if(stoneBlock != null){
			discoParty.blockHash.remove(stoneBlock.getLocation());
			stoneBlock.setType(Material.AIR);
		}
	}
	/**
	 * Add sheep to the sheeplist for this team
	 * @param sheep
	 */
	public void addSheep(Sheep sheep){
		if(sheep != null){
			sheepList.add(sheep);
			discoParty.creaturesHash.put(sheep.getEntityId(), (Entity) sheep);		
		}
		else{
			System.out.println("[DiscoSheep] addSheep in DiscoTeam recived a creeper that was null. Sheep not added");
		}
	}
	/**
	 * Add creeper to the creeperlist for this team
	 * @param sheep
	 */
	public void addCreeper(Creeper creeper) {
		if(creeper != null){
			creeperList.add(creeper);
			discoParty.creaturesHash.put(creeper.getEntityId(), (Entity) creeper);
		}
		else{
			System.out.println("[DiscoSheep] addCreeper in DiscoTeam recived a creeper that was null. Creeper not added");
		}
	}
	/**
	 * Add ghast to the ghastlist for this team
	 * @param sheep
	 */
	public void addGhast(Ghast ghast) {
		if(ghast != null){
			ghastList.add(ghast);
			discoParty.creaturesHash.put(ghast.getEntityId(), (Entity) ghast);	
		}
		else{
			System.out.println("[DiscoSheep] addGhast in DiscoTeam recived a ghast that was null. Ghast not added");
		}

	}

	
}
