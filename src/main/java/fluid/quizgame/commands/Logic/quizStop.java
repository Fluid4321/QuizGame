package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class quizStop {

    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizStop() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    quizStart quizStartInstance = new quizStart();

    // Method to stop a player's participation in the quiz
    public void quizStop(CommandSender commandSender, String[] strings) {
        // Check if the command sender is a player
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return;
        }

        Player player = (Player) commandSender;

        // Check if the player is currently participating in a quiz
        if (quizStartInstance.isPlayerPlaying(player)) {
            try {
                // Save the player's score to the database and stop their participation in the quiz
                mySqlInstance.saveScore(player.getPlayer().getUniqueId().toString(), quizStartInstance.getPlayerCategory(player), quizStartInstance.getPlayerScore(player));
                quizStartInstance.stopPlayer(player);
                commandSender.sendMessage("Quiz stopped! Your score has been saved.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            commandSender.sendMessage("You are not currently playing any quiz!");
        }
    }
}
