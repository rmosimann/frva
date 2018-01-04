package controller.util.bluetooth;

import controller.LiveViewController;
import javax.bluetooth.LocalDevice;

public class ConnectionStateInit implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateInit(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        if (LocalDevice.isPowerOn()) {
          liveViewController.setState(new ConnectionStateSearching(liveViewController));
        } else {
          liveViewController.setState(new ConnectionStateBltOff(liveViewController));
        }

      }
    });

    t.start();
  }
}
