package xyz.minefalls.duels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import xyz.minefalls.duels.arenas.ArenaManager;
import xyz.minefalls.duels.arenas.LobbySpawnManager;
import xyz.minefalls.duels.commands.Duel;
import xyz.minefalls.duels.commands.DuelCmd;
import xyz.minefalls.duels.commands.DuelTabCompleter;
import xyz.minefalls.duels.events.ArenaEvents;
import xyz.minefalls.duels.kits.KitManager;
import xyz.minefalls.duels.utils.CoinUtils;
import xyz.minefalls.duels.utils.DiscordLinkedUtils;
import xyz.minefalls.duels.utils.LeaderboardUtils;
import xyz.minefalls.duels.utils.StatUtils;

/**
 * Main duels class
 * @author TheGiorno
 */
public class Main extends JavaPlugin {

	/*
	 * Known bugs:
	 *  - arenas get removed from the arena list after every reload/restart
	 *  - players don't get teleported to the lobby location
	 */
	
	@Getter private	FileConfiguration statsFile = new YamlConfiguration();
	private File statsF;
	
	@Getter private ArenaManager arenaManager;
	@Getter private KitManager kitManager;
	@Getter private StatUtils statUtils;
	@Getter private LeaderboardUtils lbUtils;
	@Getter private DiscordLinkedUtils discordUtils;
	@Getter private CoinUtils coinUtils;
	@Getter private MongoClient mongoClient;
	@Getter private LobbySpawnManager lobbyManager;

	
	/**
	 * plugin enable method
	 */
	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		createFiles();
		arenaManager = new ArenaManager(this);
		kitManager = new KitManager(this);
		statUtils = new StatUtils(this);
		lbUtils = new LeaderboardUtils(this);
		lobbyManager = new LobbySpawnManager(this);
		mongoClient = MongoClients.create("mongodb+srv://giorno420:giornoisgod@cluster0.c5xhj.mongodb.net/test?ssl=true&ssl_cert_reqs=CERT_NONE");
		discordUtils = new DiscordLinkedUtils(this);
		coinUtils = new CoinUtils(this);

		getCommand("dueladmin").setExecutor(new DuelCmd(this));
		getCommand("dueladmin").setTabCompleter(new DuelTabCompleter());
		getCommand("duel").setExecutor(new Duel(this));
		getServer().getPluginManager().registerEvents(new ArenaEvents(this), this);
	}

	/**
	 * plugin disable method
	 */
	@Override
	public void onDisable(){

	}
	
	/**
	 * save the stats file
	 */
	public void saveStatsFile() {
		try {
			statsFile.save(statsF);
		}catch(IOException e) {
			e.printStackTrace();
			getLogger().warning("Failed to save stats.yml");
		}
	}
	
	/**
	 * create stats.yml
	 */
	private void createFiles() {
		statsF = new File(getDataFolder(), "stats.yml");
		saveRes(statsF, "stats.yml");
		statsFile = new YamlConfiguration();
		try {
			statsFile.load(statsF);
		}
		catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * save resources but with extra steps
	 * @param file
	 *   File to save
	 * @param name
	 *   Name to save it as
	 */
	private void saveRes(File file, String name) {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			saveResource(name, false);
		}
	}


	/**
	 * converts the player's inventory into Base64 strings. First string is the content and second is the armor
     * 
     * @param playerInventory to turn into an array of strings
     * @return Array of strings: [ main content, armor content ]
     * @throws IllegalStateException
     */
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
    	Inventory inv = Bukkit.createInventory(null, 36);
    	ItemStack[] items = playerInventory.getContents();
    	ItemStack[] onlyMainItems = new ItemStack[36];
    	for (int i = 0; i < onlyMainItems.length; i++) {
    		onlyMainItems[i] = items[i];
    	}
    	inv.setContents(onlyMainItems);
    	String content = toBase64(inv);
    	String armor = itemStackArrayToBase64(playerInventory.getArmorContents());
    	
    	return new String[] { content, armor };
    }
    
    /**
     * 
     * A method to serialize an {@link ItemStack} array to Base64 String.
     * 
     * <p />
     * 
     * Based off of {@link #toBase64(Inventory)}.
     * 
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(items.length);
            
            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
    	catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    /**
     * A method to serialize an inventory to Base64 string.
     * 
     * <p />
     * 
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     * 
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     * 
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());
            
            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    /**
     * 
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     * 
     * <p />
     * 
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     * 
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     * 
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());
    
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
    
    /**
     * Gets an array of ItemStacks from Base64 string.
     * 
     * <p />
     * 
     * Base off of {@link #fromBase64(String)}.
     * 
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
    
            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
