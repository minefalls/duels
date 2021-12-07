package xyz.minefalls.duels.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.minefalls.duels.Main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DiscordLinkedUtils - Manages the Discord Username and Minecraft Username saved by DiscordSRV
 * @author TheGiorno
 */
public class DiscordLinkedUtils {

    private Gson gson;

    /**
     * Constructs DiscordLinkedUtils
     */
    public DiscordLinkedUtils(){
        this.gson = new Gson();
    }

    /**
     * Method to return the Discord ID of the user via their Minecraft Username
     * @param mcUUID
     *   Minecraft username
     * @return
     *   Discord ID of user
     */
    public String getDiscordID(UUID mcUUID){

        String discordID = "";

        try {
            /* linkedaccounts.json File from DiscordSRV */
            JsonObject data = gson.fromJson(JsonParser.parseReader(new FileReader("plugins/DiscordSRV/linkedaccounts.json")).toString(), JsonObject.class);

            /* To get the keys of the JSON file */
            List<String> keys = data.entrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (String keysInFile : keys){

                String uuidModified = data.get(keysInFile).toString();
                uuidModified = uuidModified.replaceAll("\"", ""); // because the UUIDs come as "g5g25-54g-25g5-g3-45", not g5g25-54g-25g5-g3-45

                if (uuidModified.equals(mcUUID.toString())){
                    discordID = keysInFile;
                    break;
                }
            }

        }
        /* basically a redundant error since we know that the file's there */
        catch (FileNotFoundException e) { e.printStackTrace(); }

        return discordID;
    }
}
