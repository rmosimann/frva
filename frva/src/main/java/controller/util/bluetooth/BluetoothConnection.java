package controller.util.bluetooth;

import com.intel.bluetooth.BlueCoveImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class BluetoothConnection {

  private static final Logger logger = Logger.getLogger("FRVA");

  /**
   * TEST this.
   *
   * @param args empty.
   * @throws IOException          some.
   * @throws InterruptedException some.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    List<ServiceRecord[]> devicesWithSerialService =
        BluetoothConnection.getDevicesWithSerialService();
    StreamConnection connection = connectToService(devicesWithSerialService.get(0));

    OutputStream dos = connection.openOutputStream();
    dos.write("A\n".getBytes());
    dos.flush();
    System.out.println("sent C");

    InputStream dataIn = connection.openInputStream();
    System.out.println("datain opened");
    int read;
    while ((read = dataIn.read()) != -1) {
      System.out.print((char) read);
    }
  }


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
        System.out.println(remoteDevice.getBluetoothAddress());
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

    BlueCoveImpl.shutdown();
    return serviceRecords;
  }


  /**
   * Opens a connection to a service on a Bluetoothdevice.
   * PIN handling is done by the clientsystem.
   *
   * @param serviceRecords The service to connect to.
   * @return a connection where the stream can be opened.
   */
  public static StreamConnection connectToService(ServiceRecord[] serviceRecords)
      throws IOException {
    StreamConnection connection = null;
    for (ServiceRecord serviceRecord : serviceRecords) {
      String connectionUrl = serviceRecord
          .getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
      logger.info("Connecting to: " + connectionUrl);
      connection = (StreamConnection) Connector.open(connectionUrl, Connector.READ_WRITE);
    }
    BlueCoveImpl.shutdown();
    return connection;
  }


  /**
   * Closes a Connection.
   *
   * @param streamConnection the Connection to close.
   */
  public static void closeConnection(StreamConnection streamConnection) throws IOException {
    streamConnection.close();
    BlueCoveImpl.shutdown();
  }


  /**
   * Checks the powerstate of the systems bluetooth device.
   *
   * @return true when enabled.
   */
  public static boolean isBluetoothOn() {
    return LocalDevice.isPowerOn();
  }

}
