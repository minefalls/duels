package xyz.minefalls.duels.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.minefalls.duels.Main;

/**
 * LobbySpawnManager - Manages the spawn of where you go after a match
 * @author TheGiorno
 */
public class LobbySpawnManager {

    private Main plugin;

    public LobbySpawnManager(Main plugin){
        this.plugin = plugin;
    }

    /**
     * Saves the location of the lobby to the config.yml file
     * @param location
     *   Location of the lobby
     */
    public void saveLobbyLocation(Location location){
        FileConfiguration config = plugin.getConfig();
        config.set("LobbySpawn", location.getWorld().getName() + "/" + location.getX() + "/" + location.getY() + "/" + location.getZ() + "/" + location.getYaw() + "/" + location.getPitch());

        plugin.saveConfig();
        plugin.reloadConfig();
    }

    /**
     * Getting the Location of the lobby
     * @return Location of the lobby
     */
    public Location getLobbyLocation(){
        FileConfiguration config = plugin.getConfig();
        String[] splicedLocation = config.getString("LobbySpawn").split("/");

        World locWorld = Bukkit.getWorld(splicedLocation[0]);
        double locX = Double.parseDouble(splicedLocation[1]);
        double locY = Double.parseDouble(splicedLocation[2]);
        double locZ = Double.parseDouble(splicedLocation[3]);
        float locYaw = Float.parseFloat(splicedLocation[4]);
        float locPitch = Float.parseFloat(splicedLocation[5]);

        return new Location(locWorld, locX, locY, locZ, locYaw, locPitch);
    }
}
