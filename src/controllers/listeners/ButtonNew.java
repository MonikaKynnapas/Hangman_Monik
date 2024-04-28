package controllers.listeners;

import models.Model;
import views.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class ButtonNew implements ActionListener {
    private final Model model;
    private final View view;

    /**
     * New Game button constructor.
     * @param model Model
     * @param view View
     */
    public ButtonNew(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Here is the action that happens when the New Game button is pressed
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        view.setStartGame(); // Set the game to start
        view.hideNewButtons(); // Set access to buttons and text field
        view.getRealDateTime().stop(); // "Stop" real time
        if(!view.getGameTime().isRunning()) { // If gameTime not running
            view.getGameTime().setSeconds(0);
            view.getGameTime().setMinutes(0);
            view.getGameTime().setRunning(true); // Set game running
            view.getGameTime().startTimer(); // Start game time
        } else { // gameTime is running
            view.getGameTime().stopTimer(); // Stop gameTime
            view.getGameTime().setRunning(false); // set game not running
            view.getGameTime().getPlayedTimeInSeconds();
        }
        view.setNewImage(0);
        view.getTxtChar().requestFocus(); // After pressing New Game, the input box becomes active
        String selectedCategory = Objects.requireNonNull(view.getCmbCategory().getSelectedItem()).toString();  // the category the user selected
        model.randomWordsFromCmbNamesList(selectedCategory);  // Selects a word from a category
        model.setHiddenWord(new StringBuilder(model.getHiddenWord())); // Puts the selected word into a StringBuilder
        String wordOfNew = model.addSpaceBetween(String.valueOf(model.getHiddenWord())); // spaces between words
        view.getLblResult().setText(wordOfNew); // word into lblResult
    }
}
