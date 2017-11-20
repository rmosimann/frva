package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateBltOff implements ConnectionState {

  private final LiveViewController liveViewController;

  public ConnectionStateBltOff(LiveViewController liveViewController) {

    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    if (BluetoothConnection.isBluetoothOn()) {
      liveViewController.displayBluetoothOffDialog(false);
      liveViewController.setState(new ConnectionStateSearching(liveViewController));
    } else {
      liveViewController.displayBluetoothOffDialog(true);
    }
  }
}
