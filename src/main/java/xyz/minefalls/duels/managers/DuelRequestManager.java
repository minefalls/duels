package xyz.minefalls.duels.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.minefalls.duels.Main;
import xyz.minefalls.duels.arenas.Arena;

import java.util.List;

/**
 * DuelRequestManager - A Manager class to manage /duel PlayerName requests
 * @author TheGiorno
 */
public class DuelRequestManager {

    private Main main;
    private Player requester;
    private Player requestGetter;

    /**
     * Constructor class
     * @param instance
     *   instance of the Main class
     * @param requestSender
     *   the one who did the /duel PlayerName
     * @param requestGetter
     *   the PlayerName in `/duel PlayerName`
     */
    public DuelRequestManager(Main instance, Player requestSender, Player requestGetter){
        this.main = instance;
        this.requester = requestSender;
        this.requestGetter = requestGetter;
    }

    /**
     * when the requestGetter accepts the duel request
     */
    public void accept(){
        List<Arena> arenaList = main.getArenaManager().getArenaList();
        
        for (Arena arena : arenaList){
            
            if (arena.getPlayer1() == null && arena.getPlayer2() == null){
                arena.addPlayer(requester);
                arena.addPlayer(requestGetter);
                if (arena.canStart()){ // ik this is kinda useless but i just wanna be sure
                    arena.start();
                    
                    this.requester = null;
                    this.requestGetter = null;
                    
                    break;
                }
            }
        }
    }

    /**
     * when the requestGetter denies the duel request
     */
    public void deny(){
        requester.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, " + requestGetter.getDisplayName() + " decided to deny your duel request."));

        this.requester = null;
        this.requestGetter = null;
    }
}
