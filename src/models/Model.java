package models;

import models.datastructures.DataScores;
import models.datastructures.DataWords;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;
/**
 * A model where the entire logic of the game must take place
 */
public class Model {


    private final String databaseFile = "hangman_words_ee.db"; // Default database
    private List<String> imageFiles = new ArrayList<>(); // All images with full folder path
    private String[] cmbNames; // ComboBox categories names (contents)
    private final String chooseCategory = "Kõik kategooriad"; // Default first ComboBox choice
    private DefaultTableModel dtmScores; // Leaderboard DefaultTableModel
    private List<DataScores> dataScores = new ArrayList<>(); // The contents of the entire database table scores
    private int imageId = 0; // Current image id (0..11)
    private String selectedCategory = chooseCategory; // Default all categories as "All categories"

    private List<String> missedLetters = new ArrayList<>();
    public int countMissedWords;
    private String playerName;
    private final List<DataWords> dataWords;
    private String[] categories;
    private String wordToGuess;
    private StringBuilder hiddenWord;
    private Connection connection = null;

    private int gametime;

    /**
     *
     * Constructor
     */
    public Model() {
        new Database(this);
        dataWords = new ArrayList<>();
        hiddenWord = new StringBuilder();
        wordsSelect();
    }

    private Connection dbConnection() throws SQLException {
        if(connection != null) {
            connection.close();
        }
        String dbUrl = "jdbc:sqlite:" + databaseFile;
        connection = DriverManager.getConnection(dbUrl);
        return connection;
    }


    /**
     * Sets the content to match the ComboBox. Adds "All categories" and unique categories obtained from the database.
     * @param unique all unique categories from database
     */
    public void setCorrectCmbNames(List<String> unique) {
        cmbNames = new String[unique.size()+1];
        cmbNames[0] = chooseCategory; // First choice before categories
        int x = 1;
        for(String category : unique) {
            cmbNames[x] = category;
            x++;
        }
    }

    /**
     * All ComboBox contents
     * @return ComboBox contents
     */
    public String[] getCmbNames() {
        return cmbNames;
    }

    /**
     * Sets a new DefaultTableModel
     * @param dtmScores DefaultTableModel
     */
    public void setDtmScores(DefaultTableModel dtmScores) {
        this.dtmScores = dtmScores;
    }

    /**
     * ALl leaderbaord content
     * @return List<DataScores>
     */
    public List<DataScores> getDataScores() {
        return dataScores;
    }

    /**
     * Sets the new content of the leaderboard
     * @param dataScores List<DataScores>
     */
    public void setDataScores(List<DataScores> dataScores) {
        this.dataScores = dataScores;
    }

    /**
     * Returns the configured database file
     * @return databaseFile
     */
    public String getDatabaseFile() {
        return databaseFile;
    }

    /**
     * Returns the default table model (DefaultTableModel)
     * @return DefaultTableModel
     */
    public DefaultTableModel getDtmScores() {
        return dtmScores;
    }

    /**
     * Returns the images folder
     * @return String
     */
    public String getImagesFolder() {
        // Hangman game images location
        String imagesFolder = "images";
        return imagesFolder;
    }

    /**
     * Sets up an array of new images
     * @param imageFiles List<String>
     */
    public void setImageFiles(List<String> imageFiles) {
        this.imageFiles = imageFiles;
    }

    /**
     * An array of images
     * @return List<String>
     */
    public List<String> getImageFiles() {
        return imageFiles;
    }

    /**
     * Sets a new selected category
     * @param selectedCategory new category
     */
    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /**
     * Random word
     */
    public void randomWordsFromCmbNamesList (String selectedCategory){
        Random random = new Random();
        List<String> guessWordsToList = new ArrayList<>();
        if (selectedCategory.equals("Kõik kategooriad")){
            wordToGuess = dataWords.get(random.nextInt(dataWords.size())).getWord();
        } else {
            boolean categoryExists = false;
            for (DataWords word : dataWords) {
                if (selectedCategory.equals(word.getCategory())) {
                    categoryExists = true;
                    guessWordsToList.add(word.getWord());
                }
            }
            if (!categoryExists || guessWordsToList.isEmpty()) {
                wordToGuess = dataWords.get(random.nextInt(dataWords.size())).getWord();
            } else {
                wordToGuess = guessWordsToList.get(random.nextInt(guessWordsToList.size()));
            }
        }
        this.wordToGuess = wordToGuess.toUpperCase();
        hideLetters();
    }

