package xyz.minefalls.duels.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.minefalls.duels.Main;
import xyz.minefalls.duels.managers.DuelRequestManager;

/**
 * Duel - The command to /duel someone
 * @author TheGiorno
 */
public class Duel implements CommandExecutor {

    private Main plugin;
    private boolean accepted = false;

    /**
     * Command Constructor method
     * @param main
     *   instance of the Main class
     */
    public Duel(Main main){
        this.plugin = main;
    }

    /**
     * Command run Method
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Messages.NotAPlayerMessage")));
            return true;
        }

        Player player1 = (Player) sender;

        if (plugin.getArenaManager().getArena(player1) != null){
            player1.sendRawMessage(ChatColor.RED + "You're already in a match! Quit this one to send them a request");
            return true;
        }
        accepted = false;

        /* GUI instance */
        Gui gui = Gui.gui()
                .title(Component.text("Duel Invitation from " + player1.getDisplayName()))
                .rows(3)
                .create();

        /* so that people can't pick out the items in the GUI */
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        /* items to add to the gui */
        ItemStack acceptStack = new ItemStack(Material.LIME_CONCRETE);
        ItemStack denyStack = new ItemStack(Material.RED_CONCRETE);
        ItemMeta acceptMeta = acceptStack.getItemMeta();
        ItemMeta denyMeta = denyStack.getItemMeta();
        acceptMeta.setDisplayName("§aClick to accept the duel invitation");
        denyMeta.setDisplayName("§cClick to deny the duel invitation");
        acceptStack.setItemMeta(acceptMeta);
        denyStack.setItemMeta(denyMeta);

        /* when the player doesn't add a name of player */
        if (args.length == 0) return false;

        /* if the player to be duelled is offline */
        if (Bukkit.getPlayerExact(args[0]) == null){
            player1.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is not online!"));
            return true;
        }

        /* when the player to be dueled is online */
        if (Bukkit.getPlayerExact(args[0]) != null){
            Player player2 = Bukkit.getPlayerExact(args[0]);

            if (plugin.getArenaManager().getArena(player2) != null){
                player1.sendRawMessage(ChatColor.RED + "Sorry, " + player2.getDisplayName() + " is already in a match");
                return true;
            }

            DuelRequestManager requestManager = new DuelRequestManager(plugin, player1, player2);

            // adding the gui items
            GuiItem accept = ItemBuilder.from(acceptStack).asGuiItem(event -> {
                requestManager.accept();
                accepted = true;
            });
            GuiItem deny = ItemBuilder.from(denyStack).asGuiItem(event -> requestManager.deny());

            gui.setCloseGuiAction(event -> {
                if (!accepted) {
                    requestManager.deny();
                }
            });

            gui.setItem(11, accept);
            gui.setItem(15, deny);

            gui.open(player2);
            player1.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&aYou sent a duel invitation to " + player2.getDisplayName() + "!"));
        }

        return true;
    }
}
