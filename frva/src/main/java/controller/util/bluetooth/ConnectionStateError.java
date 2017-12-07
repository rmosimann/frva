package controller.util.bluetooth;

import controller.LiveViewController;
import javax.bluetooth.LocalDevice;

public class ConnectionStateError implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateError(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t= new Thread(new Runnable() {
      @Override
      public void run() {
        if (!LocalDevice.isPowerOn()) {
          liveViewController.setState(new ConnectionStateBltOff(liveViewController));
        } else if (liveViewController.getSelectedServiceRecord() != null) {
          liveViewController.setState(new ConnectionStateConnecting(liveViewController));
        }

      }
    });

    t.start();

  }
}
