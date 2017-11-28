package controller.util.liveviewparser;

import controller.LiveViewController;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import model.FrvaModel;

public class LiveDataParser {
  private final LiveViewController liveViewController;
  private final FrvaModel model;
  private InputStream inputStream;
  private OutputStream outputStream;

  Task<Void> bltSearchingTask;

  Queue<String> commandQueue = new ConcurrentLinkedQueue<>();

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
    state.setValue(new DataParserStateInit());
    startInputParsing(inputStream);
  }


  private void startInputParsing(InputStream inputStream) {
    bltSearchingTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        InputStream dataIn = null;
        dataIn = inputStream;
        int read;
        while ((read = dataIn.read()) != -1) {
          System.out.print((char) read);
        }
        return null;
      }
    };

    Thread thread = new Thread(bltSearchingTask);
    thread.setDaemon(true);
    thread.start();
  }

}
