package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.ChestController;
import nz.ac.auckland.se206.controllers.CorridorController;
import nz.ac.auckland.se206.controllers.PuzzleController;
import nz.ac.auckland.se206.controllers.PuzzleRoomController;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.controllers.SceneManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.UntangleRoomController;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  public static SceneManager.AppUi oldScene = null;
  public static SceneManager.AppUi newScene = AppUi.CORRIDOR;

  private static Scene scene;

  private static Parent root;

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(SceneManager.AppUi appUi) throws IOException {

    oldScene = newScene;
    newScene = appUi;

    scene.setRoot(SceneManager.getUiRoot(appUi));

    root.requestFocus();
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneManager.addUi(AppUi.UNTANGLE, loadFxml("untangleRoom"));
    SceneManager.addUi(AppUi.LEADERBOARD, loadFxml("leaderboard"));
    SceneManager.addUi(AppUi.FIRST_ROOM, loadFxml("room"));
    SceneManager.addUi(AppUi.CORRIDOR, loadFxml("corridor"));
    SceneManager.addUi(AppUi.START, loadFxml("startScreen"));
    SceneManager.addUi(AppUi.PUZZLE, loadFxml("puzzle"));
    SceneManager.addUi(AppUi.PUZZLEROOM, loadFxml("puzzleroom"));
    SceneManager.addUi(AppUi.CHEST, loadFxml("chest"));
    SceneManager.addUi(AppUi.CHAT, loadFxml("chat"));

    SceneManager.addController(PuzzleRoomController.getInstance());
    SceneManager.addController(RoomController.getInstance());
    SceneManager.addController(CorridorController.getInstance());
    SceneManager.addController(PuzzleController.getInstance());
    SceneManager.addController(UntangleRoomController.getInstance());
    SceneManager.addController(ChestController.getInstance());

    root = SceneManager.getUiRoot(AppUi.START);
    scene = new Scene(root, 780.0, 780.0);
    stage.setScene(scene);
    stage.show();
    focus();
  }

  public static void focus() {
    root.requestFocus();
  }

  public static void returnToCorridor() {

    try {

      App.setRoot(AppUi.CORRIDOR);

      CorridorController corridorController = CorridorController.getInstance();
      double corridorWidth = corridorController.getCorridorWidth();

      double corridorHeight = corridorController.getCorridorHeight();

      Stage primaryStage = (Stage) scene.getWindow();

      primaryStage.setWidth(corridorWidth + 15);
      primaryStage.setHeight(corridorHeight + 38);

    } catch (IOException e) {

      e.printStackTrace();
    }
    focus();
  }

  public static void goToDoor1() {
    try {
      App.setRoot(AppUi.FIRST_ROOM);

      RoomController roomController = RoomController.getInstance();
      double roomWidth = roomController.getRoomWidth();
      double roomHeight = roomController.getRoomHeight();
      Stage primaryStage = (Stage) scene.getWindow();
      primaryStage.setWidth(roomWidth + 15);
      primaryStage.setHeight(roomHeight + 38);

    } catch (IOException e) {
      e.printStackTrace();
    }
    focus();
  }

  public static void goToDoor2() {
    try {
      App.setRoot(AppUi.UNTANGLE);
      UntangleRoomController untangleroomController = UntangleRoomController.getInstance();
      double untangleroomWidth = untangleroomController.getUntangleRoomWidth();
      double untangleroomHeight = untangleroomController.getUntangleRoomHeight();
      Stage primaryStage = (Stage) scene.getWindow();
      primaryStage.setWidth(untangleroomWidth + 15);
      primaryStage.setHeight(untangleroomHeight + 38);

    } catch (IOException e) {
      e.printStackTrace();
    }
    focus();
  }

  public static void goToDoor3() {
    try {
      App.setRoot(AppUi.PUZZLEROOM);
      PuzzleRoomController puzzleroomController = PuzzleRoomController.getInstance();
      double puzzleroomWidth = puzzleroomController.getPuzzleRoomWidth();
      double puzzleroomHeight = puzzleroomController.getPuzzleRoomHeight();
      Stage primaryStage = (Stage) scene.getWindow();
      primaryStage.setWidth(puzzleroomWidth + 15);
      primaryStage.setHeight(puzzleroomHeight + 38);

    } catch (IOException e) {
      e.printStackTrace();
    }
    focus();
  }
}
