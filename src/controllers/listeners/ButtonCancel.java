package controllers.listeners;

import models.Model;
import views.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonCancel implements ActionListener {
    private final Model model;
    private final View view;
    public ButtonCancel(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    /**
     * The method that is executed when the cancel game button is clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        model.getMissedLetters().clear(); // Clear missed letters
        view.showNewButton(); // Set access to buttons and text field

        view.getGameTime().stopTimer(); // Stop gameTime
        view.getGameTime().setRunning(false); // set game not running

        view.getRealDateTime().start(); // Start real time again
        view.getLblResult().setText("H A K K A M E  M Ä N G I M A !");
    }
}
