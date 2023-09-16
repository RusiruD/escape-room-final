package nz.ac.auckland.se206.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import nz.ac.auckland.se206.Controller;

public class Inventory {
  @FXML
  private static ObservableList<String> inventory = FXCollections.observableArrayList();

  public static void addToInventory(String string) {
    inventory.add(string);
    update();
  }

  public static ObservableList<String> getInventory() {
    return inventory;
  }

  public static void removeFromInventory(String string) {
    inventory.remove(string);
    update();
  }

  private static void update() {

    for (Controller controller : SceneManager.getControllers()) {
      controller.updateInventory();
    }
  }
}