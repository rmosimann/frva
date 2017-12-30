package controller.util.liveviewparser;

import java.util.ArrayList;
import java.util.List;
import model.data.CalibrationFile;

public class CommandGetCalibration extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  List<String> data;

  /**
   * Creates a CommandGetCalibration instance that reads Calibrationfile from liveDevice.
   *
   * @param liveDataParser where all the datahandling with the LiveDevice happens.
   */
  public CommandGetCalibration(LiveDataParser liveDataParser) {
    super(liveDataParser);


    data = new ArrayList<>();
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.fc.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder);
      stringBuilder = new StringBuilder();
    }
  }

  private void handleLine(StringBuilder stringBuilder) {

    if (stringBuilder.toString().contains("FILE ENDS")) {

      liveDataParser.getDeviceStatus().setCalibrationFile(
          new CalibrationFile(data));
      liveDataParser.runNextCommand();
    } else if (!(stringBuilder.toString().contains("cal.csv"))) {

      data.add(stringBuilder.toString());

    }
  }
}
