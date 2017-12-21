package controller.util.liveviewparser;

import java.util.ArrayList;
import java.util.List;
import model.FrvaModel;
import model.data.CalibrationFile;

public class CommandGetCalibration extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  List<String> data;

  /**
   * Creates a CommandGetCalibration instance that reads Calibrationfile from liveDevice.
   *
   * @param liveDataParser where all the datahandling with the LiveDevice happens.
   * @param model          the one and only Model.
   */
  public CommandGetCalibration(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);


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
      System.out.println("got Calib File");
      liveDataParser.getDeviceStatus().setCalibrationFile(
          new CalibrationFile(data));
      liveDataParser.runNextCommand();
    } else if (!(stringBuilder.toString().contains("cal.csv"))) {

      data.add(stringBuilder.toString());

    }
  }
}
