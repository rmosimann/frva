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
import javax.bluetooth.LocalDevice;

/**
 * The ConnectionStateError represents the state when a error has occurred.
 * Followup states:
 *  Bluetooth On: ConnectionStateAvailableDevices
 *  Bluetooth Off: ConnectionStateBltOff
 */
public class ConnectionStateError implements ConnectionState {
  private final LiveViewController liveViewController;

  public ConnectionStateError(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        if (!LocalDevice.isPowerOn()) {
          liveViewController.setState(new ConnectionStateBltOff(liveViewController));
        } else if (liveViewController.getSelectedServiceRecord() != null) {
          liveViewController.setState(new ConnectionStateAvailableDevices(liveViewController));
        }

      }
    });

    t.start();

  }
}
