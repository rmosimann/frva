package controller.util.bluetooth;

import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;

public interface ConnectionState {
  public void handle();

  public StreamConnection connectTo(ServiceRecord[] serviceRecords);

}
