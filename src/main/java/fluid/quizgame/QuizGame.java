package fluid.quizgame;

import fluid.quizgame.commands.Logic.InventoryEvents;
import fluid.quizgame.commands.Logic.quizStart;
import fluid.quizgame.database.mySql;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuizGame extends JavaPlugin implements Listener {
    private static QuizGame instance;
    private mySql mySqlInstance;
    private quizStart quizStart;

    @Override
    public void onEnable() {
        instance = this;

        // Load and save the default configuration if not already present
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Access the configuration to get database connection parameters
        String host = getConfig().getString("Database.host");
        String port = getConfig().getString("Database.port");
        String database = getConfig().getString("Database.databaseName");
        String username = getConfig().getString("Database.databaseUser");
        String password = getConfig().getString("Database.databasePass");

        // Create an instance of mySql using the database connection parameters
        mySqlInstance = new mySql(host, port, database, username, password);

        // Try to connect to the database using the provided parameters
        mySqlInstance.connectToDatabase();

        // Create an instance of quizStart after the database connection is established
        quizStart = new quizStart();

        // Register commands and events for the plugin
        getCommand("quiz").setExecutor(new fluid.quizgame.commands.quizCommand());
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(quizStart), this);
        Bukkit.getPluginManager().registerEvents(this, this);

        // Create the tables if they don't already exist
        mySqlInstance.createTablesIfNotExist();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (quizStart.playerQuiz.containsKey(player)) {
            // If the player is currently participating in the quiz
            event.setCancelled(true); // Prevent other players from seeing the answer
            quizStart.processPlayerAnswer(player, event.getMessage()); // Process the player's answer
        }
    }

    public static QuizGame getInstance() {
        return instance;
    }

    // Send a colored message to a player
    public void sendColoredMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (mySqlInstance != null) {
            mySqlInstance.disconnectFromDatabase();
        }
    }
}
