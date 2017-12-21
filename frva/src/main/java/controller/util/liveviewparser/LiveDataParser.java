package controller.util.liveviewparser;

import controller.LiveViewController;
import controller.util.DeviceStatus;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.FrvaModel;

public class LiveDataParser {
  private static final Logger logger = Logger.getLogger("FRVA");

  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private OutputStream outputStream;

  private Runnable inputStremReader;

  private final ArrayDeque<CommandInterface> commandQueue = new ArrayDeque<>();
  private final ObjectProperty<CommandInterface> currentCommand = new SimpleObjectProperty<>();
  private final BooleanProperty acceptingCommands = new SimpleBooleanProperty();

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
    this.outputStream = outputStream;
    currentCommand.addListener(observable -> {
      liveViewController.setCurrentCommandLabel(
          currentCommand.getValue().getClass().getSimpleName());
      if (currentCommand.getValue() instanceof CommandIdle) {
        acceptingCommands.setValue(true);
      } else {
        acceptingCommands.setValue(false);
      }
    });

    currentCommand.setValue(new CommandInitialize(this, model));
    currentCommand.getValue().sendCommand();

    startInputParsing(inputStream);
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


  /**
   * Creates a Thread where the input from the InputStrem is handled.
   *
   * @param inputStream inputStran to handle.
   */
  private void startInputParsing(InputStream inputStream) {
    inputStremReader = new Runnable() {
      @Override
      public void run() {

        FileWriter fw= null;
        try {
          fw = new FileWriter(new File(getClass().getClassLoader().getResource("out.csv").getFile()));
        } catch (IOException e) {
          e.printStackTrace();
        }

        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        try {
          while ((read = dataIn.read()) != -1) {
            System.out.print((char) read);
            fw.append((char)read);
            fw.flush();
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

  /**
   * Sends a String to the OutputStream.
   *
   * @param command the string without linebreak.
   */
  void executeCommand(String command) {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          System.out.println("send command");
          outputStream.write(command.getBytes());
          outputStream.write(10);
          outputStream.flush();
          logger.info("Sent Command: " + command);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
  }

  /**
   * Polls next command from CommandQueue and executes it.
   * When Queue is empty IdleMode is activated.
   */
  void runNextCommand() {
    if (commandQueue.size() > 0) {
      currentCommand.setValue(commandQueue.poll());
      currentCommand.getValue().sendCommand();
    } else {
      currentCommand.setValue(new CommandIdle(this, model));
    }
  }

  public ArrayDeque<CommandInterface> getCommandQueue() {
    return commandQueue;
  }

  public LiveViewController getLiveViewController() {
    return liveViewController;
  }

  public void setCurrentCommand(CommandInterface currentCommand) {
    this.currentCommand.set(currentCommand);
  }

  public DeviceStatus getDeviceStatus() {
    return liveViewController.getDeviceStatus();
  }

  public boolean isAcceptingCommands() {
    return acceptingCommands.get();
  }

  public BooleanProperty acceptingCommandsProperty() {
    return acceptingCommands;
  }

}
