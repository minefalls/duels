package xyz.minefalls.duels.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import xyz.minefalls.duels.Main;

/**
 * PlayerRestorationInfo - Used to restore a player to their previous status after a duel
 * @author Austin Dart (Dartanman)
 */
public class PlayerRestorationInfo {
	
	public static List<PlayerRestorationInfo> pris = new ArrayList();
	
	@Getter private UUID uuid;
	@Getter private ItemStack[] inventoryContents;
	@Getter private ItemStack[] armorContents;
	@Getter private PotionEffect[] potionEffects;
	@Getter private int xpLevel;
	@Getter private GameMode gameMode;
	@Getter private double maxHealth;
	@Getter private double currentHealth;
	
	/**
	 * Constructs a new PlayerRestorationInfo object for the given player
	 * @param player
	 */
	public PlayerRestorationInfo(Player player) {
		if (player == null) {
			return;
		}
		
		this.uuid = player.getUniqueId();
		
		for(PlayerRestorationInfo pri : pris) {
			if(pri.getPlayer().getName().equals(player.getName())) {
				return;
			}
		}

		inventoryContents = player.getInventory().getContents();
		armorContents = player.getInventory().getArmorContents();
		xpLevel = player.getLevel();
		gameMode = player.getGameMode();
		maxHealth = player.getMaxHealth();
		currentHealth = player.getHealth();
		potionEffects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
		pris.add(this);
	}
	
	/**
	 * Returns the player this PRI was made for
	 * @return
	 *   The player
	 */
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	/**
	 * Applies the previous statuses to the player
	 */
	public void apply() {
		getPlayer().getInventory().setContents(inventoryContents);
		getPlayer().getInventory().setArmorContents(armorContents);
		getPlayer().setLevel(xpLevel);
		getPlayer().setGameMode(gameMode);
		getPlayer().setMaxHealth(maxHealth);
		getPlayer().setHealth(currentHealth);
		getPlayer().addPotionEffects(Arrays.asList(potionEffects));
	}

}
