package xyz.minefalls.duels.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.minefalls.duels.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * CoinUtils - Manages everything related to MongoDB and giving coins
 * @author TheGiorno
 */
public class CoinUtils {

    private Main main;
    private MongoClient mongoClient;
    private DiscordLinkedUtils discordUtils;
    private HttpURLConnection connection;

    public CoinUtils(Main plugin){
        this.main = plugin;
        this.discordUtils = plugin.getDiscordUtils();
        this.mongoClient = plugin.getMongoClient();
    }
    /**
     * Adds coins (the ones in our website) to a user
     * @param player
     *   The player to add coins to
     * @param coins
     *   The amount of coins to add to the player
     */
    public void addCoins(Player player, int coins){

        UUID uuid = player.getUniqueId();
        MongoDatabase mongodb = mongoClient.getDatabase("minefalls");
        MongoCollection<Document> userdata = mongodb.getCollection("userdata");

        Document playerData = userdata.find(
                new Document(
                        "username",
                        getUsername(discordUtils.getDiscordID(uuid))
                )
        ).first();

        userdata.updateOne(playerData, Updates.inc("coins", coins));

        player.sendRawMessage(ChatColor.GREEN + "You have " + playerData.get("coins") + "coins!");
    }

    /**
     * Returns the Discord Username of the user via the Discord ID
     * @param discordID
     *   The Discord ID of the user
     * @return username
     *   The Discord Username of the user
     */
    public String getUsername(String discordID){

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        Gson gson = new Gson();
        JsonObject data;
        String username = "";

        try {
            URL url = new URL("https://minefalls.xyz/getusernamesjs");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() > 299) reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            else reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null){
                responseContent.append(line);
            }

            reader.close();

            data = gson.fromJson(responseContent.toString(), JsonObject.class);

            username = data.get(discordID).toString().replaceAll("\"", "");
        }
        // all errors are redundant in this case so they can just be ignored
        catch (IOException e) { e.printStackTrace(); }

        finally { connection.disconnect(); }

        return username;
    }
}
