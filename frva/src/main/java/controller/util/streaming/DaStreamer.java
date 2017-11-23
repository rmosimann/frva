package controller.util.streaming;

import java.io.InputStream;
import javafx.concurrent.Task;
import javax.microedition.io.StreamConnection;

public class DaStreamer {

  /**
   * Jou, creates da streamer.
   *
   * @param streamConnection nice shit.
   */
  public DaStreamer(StreamConnection streamConnection) {
    StreamConnection finalStreamConnection = streamConnection;
    Task<Void> bltSearchingTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        InputStream dataIn = null;
        dataIn = finalStreamConnection.openInputStream();
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
