package controller.util.bluetooth;

import controller.LiveViewController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class ConnectionStateSearching implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateSearching(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        if (LocalDevice.isPowerOn()) {
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
    });
    t.start();


  }

}
