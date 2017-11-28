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
import model.FrvaModel;

public class LiveDataParser {



  private static final Logger logger = Logger.getLogger("FRVA");

  public enum Commands {
    C, //Lists all commands
    B, // - Connect App
    fc, // - send calibration file
    f1, // - transfers the current raw file QE
    f2, // - transfers the current raw file FLAME
    A, // - go back to automatic mode
    O, // - Optimise
    M, // - measure without Optimisation
    m, // - measure with Optimisation
    I, // x - set integration time to x ms(eg. I 500)
    IM, // x - set maximum integration time to x ms (eg. IM 1000
    i, // x - set the interval between measurements to x s (eg. i 60)
    iL, // x - sets the cycles between FLED to x cycles, 0=off
    S, // x - sets the resolution of sent bytes to x
    a1, // x - set the QE averages to x (eg. a1 3)
    a2, // x - set the FLAME averages to x (eg. a2 3)
    G, // - Show GPS position
    c, // - ReadIn config.txt
    T, // - to set date+time
    ss, // - toggle serial stream
    st, // - toggle serial data transfer
    FLAME // - toggle FLAME spectrometer
  }



  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private InputStream inputStream;
  private OutputStream outputStream;

 Runnable runnable;

  ArrayDeque<String> commandQueue = new ArrayDeque<>();

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
   * @param command    the command to execute.
   */
  public void addCommandToQueue(String command) {
    commandQueue.add(command);
  }


  private void startInputParsing(InputStream inputStream) {
    runnable = new Runnable() {
      @Override
      public void run() {

        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        try {
          while ((read = dataIn.read()) != -1) {
            state.getValue().handleInput((char) read);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

      }


    };

    Thread thread = new Thread(runnable);
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

  public Queue<String> getCommandQueue() {
    return commandQueue;
  }

  public void setState(DataParserState state) {
    this.state.set(state);
  }

  public LiveViewController getLiveViewController() {
    return liveViewController;
  }
}
