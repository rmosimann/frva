package controller.util.liveviewparser;

import controller.LiveViewController;
import controller.util.DeviceStatus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import model.FrvaModel;
import model.data.LiveMeasureSequence;

public class LiveDataParser {
  private static final Logger logger = Logger.getLogger("FRVA");

  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private OutputStream outputStream;

  private Runnable inputStreamReader;

  private final ArrayDeque<CommandInterface> commandQueue = new ArrayDeque<>();
  private final ObjectProperty<CommandInterface> currentCommand = new SimpleObjectProperty<>();
  private final BooleanProperty acceptingCommands = new SimpleBooleanProperty();
  private final BooleanProperty initializing = new SimpleBooleanProperty(false);

  private final Executor singleExecutor = Executors.newSingleThreadExecutor(runnable -> {
    Thread t = new Thread(runnable);
    t.setDaemon(true);
    return t;
  });

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
      liveViewController.setCurrentCommandLabels(
          currentCommand.getValue().getClass().getSimpleName());
      if (currentCommand.getValue() instanceof CommandIdle) {
        acceptingCommands.setValue(true);
        initializing.setValue(false);
      } else {
        acceptingCommands.setValue(false);
      }
      if (currentCommand.getValue() instanceof CommandAutoMode) {
        initializing.setValue(false);
      }
    });

    currentCommand.setValue(new CommandInitialize(this));
    currentCommand.getValue().sendCommand();
    initializing.setValue(true);

    startInputParsing(inputStream);
  }

  /**
   * Add command to execute to the queue. Informs currentCommand.
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
    inputStreamReader = new Runnable() {
      @Override
      public void run() {

        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        try {
          while ((read = dataIn.read()) != -1) {
            liveViewController.printToConsole((char) read);
            System.out.print((char) read);
            currentCommand.getValue().receive((char) read);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };

    Thread thread = new Thread(inputStreamReader);
    thread.setDaemon(true);
    thread.start();
  }


  /**
   * Sends a String to the OutputStream.
   *
   * @param command the string without linebreak.
   */
  void executeCommand(String command) {

    Task<Integer> task = new Task<Integer>() {
      @Override
      protected Integer call() throws Exception {


        try {
          outputStream.write(command.getBytes());
          outputStream.write(10);
          outputStream.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        logger.info("Sent Command: " + command);

        return null;
      }
    };

    singleExecutor.execute(task);

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
      currentCommand.setValue(new CommandIdle(this));
    }
  }

  /**
   * Creates a LiveMeasurementSequence and adds it to the Model.
   *
   * @return the newly created MeasureSequence.
   */
  public LiveMeasureSequence createLiveMeasurementSequence() {
    LiveMeasureSequence liveMeasureSequence = new LiveMeasureSequence(liveViewController);
    model.addLiveSequence(liveMeasureSequence);
    return liveMeasureSequence;
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

  public boolean isInitializing() {
    return initializing.get();
  }

  public BooleanProperty initializingProperty() {
    return initializing;
  }

  public File getCurrentLiveSdCardPath() {
    return model.getCurrentLiveSdCardPath();
  }
}
