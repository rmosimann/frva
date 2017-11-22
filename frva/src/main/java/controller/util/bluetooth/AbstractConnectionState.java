package controller.util.bluetooth;

import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AbstractConnectionState implements ConnectionState {
  @Override
  public void handle() {
    throw new NotImplementedException();
  }

  @Override
  public StreamConnection connectTo(ServiceRecord[] serviceRecords) {
    throw new NotImplementedException();
  }

}
