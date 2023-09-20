package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.TimerCounter;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class StartScreenController {

  @FXML private ChoiceBox<String> timerChoice;
  @FXML private ChoiceBox<String> difficultyChoice;

  @FXML
  private void initialize() {

    // Add items to the choice box
    timerChoice.getItems().add("2 Minutes");
    timerChoice.getItems().add("4 Minutes");
    timerChoice.getItems().add("6 Minutes");
    timerChoice.setValue("2 Minutes");
    difficultyChoice.getItems().add("Easy");
    difficultyChoice.getItems().add("Medium");
    difficultyChoice.getItems().add("Hard");
    difficultyChoice.setValue("Easy");

    // You can perform additional initialization here if needed.
  }

  @FXML
  private void onStartGame(ActionEvent event) throws ApiProxyException {

    // Get the chosen values from the choice box
    String chosenTimeLimit = timerChoice.getValue();
    String chosenDifficulty = difficultyChoice.getValue();
    GameState.gameTime = chosenTimeLimit;
    GameState.difficultyLevel = chosenDifficulty;

    // Create a new timer object
    TimerCounter time = new TimerCounter();

    if (chosenTimeLimit.equals("2 Minutes")) {
      time.timerStart(120);
    } else if (chosenTimeLimit.equals("4 Minutes")) {
      time.timerStart(240);
    } else {
      time.timerStart(360);
    }

    if (chosenDifficulty.equals("Easy")) {
      GameState.hintsLeft = 999;
    } else if (chosenDifficulty.equals("Medium")) {
      GameState.hintsLeft = 3;
    } else {
      GameState.hintsLeft = 0;
    }

    ChatController.getInstance().intialiseHints();

    timerChoice.getStyleClass().add("choice-box");
    difficultyChoice.getStyleClass().add("choice-box");

    App.returnToCorridor();
  }
}
