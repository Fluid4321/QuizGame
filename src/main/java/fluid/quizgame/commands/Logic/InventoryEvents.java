package fluid.quizgame.commands.Logic;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;

public class InventoryEvents implements Listener {

    private quizStart quizStartInstance;
    // Constructor to initialize the InventoryEvents with the quizStartInstance
    public InventoryEvents(quizStart quizStartInstance) {
        this.quizStartInstance = quizStartInstance;
    }

    // Event handler for when a player clicks on an inventory item
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        // Check if the inventory is the category selection GUI
        if (inventory.getHolder() == null && event.getView().getTitle().equals("Select a Category")) {

            event.setCancelled(true); // Cancel the event so the player can't take the item

            if (event.getCurrentItem() == null) return;

            // Get the selected category name without color codes
            String categoryName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            // Start the quiz for the player
            try {
                quizStartInstance.startQuizForPlayer(player, categoryName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}