package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateInit extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateInit(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    if (BluetoothConnection.isBluetoothOn()) {
      liveViewController.setState(new ConnectionStateSearching(liveViewController));
    } else {
      liveViewController.setState(new ConnectionStateBltOff(liveViewController));
    }

  }
}
