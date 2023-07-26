package fluid.quizgame.commands;

import fluid.quizgame.QuizGame;
import fluid.quizgame.commands.Logic.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class quizCommand implements CommandExecutor {

    // Create instances of each sub-command class
    private final quizCreateCategory quizCreateCategory = new quizCreateCategory();
    private final quizRemoveCategory quizRemoveCategory = new quizRemoveCategory();
    private final quizLeaderboards quizLeaderboards = new quizLeaderboards();
    private final quizScore quizScore = new quizScore();
    private final quizStart quizStart = new quizStart();
    private final quizStop quizStop = new quizStop();
    private final quizCategories quizCategories = new quizCategories();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Check if the command sender is a player
        if (!(commandSender instanceof org.bukkit.entity.Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        } else if (command.getName().equalsIgnoreCase("quiz") && commandSender.hasPermission("QuizGame.Quiz")) {
            Player player = (Player) commandSender;
            if (strings.length == 0) {
                // Display the available sub-commands if no arguments provided
                QuizGame.getInstance().sendColoredMessage(player, "&c&lQUIZ Commands");
                QuizGame.getInstance().sendColoredMessage(player, "&7");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz categories");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz createcategory [name]");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz removecategory [name]");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz leaderboards");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz score [player]");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz start");
                QuizGame.getInstance().sendColoredMessage(player, "&7/quiz stop");
                return true;
            }

            // Determine which sub-command was used
            switch (strings[0].toLowerCase()) {
                case "createcategory":
                    quizCreateCategory.quizCreateCategory(commandSender, strings);
                    break;
                case "removecategory":
                    quizRemoveCategory.quizRemoveCategory(commandSender, strings);
                    break;
                case "leaderboards":
                    quizLeaderboards.quizLeaderboards(commandSender, strings);
                    break;
                case "score":
                    quizScore.quizScore(commandSender, strings);
                    break;
                case "start":
                    quizStart.quizStart(commandSender, strings);
                    break;
                case "stop":
                    quizStop.quizStop(commandSender, strings);
                    break;
                case "categories":
                    quizCategories.quizCategories(commandSender, strings);
                    break;
                default:
                    // Notify the player if an unknown sub-command was used
                    QuizGame.getInstance().sendColoredMessage(player, "&7Unknown sub-command!");
            }
        }
        return true;
    }
}
