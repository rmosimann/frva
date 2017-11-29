package controller.util;

import com.fazecast.jSerialComm.SerialPort;
import controller.LiveViewController;
import controller.util.liveviewparser.LiveDataParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 09.11.17.
 */
public class SerialConnection {

  private OutputStream outputStream;
  private InputStream inputStream;
  private SerialPort flox;

  /**
   * Start Connection alone.
   *
   * @param args the args
   */
  public static void main(String[] args) {
    SerialPort[] arr = SerialPort.getCommPorts();
    for (SerialPort sp : arr) {
      System.out.println(sp.getSystemPortName());

    }
    SerialPort sp = SerialPort.getCommPort("cu.JB-104-ETH-DevB");
    SerialConnection sc = new SerialConnection(sp);
    sc.start();

    //sc.read(System.out);
    //sc.send('A');

    LiveDataParser ldp = new LiveDataParser(
        new LiveViewController(new FrvaModel()), new FrvaModel());
    ldp.startParsing(sp.getInputStream(), sp.getOutputStream());


  }


  /**
   * Constructor creates a new Serial connection.
   *
   * @param flox The serialPort to which the connection should be established.
   */
  public SerialConnection(SerialPort flox) {
    this.flox = flox;
    flox.setBaudRate(57600);
    flox.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
  }

  /**
   * Starts the new serial connection.
   *
   * @return true when sucessful.
   */
  public boolean start() {
    boolean temp = flox.openPort();
    inputStream = flox.getInputStream();
    outputStream = flox.getOutputStream();
    return temp;
  }

  /**
   * Stops a serial Connection by closing port.
   *
   * @return true when sucessful.
   */
  public boolean stop() {
    return this.flox.closePort();
  }

  /**
   * sends a character to the flox via serial connection.
   *
   * @param command The command to send via serial connection.
   */
  public void send(Character command) {
    try {
      outputStream.write(command);
      outputStream.write(10);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reads the incoming Data via the serial connection.
   *
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
