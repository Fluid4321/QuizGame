package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class quizLeaderboards {
    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizLeaderboards() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Method to execute the "leaderboards" command
    public void quizLeaderboards(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return;
        }

        if (strings.length != 1) {
            commandSender.sendMessage("Incorrect command usage! Use /quiz leaderboards");
            return;
        }

        Player player = (Player) commandSender;

        try {
            mySqlInstance.connectToDatabase();

            List<String> categories = mySqlInstance.getAllCategories();

            for (String category : categories) {
                // Retrieve the top scores for the category, limited to 10 entries
                List<String[]> topScores = mySqlInstance.getTopScores(category, 10);

                if (!topScores.isEmpty()) {
                    StringBuilder leaderboard = new StringBuilder();
                    leaderboard.append("&7").append(category).append(": (");

                    for (int i = 0; i < topScores.size(); i++) {
                        String[] score = topScores.get(i);
                        UUID uuid = UUID.fromString(score[0]);
                        String playerName = Bukkit.getOfflinePlayer(uuid).getName();

                        if (i == 0) {
                            leaderboard.append(playerName).append(" #1");
                        } else {
                            leaderboard.append(", ").append(playerName).append(" #").append(i + 1);
                        }
                    }

                    leaderboard.append(")");
                    QuizGame.getInstance().sendColoredMessage(player, leaderboard.toString());
                } else {
                    // If there are no scores for this category
                    QuizGame.getInstance().sendColoredMessage(player, "&7Category: &c" + category + " &7- No scores found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
