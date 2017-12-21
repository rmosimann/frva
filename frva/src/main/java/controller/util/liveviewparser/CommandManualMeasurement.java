package controller.util.liveviewparser;

import java.util.Arrays;
import javafx.application.Platform;
import model.FrvaModel;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

public class CommandManualMeasurement extends AbstractCommand {
  private StringBuilder stringBuilder;
  private LiveMeasureSequence currentMeasureSequence;

  public CommandManualMeasurement(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.M.toString());
    stringBuilder = new StringBuilder();

    currentMeasureSequence = new LiveMeasureSequence(liveDataParser.getLiveViewController());
    Platform.runLater(() -> {
      model.getLiveSequences().add(currentMeasureSequence);
    });
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
    if (string.contains("manual_mode")) {
      currentMeasureSequence.setMetadata(string.split(";"));

    } else if (string.contains("WR") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_WR, string, currentMeasureSequence);

    } else if (string.contains("WR")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR, string, currentMeasureSequence);

    } else if (string.contains("WR2")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR2, string, currentMeasureSequence);

    } else if (string.contains("VEG") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_VEG, string, currentMeasureSequence);

    } else if (string.contains("VEG")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.VEG, string, currentMeasureSequence);

    } else if (string.contains("Voltage =")) {
      currentMeasureSequence.setComplete(true, liveDataParser.getDeviceStatus().getCalibrationFile());
      liveDataParser.getLiveViewController().refreshList();
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
      String[] split = string.split(";");
      numbrs = Arrays.copyOfRange(split, 1, split.length);

    }

    double[] doubles = Arrays.stream(numbrs).filter(s -> isStringNumeric(s))
        .mapToDouble(Double::parseDouble)
        .toArray();

    measureSequence.addData(keyName, doubles);
  }
}
