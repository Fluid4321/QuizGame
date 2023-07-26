package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class quizCategories {

    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizCategories() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Method to execute the "categories" command
    public void quizCategories(CommandSender commandSender, String[] strings) {

        // Check if the sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return;
        }

        Player player = (Player) commandSender;

        try {
            // Connect to the database
            mySqlInstance.connectToDatabase();

            // Get a list of all categories from the database
            List<String> categories = mySqlInstance.getAllCategories();

            if (categories.isEmpty()) {
                // If no categories are found
                QuizGame.getInstance().sendColoredMessage(player,"&cNo categories found!");
            } else {
                // Display the available categories to the player
                QuizGame.getInstance().sendColoredMessage(player,"&aAvailable categories:");
                for (String category : categories) {
                    QuizGame.getInstance().sendColoredMessage(player, category);
                }
            }

        } catch (SQLException e) {
            // Handle any errors that occur while retrieving categories from the database
            e.printStackTrace();
        }
    }
}
