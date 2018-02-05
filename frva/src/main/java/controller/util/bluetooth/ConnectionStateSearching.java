/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller.util.bluetooth;

import controller.LiveViewController;
import javafx.concurrent.Task;
import javax.bluetooth.LocalDevice;

/**
 * The ConnectionStateSEarching represents the state while the application is searching for
 * available devices.
 * Followup states:
 *  on success: ConnectionStateAvailableDevices
 *  on failed: ConnectionStateError
 */
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
