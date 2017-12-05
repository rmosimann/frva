package controller.util.bluetooth;

import controller.LiveViewController;

public class ConnectionStateInit implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateInit(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    //TODO evaluate how to do this on OSX
    if (true) {
      liveViewController.setState(new ConnectionStateSearching(liveViewController));
    } else {
      liveViewController.setState(new ConnectionStateBltOff(liveViewController));
    }

  }
}
