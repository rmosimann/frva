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

import com.intel.bluetooth.BlueCoveImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * The BluetoothConnection provides static functionality to access the systems BluetoothStack
 * and to perform the following actions:
 *  - Discover devices with SerialPortProfile
 *  - Connect to a Bluetooth service
 *  - Close connection.
 */
public class BluetoothConnection {

  private static final Logger logger = Logger.getLogger("FRVA");


  /**
   * Discovers all available BluetoothDevices and then scans tham for SPP-Services.
   *
   * @return List of all services found (Contains the remoteDevice).
   * @throws IOException          when bluetooth has a problem.
   * @throws InterruptedException an exception.
   */
  public static List<ServiceRecord[]> getDevicesWithSerialService()
      throws IOException, InterruptedException {

    Object inquiryCompletedEvent = new Object();
    List<RemoteDevice> remotes = new ArrayList<>();
    List<ServiceRecord[]> serviceRecords = new ArrayList<>();

    DiscoveryListener dscListener = new DiscoveryListener() {
      @Override
      public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        remotes.add(btDevice);

        logger.info("Bluetooth: discovered device - " + btDevice.getBluetoothAddress());

      }

      @Override
      public void inquiryCompleted(int discType) {
        logger.info("Bluetooth: Device Inquiry completed");
        synchronized (inquiryCompletedEvent) {
          inquiryCompletedEvent.notifyAll();
        }
      }

      @Override
      public void servicesDiscovered(int transId, ServiceRecord[] servRecord) {
        logger.info("Bluetooth: found serial service");
        serviceRecords.add(servRecord);
      }

      @Override
      public void serviceSearchCompleted(int transId, int respCode) {
        logger.info("Bluetooth: service search completed");
        synchronized (inquiryCompletedEvent) {
          inquiryCompletedEvent.notifyAll();
        }
      }
    };

    synchronized (inquiryCompletedEvent) {
      boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent()
          .startInquiry(DiscoveryAgent.GIAC, dscListener);

      logger.info("Bluetooth: Device Inquiry started");
      inquiryCompletedEvent.wait();
    }

    UUID serialPort = new UUID(0x1101);
    UUID[] searchUuidSet = new UUID[] {serialPort};

    remotes.forEach(remoteDevice -> {
      synchronized (inquiryCompletedEvent) {
        try {
          LocalDevice.getLocalDevice().getDiscoveryAgent()
              .searchServices(null, searchUuidSet, remoteDevice, dscListener);

          logger.info("Bluetooth: service search started");
          inquiryCompletedEvent.wait();
        } catch (InterruptedException | BluetoothStateException e) {
          e.printStackTrace();
        }
      }
    });

    return serviceRecords;
  }


  /**
   * Opens a connection to a service on a Bluetoothdevice.
   * PIN handling is done by the clientsystem.
   *
   * @param serviceRecords The service to connect to.
   * @return a connection where the stream can be opened.
   * @throws IOException when a problem with bluetooth.
   */
  public static StreamConnection connectToService(ServiceRecord[] serviceRecords)
      throws IOException {
    StreamConnection connection = null;
    for (ServiceRecord serviceRecord : serviceRecords) {
      String connectionUrl = serviceRecord
          .getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
      logger.info("Connecting to: " + connectionUrl);
      connection = (StreamConnection) Connector.open(connectionUrl, Connector.READ_WRITE);
      logger.info("Connected to device");
    }


    return connection;
  }


  /**
   * Closes a Connection.
   *
   * @param streamConnection the Connection to close.
   * @throws IOException when there is a problem.
   */
  public static void closeConnection(StreamConnection streamConnection) throws IOException {
    streamConnection.close();
    BlueCoveImpl.shutdown();
  }


}
