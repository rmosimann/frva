package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateAvailableDevices implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateAvailableDevices(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.displayAvailableDevicesDialog(true);
  }
}
