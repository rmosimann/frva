package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateSearching implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateSearching(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.displaySearchingDialog(true);
    try {
      liveViewController.setAvailableServiceRecords(
          BluetoothConnection.getDevicesWithSerialService());
    } catch (Exception e) {
      e.printStackTrace();
    }
    liveViewController.displaySearchingDialog(false);
  }
}
