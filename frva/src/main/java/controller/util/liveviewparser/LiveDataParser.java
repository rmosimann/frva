package controller.util.liveviewparser;

import controller.LiveViewController;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import model.FrvaModel;

public class LiveDataParser {

  private static final Logger logger = Logger.getLogger("FRVA");

  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private InputStream inputStream;
  private OutputStream outputStream;

  Task<Void> bltSearchingTask;

  Queue<String> commandQueue = new ArrayDeque<>();

  private final ObjectProperty<DataParserState> state = new SimpleObjectProperty<>();

  public LiveDataParser(LiveViewController liveViewController, FrvaModel model) {
    this.liveViewController = liveViewController;
    this.model = model;
  }


  /**
   * Starts the Parsing based on a connection.
   *
   * @param inputStream  the inputstream to parse.
   * @param outputStream the outputstream to write to.
   */
  public void startParsing(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    state.setValue(new DataParserStateInit(this));
    startInputParsing(inputStream);
  }

  /**
   * Add command to execute to the que whitch is processed when in ManualMode.
   *
   * @param command    the commend to execute.
   * @param stayManual true if State should rest in manual mode after all commands.
   */
  public void addComandToQueue(String command, boolean stayManual) {

    commandQueue.add(command);

  }


  private void startInputParsing(InputStream inputStream) {
    bltSearchingTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        while ((read = dataIn.read()) != -1) {
          state.getValue().handleInput((char) read);

        }
        return null;
      }
    };

    Thread thread = new Thread(bltSearchingTask);
    thread.setDaemon(true);
    thread.start();
  }


  protected void sendCommand(String command) {
    String commandWithBreak = command + "\n";

    try {
      outputStream.write(commandWithBreak.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("Sent Command: " + commandWithBreak);
  }

  public Queue<String> getCommandQueue() {
    return commandQueue;
  }

}
