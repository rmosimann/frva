package controller.util.bluetooth;

import controller.LiveViewController;
import javafx.application.Platform;

public class ConnectionStateConnected implements ConnectionState {
  private final LiveViewController liveViewController;

  /**
   * Creates a ConnectionState Connected.
   *
   * @param liveViewController Object in state.
   */
  public ConnectionStateConnected(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
    this.liveViewController.createLiveSdCard();
    Platform.runLater(() -> this.liveViewController.showConsole());
  }

  @Override
  public void handle() {
    liveViewController.enableBltDisconnectButton();
  }
}
