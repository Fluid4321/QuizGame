package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class quizRemoveCategory {
    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizRemoveCategory() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Method to execute the "removeCategory" command
    public void quizRemoveCategory(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can execute this command!");
            return;
        }

        if (args.length != 2) {
            commandSender.sendMessage("Incorrect usage! Correct usage: /quiz removeCategory [categoryName]");
            return;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("QuizGame.removeCategory")) {
            QuizGame.getInstance().sendColoredMessage(player, "&cYou don't have permission to remove a category!");
            return;
        }

        String categoryName = args[1];

        try {
            mySqlInstance.removeCategory(categoryName);
            QuizGame.getInstance().sendColoredMessage(player, "&7Category '" + categoryName + "' successfully removed!");
        } catch (SQLException e) {
            QuizGame.getInstance().sendColoredMessage(player, "&7Syntax Error: Ensure the category is spelt correctly with correct capitals.");
            e.printStackTrace();
        }
    }
}
