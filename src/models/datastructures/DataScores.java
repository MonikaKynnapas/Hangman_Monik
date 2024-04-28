package models.datastructures;

import java.time.LocalDateTime;


/**
 * Data structure for database ranking (table scores)
 */
public record DataScores(LocalDateTime gameTime, String playerName, String word, String missedLetters,
                         int timeSeconds) {


    /**
     * Constructor
     */
    public DataScores {
    }

    // Getters

    /**
     * @return LocalDateTime
     */
    public LocalDateTime getGameTime() {
        return gameTime;
    }

    /**
     * @return String
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return String
     */
    public String getGuessWord() {
        return word;
    }

    /**
     * @return String
     */
    public String getMissingLetters() {
        return missedLetters;
    }
}
