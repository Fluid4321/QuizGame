package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class quizScore {

    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizScore() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Method to show the score of a player in each category
    public void quizScore(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return;
        }

        if (strings.length != 2) {
            commandSender.sendMessage("Incorrect command usage! Use /quiz score <playerName>");
            return;
        }

        Player player = (Player) commandSender;
        String playerName = strings[1];

        // Get the OfflinePlayer object using the player's name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);

        if(targetPlayer == null){
            QuizGame.getInstance().sendColoredMessage(player,"&cPlayer not found!");
            return;
        }

        // Get the player's UUID
        String playerUUID = targetPlayer.getUniqueId().toString();

        try {
            mySqlInstance.connectToDatabase();

            List<String> categories = mySqlInstance.getAllCategories();

            if (categories.isEmpty()) {
                // If there are no categories
                QuizGame.getInstance().sendColoredMessage(player,"&cThere are no categories to show scores for!");
            } else {
                boolean hasScore = false;
                for (String category : categories) {
                    // Pass the player's UUID to checkPlayerScore instead of their name
                    int score = mySqlInstance.checkPlayerScore(playerUUID, category);

                    if (score != -1) {
                        hasScore = true;
                        QuizGame.getInstance().sendColoredMessage(player, category + " = " + playerName + ": " + score);
                    }
                }

                if (!hasScore) {
                    // If the player doesn't have a score in any category
                    QuizGame.getInstance().sendColoredMessage(player,"&cYou don't have a score in any category!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
