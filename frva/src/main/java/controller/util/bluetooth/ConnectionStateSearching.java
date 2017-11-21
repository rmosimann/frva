package controller.util.bluetooth;

import controller.LiveViewController;
import javafx.concurrent.Task;

public class ConnectionStateSearching implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateSearching(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    liveViewController.displaySearchingDialog(true);

    try {
      Task<Void> bla = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          liveViewController.setAvailableServiceRecords(
              BluetoothConnection.getDevicesWithSerialService());
          return null;
        }
      };
      bla.setOnSucceeded(event -> {
            liveViewController.displaySearchingDialog(false);
            liveViewController.setState(new ConnectionStateAvailableDevices(liveViewController));
          }
      );


      Thread thread = new Thread(bla);
      thread.setDaemon(true);
      thread.start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
