package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;
import java.io.InputStream;
import javafx.concurrent.Task;
import javax.microedition.io.StreamConnection;

public class ConnectionStateConnecting extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateConnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    StreamConnection streamConnection = null;

    try {
      streamConnection = BluetoothConnection.connectToService(
          liveViewController.getSelectedServiceRecord());
    } catch (IOException e) {
      liveViewController.setState(new ConnectionStateError(liveViewController));
    }

    if (streamConnection == null) {
      liveViewController.setState(new ConnectionStateError(liveViewController));
    }

    liveViewController.setOpenStreamConnection(streamConnection);


    //TODO: remove this
    liveViewController.getMiniTerminalTextArea().appendText("starting");

    StreamConnection finalStreamConnection = streamConnection;
    Task<Void> bltSearchingTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        InputStream dataIn = null;
        dataIn = finalStreamConnection.openInputStream();
        System.out.println("datain opened");
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

    liveViewController.getMiniTerminalTextArea().setEditable(false);

    liveViewController.displayAvailableDevicesDialog(false);

    //    liveViewController.setState(new ConnectionStateConnected(liveViewController));
  }

}

