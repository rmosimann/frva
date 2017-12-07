package controller.util.bluetooth;

import controller.LiveViewController;
import javax.bluetooth.LocalDevice;

public class ConnectionStateBltOff implements ConnectionState {

  private final LiveViewController liveViewController;

  public ConnectionStateBltOff(LiveViewController liveViewController) {

    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t= new Thread(new Runnable() {
      @Override
      public void run() {
        if (LocalDevice.isPowerOn()) {
          liveViewController.displayBluetoothOffDialog(false);
          liveViewController.setState(new ConnectionStateSearching(liveViewController));
        } else {
          liveViewController.displayBluetoothOffDialog(true);
        }
      }
    });
    t.start();

  }
}
