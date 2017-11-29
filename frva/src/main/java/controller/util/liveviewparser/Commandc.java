package controller.util.liveviewparser;

import java.util.Arrays;
import java.util.Vector;
import model.FrvaModel;

public class Commandc extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private boolean parsing3fldPixels = false;
  private Vector<Number> fldPixels = new Vector<>();

  public Commandc(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.c.toString());
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
    if (stringBuilder.toString().contains("Max IT[ms] =")) {
      liveDataParser.getDeviceStatus().setMaxIntegrationTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Interval[s] =")) {
      liveDataParser.getDeviceStatus().setIntervalTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Serial resolution = ")) {
      liveDataParser.getDeviceStatus().setSerialResolution(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Serial stream")) {
      liveDataParser.getDeviceStatus().setSerialStream(parseOnOff(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Serial data transfer")) {
      liveDataParser.getDeviceStatus().setSerialDataTransfer(parseOnOff(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("LED on each:")) {
      liveDataParser.getDeviceStatus().setLedOnEachCycle(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("LED power:")) {
      liveDataParser.getDeviceStatus().setLedPower(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("3FLD pixels")) {
      parsing3fldPixels = true;
      liveDataParser.getDeviceStatus().setLedPower(parseNumber(stringBuilder.toString()));
    }
    if (parsing3fldPixels) {
      fldPixels.add(parseNumber(stringBuilder.toString()));
      if (fldPixels.size() == 5) {
        parsing3fldPixels = false;
        liveDataParser.getDeviceStatus().setFldPixels(Arrays.toString(fldPixels.toArray()));
      }
    }
    if (stringBuilder.toString().contains("QE averages = ")) {
      liveDataParser.getDeviceStatus().setQeAverages(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("FLAME averages = ")) {
      liveDataParser.getDeviceStatus().setFlameAverages(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("config.txt written")) {
      liveDataParser.runNextCommand();
    }
  }


}
