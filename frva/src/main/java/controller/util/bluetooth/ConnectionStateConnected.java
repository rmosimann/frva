package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateConnected extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateConnected(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.enableBltDisconnectButton();
  }
}
