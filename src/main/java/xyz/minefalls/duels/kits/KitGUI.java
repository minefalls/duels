package xyz.minefalls.duels.kits;

/*
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.minefalls.duels.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;*/

public class KitGUI {
/*
    private Main plugin;

    public KitGUI(Main pl){
        this.plugin = pl;
    }
    public Gui getGui(){
        Gui gui = Gui.gui()
                .title(Component.text("Duels Kits"))
                .rows(3)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        List<String> kitList = plugin.getKitManager().getKitNames();

        try {
            for (String kit : kitList) {

                ItemStack[] kitItemList = Main.itemStackArrayFromBase64(kit);
                Arrays.stream(kitItemList).forEach(itemStack -> {
                    if (itemStack.getType().toString().contains("sword")){

                        GuiItem kitGuiItem = ItemBuilder.from(itemStack).asGuiItem();
                        gui.addItem(kitGuiItem);
                    }
                });

            }
        }
        catch (IOException e){
            plugin.getLogger().warning("WARNING: Duels failed to give the kits due to them being set up wrong");
        }
    }*/
}
