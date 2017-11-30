package controller.util.liveviewparser;

import java.util.Arrays;
import model.FrvaModel;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

public class CommandM extends AbstractCommand {
  private StringBuilder stringBuilder;
  private LiveMeasureSequence measureSequence;

  public CommandM(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.M.toString());
    measureSequence = new LiveMeasureSequence();
    stringBuilder = new StringBuilder();

    model.getLiveSequences().add(measureSequence);
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
      measureSequence.setMetadata(string.split(";"));

    } else if (string.contains("WR") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_WR, string, measureSequence);

    } else if (string.contains("WR")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR, string, measureSequence);

    } else if (string.contains("WR2")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR2, string, measureSequence);

    } else if (string.contains("VEG") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_VEG, string, measureSequence);

    } else if (string.contains("VEG")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.VEG, string, measureSequence);

    } else if (string.contains("Voltage =")) {
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

    double[] doubles = Arrays.stream(numbrs).filter(s -> isNumeric(s))
        .mapToDouble(Double::parseDouble)
        .toArray();

    measureSequence.addData(keyName, doubles);
  }


  private boolean isNumeric(String s) {
    return s != null && s.matches("[-+]?\\d*\\.?\\d+");
  }
}
