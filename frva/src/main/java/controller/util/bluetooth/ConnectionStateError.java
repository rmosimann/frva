package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateError implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateError(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    //TODO evaluate how to do this on OSX

    if (true) {
      liveViewController.setState(new ConnectionStateBltOff(liveViewController));
    } else if (liveViewController.getSelectedServiceRecord() != null) {
      liveViewController.setState(new ConnectionStateConnecting(liveViewController));
    }

  }
}
