package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class ConnectionStateConnecting implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateConnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    StreamConnection streamConnection = null;

    System.out.println("try to connect");

    final StreamConnection[] connection = {null};
    for (ServiceRecord serviceRecord :  liveViewController.getSelectedServiceRecord()) {
      String connectionUrl = serviceRecord
          .getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
     // logger.info("Connecting to: " + connectionUrl);
    }


    try {
      streamConnection = BluetoothConnection.connectToService(
          liveViewController.getSelectedServiceRecord());
    } catch (IOException e) {
      liveViewController.setState(new ConnectionStateError(liveViewController));
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (streamConnection == null) {
      liveViewController.setState(new ConnectionStateError(liveViewController));
    }

    liveViewController.setOpenStreamConnection(streamConnection);

    liveViewController.displayAvailableDevicesDialog(false);
    liveViewController.setState(new ConnectionStateConnected(liveViewController));
  }
}

