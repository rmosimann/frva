package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;

public class ConnectionStateDisconnecting implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateDisconnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          BluetoothConnection.closeConnection(liveViewController.getOpenStreamConnection());
        } catch (IOException e) {
          e.printStackTrace();
        }
        liveViewController.setOpenStreamConnection(null);
        liveViewController.setSelectedServiceRecord(null);
        liveViewController.clearLiveView();
        liveViewController.setState(new ConnectionStateSearching(liveViewController));
      }

    });
    t.start();

  }
}
