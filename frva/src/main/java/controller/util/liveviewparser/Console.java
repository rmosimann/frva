package controller.util.liveviewparser;

/**
 * Created by patrick.wigger on 29.12.17.
 */

import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 * Created by patrick.wigger on 29.12.17.
 */
public class Console extends Pane {


  protected final TextArea textArea = new TextArea();
  private StringBuffer stringBuffer;

  /**
   * Creates a Console.
   */
  public Console() {
    textArea.setStyle("-fx-text-fill: greenyellow; -fx-control-inner-background: black");
    textArea.setEditable(false);
    this.getChildren().add(textArea);
    this.textArea.setMinWidth(800);
    this.textArea.setMinHeight(600);
    stringBuffer = new StringBuffer();
  }


  public void clear() {
    runSafe(() -> textArea.clear());
  }

  public void print(final char c) {
    if (c == '\n') {
      stringBuffer.append(c);
      runSafe(() -> textArea.appendText(stringBuffer.toString()));
      stringBuffer = new StringBuffer();
    } else {
      stringBuffer.append(c);
    }
  }

  public void println(final String text) {
    Objects.requireNonNull(text, "text");
    runSafe(() -> textArea.appendText(text + System.lineSeparator()));
  }

  public void println() {
    runSafe(() -> textArea.appendText(System.lineSeparator()));
  }

  /**
   * No clue what this does. (Copied
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
