package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class ConnectionStateConnecting implements ConnectionState {
  private final LiveViewController liveViewController;
  private StreamConnection streamConnection;

  public ConnectionStateConnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {

    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
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

        try {
          liveViewController.getLiveDataParser()
              .startParsing(streamConnection.openInputStream(), streamConnection.openOutputStream());
        } catch (IOException e) {
          e.printStackTrace();
        }


        liveViewController.displayAvailableDevicesDialog(false);
        liveViewController.setState(new ConnectionStateConnected(liveViewController));
      }
    });
    t.start();

  }
}

