package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateAvailableDevices extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateAvailableDevices(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.displayAvailableDevicesDialog(true);
  }
}
