package controller.util.liveviewparser;

/**
 * Created by patrick.wigger on 29.12.17.
 */

import java.util.Objects;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Created by patrick.wigger on 29.12.17.
 */
public class Console {

  //private final HBox statusBar;
  private final LiveDataParser liveDataParser;
  private StringBuilder stringBuilder;
  private boolean isPaused;

  @FXML
  private TextField sendAnyCommandField;
  @FXML
  private Button sendAnyCommandButton;
  @FXML
  private Button pauseOutputButton;
  @FXML
  private TextArea consoleOutput;


  /**
   * Creates a Console.
   */
  public Console(LiveDataParser liveDataParser) {
    stringBuilder = new StringBuilder();
    this.liveDataParser = liveDataParser;
  }

  @FXML
  private void initialize() {
    initializeLayout();
    defineButtonActions();
  }

  private void initializeLayout() {
    consoleOutput.setStyle("-fx-text-fill: greenyellow; -fx-control-inner-background: black");
    consoleOutput.setFont(Font.font("monospace", FontWeight.NORMAL, 14));
    consoleOutput.setEditable(false);
  }

  private void defineButtonActions() {
    isPaused = false;


    pauseOutputButton.setOnAction(e -> {
      if (isPaused) {
        isPaused = false;
        pauseOutputButton.setText("Pause Output");
      } else {
        isPaused = true;
        pauseOutputButton.setText("Resume Output");
      }
    });

    sendAnyCommandButton.setOnAction(event -> {
      String command = sendAnyCommandField.getText();
      sendAnyCommandField.setText("");
      liveDataParser.addCommandToQueue(new CommandAny(liveDataParser, command));
    });

    sendAnyCommandField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
          String command = sendAnyCommandField.getText();
          sendAnyCommandField.setText("");
          liveDataParser.addCommandToQueue(new CommandAny(liveDataParser, command));
        }
      }
    });
  }


  public void clear() {
    runSafe(() -> consoleOutput.clear());
  }

  /**
   * Caches the input until the end of Line
   * @param c input
   */
  public void print(final char c) {
    if (c == '\n' && !isPaused) {
      println(stringBuilder.toString());
      stringBuilder = new StringBuilder();
    } else {
      stringBuilder.append(c);
    }
  }

  public void println(final String text) {
    Objects.requireNonNull(text, "text");
    runSafe(() -> consoleOutput.appendText(text + System.lineSeparator()));
  }

  public void println() {
    runSafe(() -> consoleOutput.appendText(System.lineSeparator()));
  }

  /**
   * Make sure Console works from within FX-Threads and from non FX-Threads (Copied
   * from https://codereview.stackexchange.com/questions/52197/console-component-in-javafx)
   *
   * @param runnable to be run.
   */
  public static void runSafe(final Runnable runnable) {
    Objects.requireNonNull(runnable, "runnable");
    if (Platform.isFxApplicationThread()) {
      runnable.run();
    } else {
      Platform.runLater(runnable);
    }
  }

}
