package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Controller;
import nz.ac.auckland.se206.CustomNotifications;
import nz.ac.auckland.se206.DungeonMaster;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.Instructions;
import nz.ac.auckland.se206.Riddle;
import nz.ac.auckland.se206.Utililty;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class RoomController implements Controller {
  private static RoomController instance;

  public static RoomController getInstance() {
    return instance;
  }

  public static Color convertStringToColor(String colorName) {
    switch (colorName) {
      case "Red Potion":
        return Color.RED;
      case "Green Potion":
        return Color.GREEN;

      case "Blue Potion":
        return Color.BLUE;
      case "Purple Potion":
        return Color.PURPLE;
      case "Yellow Potion":
        return Color.YELLOW;

        // Add more color mappings as needed
      default:
        return Color.BLACK;
    } // Default to black if the color name is not recognized
  }

  public static Color calculateAverageColor(Color color1, Color color2) {
    double avgRed = (color1.getRed() + color2.getRed()) / 2.0;
    double avgGreen = (color1.getGreen() + color2.getGreen()) / 2.0;
    double avgBlue = (color1.getBlue() + color2.getBlue()) / 2.0;

    return new Color(avgRed, avgGreen, avgBlue, 1.0); // Alpha value set to 1.0 (fully opaque)
  }

  @FXML private Pane potionsRoomPane;
  @FXML private Pane popUp;
  @FXML private Pane instructionsDisplay;
  @FXML private Pane visualDungeonMaster;

  @FXML private ComboBox<String> inventoryChoiceBox;
  @FXML private Button btnReturnToCorridor;
  

  
  @FXML private Label lblTime;
 
  @FXML private ImageView key1;

  @FXML private ImageView boulder;

  @FXML private ImageView note;

  @FXML private ImageView yellowPotion;
  @FXML private ImageView redPotion;
  @FXML private ImageView bluePotion;
  @FXML private ImageView greenPotion;
  @FXML private ImageView purplePotion;
  
 
  @FXML private ImageView cauldron;


  
  @FXML private ImageView exclamationMark;
  @FXML private TextArea chatTextArea;

 
  @FXML private ImageView soundToggle;

  @FXML private Button btnHideNote;
  private double horizontalOffset = 0;
  private double verticalOffset = 0;
  private List<String> potionsincauldron = new ArrayList<>();
 

  private Riddle call;

  @FXML
  public double getRoomWidth() {

    return potionsRoomPane.getPrefWidth();
  }

  @FXML
  public double getRoomHeight() {

    return potionsRoomPane.getPrefHeight();
  }

  @FXML
  public void updateTimerLabel(String time) {
    lblTime.setText(time);
  }

  @FXML
  public void getInstructions(MouseEvent event) {
    // Set the instructions pane to be visible and not mouse transparent
    GameState.noCombination = false;
    instructionsDisplay.visibleProperty().set(true);
    instructionsDisplay.mouseTransparentProperty().set(false);
    instructionsDisplay.toFront();
  }

  @FXML
  public void getAi(MouseEvent event) {
    callAi(call);
  }

  // Call the AI to give a hint
  private void callAi(Riddle call) {
    // Get the dungeon master and the pop up pane
    DungeonMaster dungeonMaster = call.getDungeonMaster();
    Pane dialogue = dungeonMaster.getPopUp();
    Pane dialogueFormat = dungeonMaster.paneFormat(dialogue, dungeonMaster);
    popUp.toFront();
    popUp.getChildren().add(dialogueFormat);
    // Set the dialogue to be visible and not mouse transparent
    dialogueFormat.getStyleClass().add("popUp");
    visualDungeonMaster.visibleProperty().set(false);
    visualDungeonMaster.mouseTransparentProperty().set(true);
  }

  public void initialize() throws ApiProxyException {
    // Set the instance variable to this object
    instance = this;
    popUp.toBack();
    visualDungeonMaster.visibleProperty().set(false);
    visualDungeonMaster.mouseTransparentProperty().set(true);
    // Set the dungeon master to be invisible and mouse transparent
    TranslateTransition translateTransition = GameState.translate(exclamationMark);
    translateTransition.play();
    // Set the dungeon master to be invisible and mouse transparent
    String instructionsString =
        "Pieces of parchment are all over the room and you see a key gleaming behind the boulder."
            + " \n"
            + "Combine the parchments on the table to see what they have to say. \n";
    Instructions instructions = new Instructions(instructionsString);
    Pane instructionsPane = instructions.getInstructionsPane();
    instructionsDisplay.getChildren().add(instructionsPane);
    instructionsPane.getStyleClass().add("riddle");

    instructionsDisplay.visibleProperty().set(false);
    instructionsDisplay.mouseTransparentProperty().set(true);
    // Set style sheets
    chatTextArea
        .getStylesheets()
        .add(getClass().getResource("/css/roomStylesheet.css").toExternalForm());
    chatTextArea.getStyleClass().add("text-area .content");
    btnHideNote.getStyleClass().add("custom-button");
    String[] colors = {"Blue", "Yellow", "Purple", "Red", "Green"};

    Random random = new Random();
    // Randomly select two colors from the array
    int firstIndex = random.nextInt(colors.length);
    String firstPotion = colors[firstIndex];
    GameState.firstPotion = "" + firstPotion + " Potion";
    int secondIndex;
    do {
      secondIndex = random.nextInt(colors.length);
    } while (secondIndex == firstIndex); // Ensure the second color is different from the first
    // Randomly select two colors from the array
    String secondPotion = colors[secondIndex];
    GameState.secondPotion = secondPotion + " Potion";
    // Create a new dungeon master
    DungeonMaster dungeonMaster = new DungeonMaster();
    String message =
        "Congratulate a player for moving the boulder to get the key commenting on them mixing the "
            + firstPotion
            + " and the "
            + secondPotion
            + " to increase their strength. Keep this short";
    call = new Riddle(dungeonMaster, message);
    // Set the tiles and solution
    chatTextArea.appendText(
        "Dear Future Captives,\nI was close, so very close, to mastering the potion. \n Mix the "
            + firstPotion
            + " potion and "
            + secondPotion
            + " potion in the cauldron the fumes should give you incredible Power. \n"
            + "I pray you succeed where I couldn't. In fading memory,A Lost Soul");
    // Set the tiles and solution
  

    // Allow the boulder to be dragged and dropped

  }

  @FXML
  public void getHint() throws IOException {
    App.goToChat();
  }

  @FXML
  private void enlarge(ImageView image) {
    image.setScaleX(1.5);
    image.setScaleY(1.5);
  }

  // When the cauldron is clicked, the potion is added to the cauldron
  @FXML
  private void onCauldronClicked(MouseEvent event) {

    // Check if a potion is selected in the combo box
    String selectedItem = inventoryChoiceBox.getSelectionModel().getSelectedItem();

    // if a potion is selected it is made visible in the scene
    if (selectedItem != null && selectedItem.contains("redPotion")) {
      // if the potion is already in the cauldron, it is not added again
      potionsincauldron.add("Red Potion");
      // the potion is removed from the combo box
      Inventory.removeFromInventory(selectedItem);
      // the inventory is updated
      updateInventory();

      // repeat for the other potions
    } else if (selectedItem != null && selectedItem.contains("bluePotion")) {
      potionsincauldron.add("Blue Potion");

      Inventory.removeFromInventory(selectedItem);

      updateInventory();

      // repeat for the other potions
    } else if (selectedItem != null && selectedItem.contains("greenPotion")) {
      potionsincauldron.add("Green Potion");

      Inventory.removeFromInventory(selectedItem);

      updateInventory();
      // repeat for the other potions
    } else if (selectedItem != null && selectedItem.contains("yellowPotion")) {
      potionsincauldron.add("Yellow Potion");

      Inventory.removeFromInventory(selectedItem);

      updateInventory();
      // repeat for the other potions
    } else if (selectedItem != null && selectedItem.contains("purplePotion")) {
      potionsincauldron.add("Purple Potion");

      Inventory.removeFromInventory(selectedItem);

      updateInventory();
    }
    // if the two correct potions are in the cauldron, the boulder is made draggable
    if (potionsincauldron.contains(GameState.firstPotion)
        && potionsincauldron.contains(GameState.secondPotion)) {
      tintScene(potionsRoomPane);
      CustomNotifications.generateNotification(
          "Something Happens!",
          "You feel far stronger... like energy's coursing through you and you could move"
              + " anything...");
      GameState.noPotionBoulder = false;
      allowImageToBeDragged(boulder);
    }
  }

  @FXML
  private void shrink(ImageView image) {
    image.setScaleX(1.0);
    image.setScaleY(1.0);
  }

  @FXML
  private void addToInventory(ImageView image) {
    GameState.noPapers = false;
    image.setVisible(false);
    image.setDisable(true);
 
    Inventory.addToInventory(image.getId());
    updateInventory();
    
  }

  @FXML
  private void addToInventoryFromScene(MouseEvent event) {
    ImageView image = (ImageView) event.getSource();
    image.setVisible(false);
    image.setDisable(true);

    Inventory.addToInventory(image.getId());
    updateInventory();
  }

  private void setRandomPosition(ImageView imageView) {
    double minX = 200; // Minimum x-coordinate (left)
    double minY = 250; // Minimum y-coordinate (top)S
    double maxX = 850; // Maximum x-coordinate (right)
    double maxY = 560; // Maximum y-coordinate (bottom)

    // Randomly generate initial positions for the images
    double initialX = Math.random() * (maxX - minX) + minX;
    double initialY = Math.random() * (maxY - minY) + minY;

    imageView.setLayoutX(initialX);
    imageView.setLayoutY(initialY);
  }

  public void updateInventory() {
    inventoryChoiceBox.setItems(Inventory.getInventory());
    inventoryChoiceBox.setStyle(" -fx-effect: dropshadow(gaussian, #ff00ff, 10, 0.5, 0, 0);");

    // Create a Timeline to revert the shadow back to its original state after 2 seconds
    Duration duration = Duration.seconds(0.5);
    javafx.animation.Timeline timeline = new javafx.animation.Timeline(
        new javafx.animation.KeyFrame(duration, event -> {
            // Revert the CSS style to remove the shadow (or set it to the original style)
            inventoryChoiceBox.setStyle("");
        })
    );
    timeline.play();
    
  }

  
  @FXML
  private void onHideNote() {
    chatTextArea.setVisible(false);
    chatTextArea.setDisable(true);
    btnHideNote.setDisable(true);
    btnHideNote.setVisible(false);
  }

  @FXML
  private void showNote() {
    note.setVisible(true);
    note.setDisable(false);
  }

  @FXML
  private void showNoteWithoutButton() {
    note.setVisible(true);
    note.setDisable(false);
  }

  // Allow the image to be dragged and dropped
  @FXML
  private void allowImageToBeDragged(ImageView image) {
    // When the mouse is pressed it records the offset from the top left corner
    image.setOnMousePressed(
        (MouseEvent event) -> {
          horizontalOffset = event.getSceneX() - image.getLayoutX();
          verticalOffset = event.getSceneY() - image.getLayoutY();
        });
    // When the mouse is dragged it sets the new position of the image
    image.setOnMouseDragged(
        (MouseEvent event) -> {
          double newX = event.getSceneX() - horizontalOffset;
          double newY = event.getSceneY() - verticalOffset;
          image.setLayoutX(newX);
          image.setLayoutY(newY);
        });
  }

  @FXML
  private void enlargeItem(MouseEvent event) {
    enlarge((ImageView) event.getSource());
  }

  @FXML
  private void shrinkItem(MouseEvent event) {
    shrink((ImageView) event.getSource());
  }

  @FXML
  private void clickedParchment(MouseEvent event) {
    addToInventory((ImageView) event.getSource());
  }

  @FXML
  private void onNoteClicked(MouseEvent event) {
    // Check if a note is selected in the combo box
    System.out.println("Note clicked");
    GameState.noCombination = false;
    chatTextArea.setVisible(true);
    chatTextArea.setDisable(false);
    // if a note is selected it is made visible in the scene
    addToInventory(note);
    btnHideNote.setDisable(false);
    btnHideNote.setVisible(true);
  }

  @FXML
  private void onKey1Clicked(MouseEvent event) {
    GameState.hasKeyOne = true;
    addToInventory(key1);
    GameState.isKey1Collected = true;

    visualDungeonMaster.visibleProperty().set(true);
    visualDungeonMaster.mouseTransparentProperty().set(false);
  }

  @FXML
  private void onReturnToCorridorClicked(ActionEvent event) {
    App.returnToCorridor();
    GameState.currentRoom = GameState.State.CHEST;
  }

  private void tintScene(Pane potionsRoomPane) {
    Color colour1 = convertStringToColor(GameState.firstPotion);
    Color colour2 = convertStringToColor(GameState.secondPotion);
    Color colour3 = calculateAverageColor(colour1, colour2);
    // Create a colored rectangle to overlay the scene

    Rectangle tintRectangle =
        new Rectangle(potionsRoomPane.getWidth(), potionsRoomPane.getHeight(), colour3);
    tintRectangle.setOpacity(0); // Initially, make it fully transparent

    // Add the rectangle to the root layout
    potionsRoomPane.getChildren().add(tintRectangle);

    // Create a timeline animation to control the tint effect
    Timeline timeline =
        new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(tintRectangle.opacityProperty(), 0.0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(tintRectangle.opacityProperty(), 0.60)),
            new KeyFrame(Duration.seconds(2), new KeyValue(tintRectangle.opacityProperty(), 0.0)));
    timeline.setOnFinished(
        event -> {
          potionsRoomPane
              .getChildren()
              .remove(tintRectangle); // Remove the tint rectangle from the root
        });
    // Play the animation
    timeline.play();
  }

  @FXML
  private void onTableClicked(MouseEvent event) {

    // Check if a note is selected in the combo box
    String selectedItem = inventoryChoiceBox.getSelectionModel().getSelectedItem();

    if (selectedItem != null && selectedItem.contains("note")) {

      Inventory.removeFromInventory(selectedItem);

      showNoteWithoutButton();
      return;
    }
    // if a parchment piece is selected it is made visible in the scene
    // and the parchment piece is removed from the combo box
    // if already three pieces are visible the note is shown instead
   
       
  }

  @FXML
  private void clickExit(MouseEvent event) {
    // Handle click on exit
    Utililty.exitGame();
  }

  @FXML
  private void mute() {
    // Handle click on mute
    GameState.mute();
  }

  @FXML
  public void updateMute() {
    if (!GameState.isMuted) {
      soundToggle.setImage(new ImageView("images/sound/audioOn.png").getImage());
      return;
    }
    soundToggle.setImage(new ImageView("images/sound/audioOff.png").getImage());
  }
}
