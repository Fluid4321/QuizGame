package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class quizCreateCategory {
    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizCreateCategory() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Method to execute the "createcategory" command
    public void quizCreateCategory(CommandSender commandSender, String[] args) {

        // Check if the sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can execute this command!");
            return;
        }

        Player player = (Player) commandSender;

        // Check if the command has the correct number of arguments
        if (args.length != 2) {
            QuizGame.getInstance().sendColoredMessage(player,"&7Incorrect usage! Correct usage: /quiz createcategory [categoryName]");
            return;
        }

        // Check if the player has the required permission to create a category
        if (!player.hasPermission("QuizGame.createcategory")) {
            QuizGame.getInstance().sendColoredMessage(player,"&7You don't have permission to create a category!");
            return;
        }

        String categoryName = args[1];

        try {
            // Attempt to create the category in the database
            mySqlInstance.createCategory(categoryName);
            QuizGame.getInstance().sendColoredMessage(player,"&7Category '" + categoryName + "' successfully created!");
        } catch (SQLException e) {
            // Handle any errors that occur during category creation
            QuizGame.getInstance().sendColoredMessage(player,"&7An error occurred while creating the category. Please try again.");
            e.printStackTrace();
        }
    }
}
