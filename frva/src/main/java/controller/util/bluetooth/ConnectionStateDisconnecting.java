package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateDisconnecting extends AbstractConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateDisconnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.setOpenStreamConnection(null);
    liveViewController.setSelectedServiceRecord(null);
    liveViewController.setState(new ConnectionStateSearching(liveViewController));
  }
}
