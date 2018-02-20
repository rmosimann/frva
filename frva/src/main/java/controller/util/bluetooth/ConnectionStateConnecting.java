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
import java.io.IOException;
import javax.microedition.io.StreamConnection;

/**
 * The ConnectionStateConnecting represents the state while establishing a connection.
 * Followup states:
 *  on success: ConnectionStateConnected
 *  on failure: ConnectionStateError
 */
public class ConnectionStateConnecting implements ConnectionState {
  private final LiveViewController liveViewController;
  private StreamConnection streamConnection;

  public ConnectionStateConnecting(LiveViewController liveViewController) {
    this.liveViewController = liveViewController;
  }

  @Override
  public void handle() {

    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          streamConnection = BluetoothConnection.connectToService(
              liveViewController.getSelectedServiceRecord());
        } catch (IOException e) {
          liveViewController.setState(new ConnectionStateError(liveViewController));
        }
        if (streamConnection == null) {
          liveViewController.setState(new ConnectionStateError(liveViewController));
        }
        liveViewController.setOpenStreamConnection(streamConnection);

        try {
          liveViewController.getLiveDataParser()
              .startParsing(streamConnection.openInputStream(),
                  streamConnection.openOutputStream());
        } catch (IOException e) {
          e.printStackTrace();
        }


        liveViewController.displayAvailableDevicesDialog(false);
        liveViewController.setState(new ConnectionStateConnected(liveViewController));
      }
    });
    t.start();

  }
}

