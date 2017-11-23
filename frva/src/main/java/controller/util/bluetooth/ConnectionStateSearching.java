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
    if (BluetoothConnection.isBluetoothOn()) {
      liveViewController.displaySearchingDialog(true);
      try {
        Task<Void> bltSearchingTask = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            liveViewController.setAvailableServiceRecords(
                BluetoothConnection.getDevicesWithSerialService());
            return null;
          }
        };
        bltSearchingTask.setOnSucceeded(event -> {
          liveViewController.displaySearchingDialog(false);
          liveViewController.setState(new ConnectionStateAvailableDevices(liveViewController));
        });
        bltSearchingTask.setOnFailed(event -> {
          liveViewController.displaySearchingDialog(false);
          liveViewController.setState(new ConnectionStateError(liveViewController));
        });

        Thread thread = new Thread(bltSearchingTask);
        thread.setDaemon(true);
        thread.start();

      } catch (Exception e) {
        liveViewController.setState(new ConnectionStateError(liveViewController));
      }
    } else {
      liveViewController.setState(new ConnectionStateError(liveViewController));
    }

  }
}
