package controller.util.liveviewparser;

import controller.LiveViewController;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.FrvaModel;

public class LiveDataParser {
  private static final Logger logger = Logger.getLogger("FRVA");

  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private InputStream inputStream;
  private OutputStream outputStream;

  Runnable inputStremReader;

  private final ArrayDeque<CommandInterface> commandQueue = new ArrayDeque<>();
  private final ObjectProperty<CommandInterface> currentCommand = new SimpleObjectProperty<>();

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
    currentCommand.setValue(new CommandInitialize(this, model));
    currentCommand.getValue().sendCommand();

    startInputParsing(inputStream);

    currentCommand.addListener(observable -> {
      logger.info("New Command: " + currentCommand.getValue().toString());
    });

  }

  /**
   * Add command to execute to the que whitch is processed when in ManualMode.
   *
   * @param command the command to execute.
   */
  public void addCommandToQueue(CommandInterface command) {
    commandQueue.add(command);
    currentCommand.getValue().onQueueUpdate();
  }


  private void startInputParsing(InputStream inputStream) {
    inputStremReader = new Runnable() {
      @Override
      public void run() {

        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        try {
          while ((read = dataIn.read()) != -1) {
            currentCommand.getValue().receive((char) read);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };

    Thread thread = new Thread(inputStremReader);
    thread.setDaemon(true);
    thread.start();
  }


  protected void sendCommand(String command) {
    try {
      outputStream.write(command.getBytes());
      outputStream.write(10);
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.info("Sent Command: " + command);
  }

  public ArrayDeque<CommandInterface> getCommandQueue() {
    return commandQueue;
  }

  public void setCurrentCommand(CommandInterface currentCommand) {
    this.currentCommand.set(currentCommand);
  }

  public LiveViewController getLiveViewController() {
    return liveViewController;
  }

  public void runNextCommand() {
    if (commandQueue.size() > 0) {
      currentCommand.setValue(commandQueue.poll());
      currentCommand.getValue().sendCommand();
    } else {
      currentCommand.setValue(new CommandIdle(this, model));
    }
  }
}
