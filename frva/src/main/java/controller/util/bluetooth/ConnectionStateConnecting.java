package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;
import javax.microedition.io.StreamConnection;

public class ConnectionStateConnecting implements ConnectionState {
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

    liveViewController.displayAvailableDevicesDialog(false);
    liveViewController.setState(new ConnectionStateConnected(liveViewController));
  }
}

