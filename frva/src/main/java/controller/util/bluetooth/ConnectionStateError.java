package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateError extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateError(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    if (!BluetoothConnection.isBluetoothOn()) {
      liveViewController.setState(new ConnectionStateBltOff(liveViewController));
    } else if (liveViewController.getSelectedServiceRecord() != null) {
      liveViewController.setState(new ConnectionStateConnecting(liveViewController));
    }

  }
}
