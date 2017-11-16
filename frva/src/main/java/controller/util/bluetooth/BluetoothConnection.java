package controller.util.bluetooth;

import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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

  public static void main(String[] args) throws IOException, InterruptedException {
    BluetoothConnection.getDevicesWithSerialService();
  }


  public static void getDevicesWithSerialService()
      throws IOException, IOException, InterruptedException {
    Object inquiryCompletedEvent = new Object();
    List<RemoteDevice> remotes = new ArrayList<>();
    List<ServiceRecord[]> serviceRecords = new ArrayList<>();

    DiscoveryListener dscListener = new DiscoveryListener() {
      @Override
      public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        System.out.println("Device: " + btDevice.getBluetoothAddress());
        remotes.add(btDevice);

      }

      @Override
      public void inquiryCompleted(int discType) {
        System.out.println("Device Inquiry completed!");
        synchronized (inquiryCompletedEvent) {
          inquiryCompletedEvent.notifyAll();
        }
      }

      @Override
      public void servicesDiscovered(int transId, ServiceRecord[] servRecord) {
        System.out.println("found service");
        serviceRecords.add(servRecord);
      }

      @Override
      public void serviceSearchCompleted(int transId, int respCode) {
        System.out.println("Service Inquiry completed!");
        synchronized (inquiryCompletedEvent) {
          inquiryCompletedEvent.notifyAll();
        }
      }
    };

    synchronized (inquiryCompletedEvent) {
      boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent()
          .startInquiry(DiscoveryAgent.GIAC, dscListener);
      System.out.println("wait for device inquiry to complete...");
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
          System.out.println("wait for device inquiry to complete...");
          inquiryCompletedEvent.wait();
        } catch (BluetoothStateException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    serviceRecords.forEach(serviceRecords1 -> {
      for (ServiceRecord serviceRecord : serviceRecords1) {
        String connectionUrl = serviceRecord
            .getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false);
        System.out.println(connectionUrl);
        StreamConnection connection = null;
        try {
          System.out.println(RemoteDeviceHelper
              .authenticate(serviceRecord.getHostDevice(), "1234"));
          connection = (StreamConnection) Connector.open(connectionUrl, Connector.READ_WRITE);
          System.out.println("Authenticated");
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          OutputStream dos = connection.openOutputStream();
          dos.write("A\n".getBytes());
          dos.flush();
          System.out.println("sent C");
        } catch (IOException e) {
          e.printStackTrace();
        }


        InputStream dataIn = null;
        try {
          dataIn = connection.openInputStream();
          System.out.println("datain opened");
          int read;
          while ((read = dataIn.read()) != -1) {

            System.out.print((char) read);

          }
          //End of stream, no more data available.
        } catch (IOException e) {
          e.printStackTrace();
        }
        ;


      }
    });
  }


  public static boolean isBluetoothOn() {
    return LocalDevice.isPowerOn();
  }

}
