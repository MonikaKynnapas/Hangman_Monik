package models.datastructures;

/**
 * Data structure for database words (table words)
 */
public record DataWords(int id, String word, String category) {
    /**
     * Constructor
     */
    public DataWords {
    }
    //Getters

    /**
     * @return String
     */
    public String getWord() {
        return word;
    }
    /**
     * @return String
     */
    public String getCategory() {
        return category;
    }
}
