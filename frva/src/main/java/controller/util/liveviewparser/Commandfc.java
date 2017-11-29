package controller.util.liveviewparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import model.FrvaModel;
import model.data.CalibrationFile;

public class Commandfc extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  private Vector<Double> wlF1;
  private Vector<Double> wlF2;
  private Vector<Double> upCoefF1;
  private Vector<Double> upCoefF2;
  private Vector<Double> dwCoefF1;
  private Vector<Double> dwCoefF2;
  private List<String> metadata;

  /**
   * Creates a Commandfc instance that reads Calibrationfile from liveDevice.
   *
   * @param liveDataParser where all the datahandling with the LiveDevice happens.
   * @param model          the one and only Model.
   */
  public Commandfc(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
    this.wlF1 = new Vector<>();
    this.wlF2 = new Vector<>();
    this.upCoefF1 = new Vector<>();
    this.upCoefF2 = new Vector<>();
    this.dwCoefF1 = new Vector<>();
    this.dwCoefF2 = new Vector<>();
    this.metadata = new ArrayList<>();
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
          new CalibrationFile(wlF1, upCoefF1, dwCoefF1, wlF2, upCoefF2, dwCoefF2, metadata));
      liveDataParser.runNextCommand();
    } else if (!(stringBuilder.toString().contains("cal.csv") || stringBuilder.toString()
        .contains("wl_F;up_coef_F;dw_coef_F;wl_F;up_coef_F;dw_coef_F;Device ID"))) {

      String[] splitLine = stringBuilder.toString().split(";");

      wlF1.add(Double.parseDouble(splitLine[0]));
      upCoefF1.add(Double.parseDouble(splitLine[1]));
      dwCoefF1.add(Double.parseDouble(splitLine[2]));
      wlF2.add(Double.parseDouble(splitLine[3]));
      upCoefF2.add(Double.parseDouble(splitLine[4]));
      dwCoefF2.add(Double.parseDouble(splitLine[5]));
      if (splitLine.length > 6) {
        metadata.add(splitLine[6]);
      }
    }
  }
}
