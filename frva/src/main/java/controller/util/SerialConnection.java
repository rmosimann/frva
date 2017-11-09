package controller.util;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by patrick.wigger on 09.11.17.
 */
public class SerialConnection {

  private OutputStream outputStream;
  private InputStream inputStream;
  private SerialPort flox;

  /**
   * Constructor creates a new Serial connection.
   * @param flox The serialPort to which the connection should be established.
   */
  public SerialConnection(SerialPort flox) {
    this.flox = flox;
    flox.setBaudRate(57600);
    flox.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
    inputStream = flox.getInputStream();
    outputStream = flox.getOutputStream();
  }

  /**
   * Starts the new serial connection.
   * @return true when sucessfull.
   */
  public boolean start() {
   return flox.openPort();
  }

  /**
   * Stops a serial Connection by closing port.
   * @return true when sucessful.
   */
  public boolean stop(){
    return this.flox.closePort();
  }

  /**
   *
   * @param command The command to send via serial connection
   * @throws IOException
   */
  public void send(Character command) throws IOException {
    outputStream.write(command);
    outputStream.write(10);
  }

  /**
   * Reads the incoming Data via the serial connection.
   * @param printStream the stream where the data is sent to.
   */
  public void read(PrintStream printStream) {

    Thread thread = new Thread(() -> {
      try {
        while (true) {
          while (inputStream.available() > 0) {
            int i = inputStream.read();
            printStream.print(((char) i));
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    });
    thread.start();
  }

}
