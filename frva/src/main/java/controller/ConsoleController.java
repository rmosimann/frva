/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller;

import controller.util.liveviewparser.CommandAny;
import controller.util.liveviewparser.LiveDataParser;
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
 * The ConsoleController provides the core functionality to handle the console in the live view.
 * This is the controller to the console.fxml.
 */
public class ConsoleController {

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
   * Creates a ConsoleController.
   * @param liveDataParser the liveDataParser
   */
  public ConsoleController(LiveDataParser liveDataParser) {
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
   * Caches the input until the end of Line.
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
   * Make sure ConsoleController works from within FX-Threads and from non FX-Threads (Copied
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
