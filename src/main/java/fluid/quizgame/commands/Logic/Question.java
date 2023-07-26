package fluid.quizgame.commands.Logic;


public class Question {
    private String question; // The text of the question
    private String answer; // The correct answer to the question

    // Constructor to create a new question with its associated answer
    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    // Method to get the text of the question
    public String getQuestion() {
        return question;
    }

    // Method to get the correct answer to the question
    public String getAnswer() {
        return answer;
    }
}
