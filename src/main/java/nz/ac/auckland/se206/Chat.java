package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

public class Chat {
  private static Chat instance;

  public static Chat getInstance() {
    return instance;
  }

  @FXML private TextArea chatTextArea;
  @FXML private TextArea lastHintArea;

  private ChatCompletionRequest chatCompletionRequest;
  private List<TextArea> variousChatScreens;
  private boolean isThinking;
  private boolean showLastHintOnly;

  public Chat() {
    isThinking = true;
    instance = this;
    showLastHintOnly = false;
    chatTextArea = new TextArea();
    lastHintArea = new TextArea();
    variousChatScreens = new ArrayList<>();
  }

  public void addChat(TextArea chat) {
    variousChatScreens.add(chat);
  }

  public void initialiseAfterStart() throws ApiProxyException {

    if (GameState.currentDifficulty == GameState.Difficulty.MEDIUM) {}

    // Create a CompletableFuture for the background task

    CompletableFuture.runAsync(
        () -> {
          // Configure the chat completion request
          chatCompletionRequest =
              new ChatCompletionRequest()
                  .setN(1)
                  .setTemperature(0.7)
                  .setTopP(0.8)
                  .setMaxTokens(100);

          // Run GPT-3 with an initial hint request
          runGpt(new ChatMessage("user", GptPromptEngineering.getHint()));
          isThinking = false;
        });
  }

  private void appendChatMessage(ChatMessage msg, String role) {

    String message = role + ": " + msg.getContent() + "\n\n";

    lastHintArea.appendText(message);
    chatTextArea.appendText(message);
    updateChats();
  }

  private ChatMessage runGpt(ChatMessage msg) {
    chatCompletionRequest.addMessage(msg);
    try {
      // Execute the chat completion request with GPT-3
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());

      // Append the GPT-3 response to the chat interface
      appendChatMessage(result.getChatMessage(), "Dungeon Master");

      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void onSendMessage(
      String inputText,
      TextArea actualText,
      Button sendButton,
      Button switchButton,
      Label hintField)
      throws ApiProxyException, IOException {

    if (isThinking) {
      return;
    }

    disableNode(sendButton);
    disableNode(switchButton);
    lastHintArea.clear();

    String message = inputText;

    // If the message is empty, return early
    if (message.trim().isEmpty()) {
      return;
    }

    // Append the fake message to the chat interface
    appendChatMessage(new ChatMessage("user", message), "Player");

    CompletableFuture.runAsync(
        () -> {
          isThinking = true;

          // Clear the input field and create actual and fake chat messages
          String hint = "";
          if (GameState.currentRoom == GameState.State.CHEST) {
            if (GameState.hasKeyOne && GameState.hasKeyTwo && GameState.hasKeyThree) {
              hint = "\"The riddle gives the information of the keys unlock the chest\"";
            } else {
              hint = "\"Search the dungeon for three keys\"";
            }
          } else if (GameState.currentRoom == GameState.State.MARCELLIN) {
            hint = "\"Move the points of the shape such that no lines between points overlap.\"";
          } else if (GameState.currentRoom == GameState.State.ZACH) {
            hint = "\"Investigate the door to find a sliding puzzle\"";
          } else if (GameState.currentRoom == GameState.State.RUSIRU) {
            if (GameState.noPapers == true) {
              hint = "\"Pick up all the papers.\"";
            } else if (GameState.noCombination == true && GameState.noPapers == false) {
              hint = "\"Put all the papers on the table and read it.\"";
            } else if (GameState.noPotionBoulder == true && GameState.noCombination == false) {
              hint = "\"Brew a potion of strength to move the big rock.\"";
            }
          }

          String contextMsg;
          if (GameState.currentDifficulty == GameState.Difficulty.HARD) {
            contextMsg = message;
          } else if (GameState.currentDifficulty == GameState.Difficulty.EASY) {
            contextMsg = GptPromptEngineering.hintPrompt(message, hint);
          } else {
            if (GameState.hintsGiven < 5) {
              contextMsg = GptPromptEngineering.hintPrompt(message, hint);
            } else {
              contextMsg = GptPromptEngineering.noHintPrompt(message);
            }
          }

          ChatMessage actualMessage = new ChatMessage("user", contextMsg);

          if (runGpt(actualMessage).getContent().toLowerCase().substring(0, 4).equals("hint")) {
            GameState.hintsGiven++;
            System.out.println("HINT DETECTED!");
          }
          isThinking = false;
          // Ensure that the UI updates on the JavaFX application thread
          Platform.runLater(
              () -> {
                enableNode(switchButton);
                enableNode(sendButton);
                int hintsLeft = 5 - GameState.hintsGiven;
                if (hintsLeft < 0) {
                  hintsLeft = 0;
                }
                hintField.setText(hintsLeft + " Hints(s) Remaining");
              });
        });
  }

  private void updateChats() {

    for (TextArea textArea : variousChatScreens) {
      if (showLastHintOnly) {
        textArea.setText(lastHintArea.getText());
      } else {
        textArea.setText(chatTextArea.getText());
      }
    }
  }

  public void lastHintToggle() {
    showLastHintOnly = !showLastHintOnly;
    updateChats();
  }

  private void enableNode(Object node) {
    Node actualNode = (Node) node;
    actualNode.setVisible(true);
    actualNode.setDisable(false);
  }

  private void disableNode(Object node) {
    Node actualNode = (Node) node;
    actualNode.setVisible(false);
    actualNode.setDisable(true);
  }

  public void massDisable(
      TextArea textArea,
      TextField inputText,
      Button closeButton,
      Button showButton,
      ImageView chatBackground,
      Button sendButton,
      Button switchButton,
      Label hintField) {

    disableNode(textArea);
    disableNode(inputText);
    disableNode(closeButton);
    enableNode(showButton);
    disableNode(chatBackground);
    disableNode(sendButton);
    disableNode(switchButton);
    disableNode(hintField);
  }

  public void massEnable(
      TextArea textArea,
      TextField inputText,
      Button closeButton,
      Button showButton,
      ImageView chatBackground,
      Button sendButton,
      Button switchButton,
      Label hintField) {

    enableNode(textArea);
    enableNode(inputText);
    enableNode(closeButton);
    disableNode(showButton);
    enableNode(chatBackground);
    enableNode(sendButton);
    enableNode(switchButton);
    enableHintField(hintField);
  }

  private void enableHintField(Label hintField) {
    if (GameState.currentDifficulty == GameState.Difficulty.MEDIUM) {
      enableNode(hintField);
    }
  }
}
