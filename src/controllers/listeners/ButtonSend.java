package controllers.listeners;

import models.Model;
import views.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Class for the Send letter button
 */
public class ButtonSend implements ActionListener {

    private final Model model;
    private final View view;

    /**
     * Constructor
     */

    public ButtonSend(Model model, View view) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        view.getTxtChar().requestFocus(); // After pressing New Game, the input box becomes active
        String enteredChar = view.getTxtChar().getText().toUpperCase(); // Takes the character
        if (enteredChar.isEmpty()) { // If the entered letter is empty, nothing is done
            return;
        }

        char guessedLetter = enteredChar.charAt(0); // Takes the first letter
        String guessWord = model.getWordToGuess(); // A word that the player has to guess
        String[] guessList = guessWord.split(""); // Makes a word an array
        System.out.println("GuessList: " + Arrays.toString(guessList));  // Prints an array

        checkGuess(guessList, guessedLetter, enteredChar);  //  Checking whether the entered letter is correct or incorrect

        view.getTxtChar().setText(""); // Clears the entered character
        checkGameStatus(); // Checks whether the player won or lost
    }

    /**
     * Checks whether the entered letter is correct or incorrect
     */
    public void checkGuess(String[] guessList, char guessedLetter, String enteredChar) {
        boolean correct = false;
        for (int i = 0; i < guessList.length; i++) { // Loops through an array
            if (guessList[i].equals(enteredChar)) { // If the letter entered is in the array
                model.getHiddenWord().setCharAt(i, guessedLetter); // Places the entered letter in the correct position
                view.getLblResult().setText(model.addSpaceBetween(model.getHiddenWord().toString())); // word with the letter entered
                correct = true;
            }
        }
        handleIncorrectGuess(enteredChar, correct);
        model.setCountMissedWords(model.getMissedLetters().size());
        view.getLblError().setText("Valesti: " + model.getCountMissedWords() + " täht(e) " + model.getMissedLetters()); // incorrectly counted number of letters
        view.getGameImages().updateImage(); // Update the image
    }

    private void handleIncorrectGuess(String enteredChar, boolean correct) {
        if (!correct) {
            model.getMissedLetters().add(enteredChar); // Adds a wrong letter
            view.getLblError().setForeground(Color.RED); // Shows the wrong letter in red
            model.setCountMissedWords(model.getCountMissedWords() + 1);  // incorrectly counted number of letters
            view.getGameImages().updateImage(); // Update the image
        } else {
            view.getLblError().setForeground(Color.BLACK);  // Shows correctly guessed letter in black
        }
    }

    /**
     * Checks whether the player won or lost
     */
    private void checkGameStatus() {
        if (!model.getHiddenWord().toString().contains("_")) {  // If there are no more underscores in the word, then

            JOptionPane.showMessageDialog(null, "Võitsid mängu", "Mäng läbi", JOptionPane.PLAIN_MESSAGE); // Kuvab teate, et mängija võitis
            view.getGameTime().stopTimer();  // Stops game time
            int playedTimeInSeconds = view.getGameTime().getPlayedTimeInSeconds();  // played time in seconds
            model.setGametime(playedTimeInSeconds);  // Records the time played

            model.askPlayerName(); // Asks for the player's name
            model.getPlayerName();
            model.insertScoreToTable();  // Adds the player's name and time played to the table

            view.setEndGame();  // Ends the game
            return;

        }
        if (!(model.getCountMissedWords() < 11)) {
            JOptionPane.showMessageDialog(null, "Kaotasid mängu", "Mäng läbi", JOptionPane.PLAIN_MESSAGE); // Displays a message that the player lost
            view.setEndGame(); // Ends the game
        }
    }
}
