package controller.util.liveviewparser;

import java.util.Arrays;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

public class CommandManualMeasurement extends AbstractCommand {
  private final boolean optimize;
  private StringBuilder stringBuilder;
  private LiveMeasureSequence currentMeasureSequence;

  public CommandManualMeasurement(
      LiveDataParser liveDataParser, boolean optimize) {
    super(liveDataParser);
    this.optimize = optimize;
  }

  @Override
  public void sendCommand() {
    if (optimize) {
      liveDataParser.executeCommand(Commands.m.toString());
    } else {
      liveDataParser.executeCommand(Commands.M.toString());
    }
    stringBuilder = new StringBuilder();

    currentMeasureSequence = liveDataParser.createLiveMeasurementSequence();
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder.toString());
      stringBuilder = new StringBuilder();
    }
  }

  private void handleLine(String string) {
    if (string.contains("WRIT") || string.contains("VEGIT")) {
      logger.fine("Do nothing on this input.");

    } else if (string.contains("manual_mode")) {
      currentMeasureSequence.setMetadata(string.split(";"));

    } else if (string.contains("WR") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_WR, string, currentMeasureSequence);

    } else if (string.contains("WR2")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR2, string, currentMeasureSequence);

    } else if (string.contains("WR")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR, string, currentMeasureSequence);

    } else if (string.contains("VEG") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_VEG, string, currentMeasureSequence);

    } else if (string.contains("VEG")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.VEG, string, currentMeasureSequence);

    } else if (string.contains("Voltage =")) {
      currentMeasureSequence.setComplete(true, liveDataParser.getDeviceStatus()
          .getCalibrationFile(), liveDataParser.getCurrentLiveSdCardPath());

      liveDataParser.runNextCommand();
    }
  }

  private void addValuesToMs(MeasureSequence.SequenceKeyName keyName, String string,
                             LiveMeasureSequence measureSequence) {
    String[] numbrs;
    if (Character.isDigit(string.charAt(0))) {
      String[] split = string.split(":");
      numbrs = split[3].replace(" ", "").split(";");

    } else {
      String[] split = string.replace(" ", "").split(";");
      numbrs = Arrays.copyOfRange(split, 1, split.length - 2);
    }

    double[] doubles = Arrays.stream(numbrs).filter(s -> isStringNumeric(s))
        .mapToDouble(Double::parseDouble)
        .toArray();

    measureSequence.addData(keyName, doubles);

    liveDataParser.currentMeasurementUpdated(currentMeasureSequence);
  }
}
