package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateConnected implements ConnectionState {
  private final LiveViewController liveViewController;

  /**
   * Creates a ConnectionState Connected.
   * @param liveViewController Object in state.
   */
  public ConnectionStateConnected(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
    this.liveViewController.createLiveSdCard();
    this.liveViewController.showConsole();
  }

  @Override
  public void handle() {
    liveViewController.enableBltDisconnectButton();
  }
}
