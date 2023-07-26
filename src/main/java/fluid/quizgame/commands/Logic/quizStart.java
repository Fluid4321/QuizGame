package fluid.quizgame.commands.Logic;

import fluid.quizgame.QuizGame;
import fluid.quizgame.database.mySql;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class quizStart {

    mySql mySqlInstance;

    // Constructor to initialize the database connection
    public quizStart() {
        String host = QuizGame.getInstance().getConfig().getString("Database.host");
        String port = QuizGame.getInstance().getConfig().getString("Database.port");
        String database = QuizGame.getInstance().getConfig().getString("Database.databaseName");
        String username = QuizGame.getInstance().getConfig().getString("Database.databaseUser");
        String password = QuizGame.getInstance().getConfig().getString("Database.databasePass");
        mySqlInstance = new mySql(host, port, database, username, password);
        mySqlInstance.connectToDatabase();
    }

    // Maps to store player-related data during the quiz
    public Map<Player, List<Question>> playerQuiz = new HashMap<>(); // to store the questions of the quiz for each player
    public Map<Player, String> playerCategory = new HashMap<>(); // to store the category the player is playing
    public Map<Player, Integer> playerScore = new HashMap<>(); // to store the player's score during the quiz

    // Method to start the quiz for the player
    public void quizStart(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return;
        }

        Player player = (Player) commandSender;

        // Check if the player is already in a game
        if (isPlayerPlaying(player)) {
            QuizGame.getInstance().sendColoredMessage(player, "&7You are already in a game. Finish it before starting a new one.");
            return;
        }

        try {
            // Connect to the database and retrieve available categories
            mySqlInstance.connectToDatabase();
            List<String> categories = mySqlInstance.getAllCategories();

            // Create an inventory GUI to let the player select a category
            Inventory categorySelectGUI = Bukkit.createInventory(null, 9, "Select a Category");

            // Populate the inventory with category items
            for (String category : categories) {
                ItemStack categoryItem = new ItemStack(Material.PAPER);
                ItemMeta categoryItemMeta = categoryItem.getItemMeta();

                categoryItemMeta.setDisplayName(category);
                categoryItem.setItemMeta(categoryItemMeta);

                categorySelectGUI.addItem(categoryItem);
            }

            player.openInventory(categorySelectGUI);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Call this function when player selects a category
    public void startQuizForPlayer(Player player, String category) throws SQLException {
        // Connect to the database and set up the quiz for the player
        mySqlInstance.connectToDatabase();

        // Initialize the player's score
        playerScore.put(player, 0);

        // Store the selected category for the player
        playerCategory.put(player, category);

        // Get the questions for the selected category and start the quiz
        List<Question> questions = mySqlInstance.getQuestionsForCategory(category);

        // Limit the number of questions to 10
        if (questions.size() > 10) {
            questions = questions.subList(0, 10);
        }

        playerQuiz.put(player, questions);
        askNextQuestion(player);
    }

    // Call this function when player answers a question
    public void processPlayerAnswer(Player player, String answer) {
        if (isPlayerPlaying(player)) {
            String category = getPlayerCategory(player);
            int score = getPlayerScore(player);
            List<Question> questions = playerQuiz.get(player);

            if (!questions.isEmpty()) {
                Question currentQuestion = questions.get(0);
                String correctAnswer = currentQuestion.getAnswer();

                if (answer.equalsIgnoreCase(correctAnswer)) {
                    QuizGame.getInstance().sendColoredMessage(player, "&aCorrect! Your current score: " + score);
                    playerScore.put(player, score + 1); // Increment score for correct answer
                } else {
                    QuizGame.getInstance().sendColoredMessage(player, "&cIncorrect! The correct answer was: " + correctAnswer);
                    playerScore.put(player, Math.max(score - 1, 0)); // Decrement score for incorrect answer, but don't go below 0
                }

                questions.remove(0); // Remove the processed question
                playerQuiz.put(player, questions); // Update the player's question list

                if (!questions.isEmpty()) {
                    askNextQuestion(player); // Ask the next question
                } else {
                    // Quiz finished, save score and clean up
                    try {
                        mySqlInstance.saveScore(player.getPlayer().getUniqueId().toString(), category, score);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    QuizGame.getInstance().sendColoredMessage(player, "&7Quiz Finished! Your Final Score: &a" + playerScore.get(player));
                    stopPlayer(player);
                }
            }
        }
    }

    // Call this function to ask the next question to the player
    private void askNextQuestion(Player player) {
        List<Question> questions = playerQuiz.get(player);
        if (!questions.isEmpty()) {
            QuizGame.getInstance().sendColoredMessage(player, "&7Your next question is: &a" + questions.get(0).getQuestion()); // send next question
        } else {
            // Quiz finished, save score and clean up
            try {
                mySqlInstance.saveScore(player.getPlayer().getUniqueId().toString(), playerCategory.get(player), playerScore.get(player));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            QuizGame.getInstance().sendColoredMessage(player, "&7Quiz Finished! Your Final Score: &a" + playerScore.get(player));
            playerQuiz.remove(player);
            playerScore.remove(player);
            playerCategory.remove(player);
        }
    }

    // Check if a player is currently playing the quiz
    public boolean isPlayerPlaying(Player player) {
        return playerQuiz.containsKey(player);
    }

    // Get the category of the quiz the player is playing
    public String getPlayerCategory(Player player) {
        return playerCategory.get(player);
    }

    // Get the current score of the player
    public Integer getPlayerScore(Player player) {
        return playerScore.get(player);
    }

    // Stop the quiz for the player and remove their data from the maps
    public void stopPlayer(Player player) {
        playerQuiz.remove(player);
        playerScore.remove(player);
        playerCategory.remove(player);
    }
}
