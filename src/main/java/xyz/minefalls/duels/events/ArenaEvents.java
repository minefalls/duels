package xyz.minefalls.duels.events;

import java.net.UnknownHostException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import xyz.minefalls.duels.Main;
import xyz.minefalls.duels.arenas.Arena;
import xyz.minefalls.duels.utils.PlayerRestorationInfo;
/**
 * ArenaEvents - Listens to multiple events relating to Duels in Arenas
 * @author TheGiorno
 */
public class ArenaEvents implements Listener{
	
	private Main plugin;
	
	public ArenaEvents(Main pl) {
		plugin = pl;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) throws UnknownHostException {
		Player player = event.getEntity();
		// Currently unused, but will be used if/when I support Bungeecord.
		boolean bungee = false;
		if (bungee) {
			// blank for now
		}
		else {
			// Bukkit/Spigot
			if (plugin.getArenaManager().getArena(player) != null) {
				Arena arena = plugin.getArenaManager().getArena(player);
				if (!arena.getActive()) {
					// They joined the arena but the duel has not started yet
					return;
				}
				event.getDrops().clear();
				if (arena.getPlayer1().getName().equals(player.getName())) {
					arena.end(arena.getPlayer2(), arena.getPlayer1());
				}
				else {
					arena.end(arena.getPlayer1(), arena.getPlayer2());
				}
			}
		}
	}
	
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		String uuidStr = uuid.toString();
		// Currently unused, but will be used if/when I support Bungeecord.
		boolean bungee = false;
		if (bungee) {
			// blank for now
		}
		else {
			// Bukkit/Spigot
			if (plugin.getArenaManager().getArena(player) != null) {
				Arena arena = plugin.getArenaManager().getArena(player);
				if (!arena.getActive()) {
					// They joined the arena but the duel has not started yet
					return;
				}
				if (arena.getPlayer1().getName().equals(player.getName())) {
					arena.end(arena.getPlayer2(), arena.getPlayer1());
				}
				else {
					arena.end(arena.getPlayer1(), arena.getPlayer2());
				}
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerRestorationInfo pri = new PlayerRestorationInfo(null);
		for(PlayerRestorationInfo priL : PlayerRestorationInfo.pris) {
			if (priL.getPlayer().getName().equals(player.getName())) {
				pri = priL;
			}
		}
		final PlayerRestorationInfo priF = pri;
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
        		priF.apply();
        		if (priF.getPlayer().getBedSpawnLocation() != null && priF.getPlayer().equals(player)) event.setRespawnLocation(priF.getPlayer().getBedSpawnLocation());
        		else if (priF.getPlayer().getBedSpawnLocation() != null && priF.getPlayer().equals(player)) event.setRespawnLocation(plugin.getGameSpawn());
        		PlayerRestorationInfo.pris.remove(priF);
            }
        }, 10L);
	}


	@EventHandler
	public void commandSend (PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		if (plugin.getArenaManager().getArena(player) != null){
			if (plugin.getConfig().getStringList("DisabledInGameCommands").contains(event.getMessage())){
				event.setCancelled(true);
			}
		}
	}

}
