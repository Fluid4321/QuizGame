package fluid.quizgame.database;

import fluid.quizgame.commands.Logic.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mySql {
    Connection connection;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    // Constructor to initialize the database connection parameters
    public mySql(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    // Establish a connection to the database
    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Disconnect from the database
    public void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new category in the database
    public void createCategory(String categoryName) throws SQLException {
        String query = "INSERT INTO tbl_categories (CategoryName) VALUES (?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, categoryName);
        preparedStatement.execute();
    }

    // Remove a category from the database
    public void removeCategory(String categoryName) throws SQLException {
        String query = "DELETE FROM tbl_Categories WHERE CategoryName = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, categoryName);
        preparedStatement.execute();
    }

    public void createTablesIfNotExist() {
        try {
            connectToDatabase();

            // Create tbl_categories table
            String createCategoriesTableQuery = "CREATE TABLE IF NOT EXISTS `tbl_categories` (" +
                    "`CategoryID` int(11) NOT NULL AUTO_INCREMENT," +
                    "`CategoryName` varchar(255) NOT NULL," +
                    "PRIMARY KEY (`CategoryID`)," +
                    "UNIQUE KEY `CategoryName` (`CategoryName`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
            executeUpdateQuery(createCategoriesTableQuery);

            // Insert 'Trivia' category
            String insertTriviaCategoryQuery = "INSERT INTO `tbl_categories` (`CategoryName`) VALUES ('Trivia')";
            executeUpdateQuery(insertTriviaCategoryQuery);

            // Create tbl_users table
            String createUsersTableQuery = "CREATE TABLE IF NOT EXISTS `tbl_users` (" +
                    "`UserID` int(11) NOT NULL AUTO_INCREMENT," +
                    "`UUID` varchar(36) NOT NULL," +
                    "PRIMARY KEY (`UserID`)," +
                    "UNIQUE KEY `UserName` (`UUID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
            executeUpdateQuery(createUsersTableQuery);

            // Create tbl_leaderboards table
            String createLeaderboardsTableQuery = "CREATE TABLE IF NOT EXISTS `tbl_leaderboards` (" +
                    "`LeaderboardID` int(11) NOT NULL AUTO_INCREMENT," +
                    "`CategoryID` int(11) DEFAULT NULL," +
                    "`UserID` int(11) DEFAULT NULL," +
                    "`Score` int(11) DEFAULT 0," +
                    "PRIMARY KEY (`LeaderboardID`)," +
                    "KEY `CategoryID` (`CategoryID`)," +
                    "KEY `UserID` (`UserID`)," +
                    "CONSTRAINT `tbl_leaderboards_ibfk_1` FOREIGN KEY (`CategoryID`) REFERENCES `tbl_categories` (`CategoryID`)," +
                    "CONSTRAINT `tbl_leaderboards_ibfk_2` FOREIGN KEY (`UserID`) REFERENCES `tbl_users` (`UserID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
            executeUpdateQuery(createLeaderboardsTableQuery);

            // Create tbl_questions table
            String createQuestionsTableQuery = "CREATE TABLE IF NOT EXISTS `tbl_questions` (" +
                    "`QuestionID` int(11) NOT NULL AUTO_INCREMENT," +
                    "`CategoryID` int(11) NOT NULL," +
                    "`QuestionText` varchar(255) DEFAULT NULL," +
                    "`Answer` varchar(255) DEFAULT NULL," +
                    "PRIMARY KEY (`QuestionID`)," +
                    "KEY `CategoryID` (`CategoryID`)," +
                    "CONSTRAINT `tbl_questions_ibfk_1` FOREIGN KEY (`CategoryID`) REFERENCES `tbl_categories` (`CategoryID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
            executeUpdateQuery(createQuestionsTableQuery);

            // Insert sample quiz questions/answers under 'Trivia' category (CategoryID = 1)
            String insertSampleQuestionsQuery = "INSERT INTO `tbl_questions` (`CategoryID`, `QuestionText`, `Answer`) VALUES " +
                    "(1, 'What color is the sun?', 'Yellow')," +
                    "(1, 'How many days are there in a week?', 'Seven')," +
                    "(1, 'What is the color of a banana?', 'Yellow')," +
                    "(1, 'What do cows drink?', 'Water')," +
                    "(1, 'What sound does a dog make?', 'Bark')," +
                    "(1, 'How many legs does a spider have?', 'Eight')," +
                    "(1, 'What is the baby of a frog called?', 'Tadpole')," +
                    "(1, 'Which animal is known as the king of the jungle?', 'Lion')," +
                    "(1, 'What is the capital of the United States?', 'WashingtonDC')," +
                    "(1, 'Which bird cannot fly?', 'Penguin')," +
                    "(1, 'What do you use to write on a blackboard?', 'Chalk')," +
                    "(1, 'How many wheels does a bicycle have?', 'Two')," +
                    "(1, 'Which fruit is red and shiny?', 'Apple')," +
                    "(1, 'Which month of the year has the least number of days?', 'February')," +
                    "(1, 'What do bees make?', 'Honey')," +
                    "(1, 'Which is the largest mammal?', 'BlueWhale')," +
                    "(1, 'What do you use to cut paper?', 'Scissors')," +
                    "(1, 'How many months of the year have 31 days?', 'Seven')," +
                    "(1, 'What is the main color of the UN flag?', 'Blue')," +
                    "(1, 'Which planet is known as the Red Planet?', 'Mars')";
            executeUpdateQuery(insertSampleQuestionsQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void executeUpdateQuery(String query) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        }
    }


    // Check a player's score in a specific category
    public int checkPlayerScore(String uuid, String categoryName) throws SQLException {
        String query = "SELECT Score FROM tbl_Leaderboards " +
                "INNER JOIN tbl_Categories ON tbl_Leaderboards.CategoryID = tbl_Categories.CategoryID " +
                "INNER JOIN tbl_Users ON tbl_Leaderboards.UserID = tbl_Users.UserID " +
                "WHERE tbl_Users.uuid = ? AND tbl_Categories.CategoryName = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, uuid);
        preparedStatement.setString(2, categoryName);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("Score");
        }

        return -1; // Return -1 if no score found for player in category
    }

    // Get the top scores for a specific category
    public List<String[]> getTopScores(String categoryName, int topPositions) throws SQLException {
        String query = "SELECT tbl_Users.UUID, tbl_Leaderboards.Score FROM tbl_Leaderboards " +
                "INNER JOIN tbl_Categories ON tbl_Leaderboards.CategoryID = tbl_Categories.CategoryID " +
                "INNER JOIN tbl_Users ON tbl_Leaderboards.UserID = tbl_Users.UserID " +
                "WHERE tbl_Categories.CategoryName = ? " +
                "ORDER BY tbl_Leaderboards.Score DESC " +
                "LIMIT ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, categoryName);
        preparedStatement.setInt(2, topPositions);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<String[]> topScores = new ArrayList<>();

        while (resultSet.next()) {
            topScores.add(new String[]{resultSet.getString("UUID"), String.valueOf(resultSet.getInt("Score"))});
        }

        return topScores;
    }

    // Get a list of all categories from the database
    public List<String> getAllCategories() throws SQLException {
        String query = "SELECT CategoryName FROM tbl_Categories";

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<String> categories = new ArrayList<>();

        while (resultSet.next()) {
            categories.add(resultSet.getString("CategoryName"));
        }

        return categories;
    }

    // Add to a player's score in a specific category
    public void addToPlayerScore(String uuid, String categoryName, int scoreIncrement) throws SQLException {
        String query = "UPDATE tbl_Leaderboards " +
                "INNER JOIN tbl_Categories ON tbl_Leaderboards.CategoryID = tbl_Categories.CategoryID " +
                "INNER JOIN tbl_Users ON tbl_Leaderboards.UserID = tbl_Users.UserID " +
                "SET tbl_Leaderboards.Score = tbl_Leaderboards.Score + ? " +
                "WHERE tbl_Users.UUID = ? AND tbl_Categories.CategoryName = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, scoreIncrement);
        preparedStatement.setString(2, uuid);
        preparedStatement.setString(3, categoryName);

        preparedStatement.execute();
    }

    // Get questions for a specific category from the database
    public List<Question> getQuestionsForCategory(String categoryName) throws SQLException {
        String query = "SELECT * FROM tbl_Questions " +
                "INNER JOIN tbl_Categories ON tbl_Questions.CategoryID = tbl_Categories.CategoryID " +
                "WHERE tbl_Categories.CategoryName = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, categoryName);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Question> questions = new ArrayList<>();

        while (resultSet.next()) {
            String question = resultSet.getString("QuestionText");
            String answer = resultSet.getString("Answer");
            questions.add(new Question(question, answer));
        }

        return questions;
    }

    // Save a player's score in a specific category to the database
    public void saveScore(String uuid, String categoryName, int score) throws SQLException {
        String queryCheck = "SELECT * FROM tbl_Leaderboards " +
                "INNER JOIN tbl_Categories ON tbl_Leaderboards.CategoryID = tbl_Categories.CategoryID " +
                "INNER JOIN tbl_Users ON tbl_Leaderboards.UserID = tbl_Users.UserID " +
                "WHERE tbl_Users.UUID = ? AND tbl_Categories.CategoryName = ?";

        PreparedStatement preparedStatementCheck = connection.prepareStatement(queryCheck);
        preparedStatementCheck.setString(1, uuid);
        preparedStatementCheck.setString(2, categoryName);

        ResultSet resultSet = preparedStatementCheck.executeQuery();

        if (!resultSet.next()) {
            // User doesn't exist in the leaderboard, so we add them
            // First, check if the user exists in tbl_Users
            String queryUserCheck = "SELECT UserID FROM tbl_Users WHERE UUID = ?";
            PreparedStatement preparedStatementUserCheck = connection.prepareStatement(queryUserCheck);
            preparedStatementUserCheck.setString(1, uuid);
            ResultSet resultSetUserCheck = preparedStatementUserCheck.executeQuery();

            int newUserID;

            if (resultSetUserCheck.next()) {
                // User already exists in tbl_Users, get their UserID
                newUserID = resultSetUserCheck.getInt("UserID");
            } else {
                // User doesn't exist in tbl_Users, so we add them
                String queryInsertUser = "INSERT INTO tbl_Users (uuid) VALUES (?)";
                PreparedStatement preparedStatementInsertUser = connection.prepareStatement(queryInsertUser, Statement.RETURN_GENERATED_KEYS);
                preparedStatementInsertUser.setString(1, uuid);
                preparedStatementInsertUser.execute();
                ResultSet generatedKeys = preparedStatementInsertUser.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newUserID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // Get CategoryID for the provided category name
            String queryCategoryId = "SELECT CategoryID FROM tbl_Categories WHERE CategoryName = ?";
            PreparedStatement preparedStatementCategoryId = connection.prepareStatement(queryCategoryId);
            preparedStatementCategoryId.setString(1, categoryName);
            ResultSet resultSetCategoryId = preparedStatementCategoryId.executeQuery();
            if (resultSetCategoryId.next()) {
                int categoryID = resultSetCategoryId.getInt("CategoryID");

                String queryInsertScore = "INSERT INTO tbl_Leaderboards (UserID, CategoryID, Score) VALUES (?, ?, ?)";
                PreparedStatement preparedStatementInsertScore = connection.prepareStatement(queryInsertScore);
                preparedStatementInsertScore.setInt(1, newUserID);
                preparedStatementInsertScore.setInt(2, categoryID);
                preparedStatementInsertScore.setInt(3, score);
                preparedStatementInsertScore.execute();
            } else {
                throw new SQLException("Category not found.");
            }
        }
    }
}
