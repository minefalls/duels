package xyz.minefalls.duels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.minefalls.duels.Main;
import xyz.minefalls.duels.arenas.Arena;

import java.util.UUID;

/**
 * StartTimerEvents - Listens to events that go on in the 10 seconds of the game start timer
 * @author TheGiorno
 */
public class StartTimerEvents implements Listener {

    private Main plugin;

    public StartTimerEvents(Main instance){
        this.plugin = instance;
    }

    // stops players from dropping items when the game countdown begins
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidStr = uuid.toString();

        if (plugin.getArenaManager().getArena(player) != null){
            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena.getStarting()){
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getArenaManager().getArena(player) != null){
            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena.getStarting()){
                if (arena.getPlayer1().getName().equals(player.getName())){
                    arena.end(arena.getPlayer2(), arena.getPlayer1());
                }
                else {
                    arena.end(arena.getPlayer1(), arena.getPlayer2());
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            UUID uuid = player.getUniqueId();

            if (plugin.getArenaManager().getArena(player) != null) {
                Arena arena = plugin.getArenaManager().getArena(player);
                if (arena.getStarting()) {
                    event.setCancelled(true);
                }
            }
        }
        else {
        }
    }
}