    /**
     * Hides the words
     */
    private void hideLetters() {
        hiddenWord = new StringBuilder();
        hiddenWord.append("_".repeat(wordToGuess.length()));
    }

    /**
     * SELECT statement to read the contents of the words table and add information to the dataWords list
     */
    public void wordsSelect() {
        String sql = "SELECT * FROM words ORDER BY category, word";
        List<String> categories = new ArrayList<>();
        try {
            Connection conn = this.dbConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            dataWords.clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("word");
                String category = rs.getString("category");
                dataWords.add(new DataWords(id, word, category));
                categories.add(category);
            }
            List<String> unique = categories.stream().distinct().collect(Collectors.toList());
            setCorrectCmbNames(unique);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return String
     */
    public List<String> getMissedLetters(){
        return missedLetters;
    }

    /**
     * Returns the included word
     */
    public void setMissedLetters(List<String> missedLetters) {
        this.missedLetters = missedLetters;
    }

    /**
     * Asks for the player's name
     */
    public void askPlayerName() {
        playerName = JOptionPane.showInputDialog("Sisesta oma nimi");
        if (playerName.length() < 2) {
            askPlayerName();
        }
    }


    /**
     * @return A modified word with spaces added between letters.
     */
    public String addSpaceBetween(String word) {  // We split the word into letters and put them in an array
        String[] wordListOfList= word.split("");  // We create a StringJoiner object to join letters with spaces
        StringJoiner joiner = new StringJoiner(" ");  // We use StringJoiner to join letters with spaces
        for (String words : wordListOfList){
            joiner.add(words);
        }
        return joiner.toString(); // we type the modified word with spaces added between the letters
    }

    /**
     * Returns the word to guess
     */
    public String getWordToGuess() {return wordToGuess;}


    /**
     * Returns any selected word with the middle words hidden under "_".
     */
    public StringBuilder getHiddenWord() {
        return hiddenWord;
    }

    /**
     * Returns the name of the player
     */
    public String getPlayerName() {return playerName;}

    /**
     * Enters the player's data into the leaderboard
     */
    public void insertScoreToTable (){

        String sql = "INSERT INTO scores (playertime, playername, guessword, wrongcharacters, gametime) VALUES (?,?, ?, ?,?)";
        String removeBrackets = getMissedLetters().toString().replace("[", "").replace("]", "");
        DataScores endTime = new DataScores(LocalDateTime.now(), getPlayerName(), getWordToGuess(), removeBrackets, getGametime());

        try {
            Connection conn = this.dbConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String playerTime = endTime.getGameTime().format(formatter);
            preparedStmt.setString(1, playerTime);
            preparedStmt.setString(2, endTime.getPlayerName());
            preparedStmt.setString(3, endTime.getGuessWord());
            preparedStmt.setString(4, endTime.getMissingLetters());
            preparedStmt.setInt(5, getGametime());

            preparedStmt.executeUpdate();
            selectScores();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the leaderboard
     */
    private void selectScores() {}


    /**
     *
     * Returns an unexpected word
     */
    public int getCountMissedWords() {
        return countMissedWords;
    }

    /**
     * Returns the included word
     */
    public void setCountMissedWords(int countMissedWords) {
        this.countMissedWords = countMissedWords;
    }

    /**
     * Returns the guessed word
     */
    public void setHiddenWord(StringBuilder hiddenWord) {
        this.hiddenWord = hiddenWord;
    }

    /**
     * Get game time in seconds
     */
    public int getGametime() {
        return gametime;
    }

    /**
     * Sets the game time in seconds
     */
    public void setGametime(int gametime) {
        this.gametime = gametime;
    }
}

