package controller.util.liveviewparser;

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
    if (string.contains("WR") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_WR, string, measureSequence);

    } else if (string.contains("WR")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR, string, measureSequence);

    } else if (string.contains("WR2")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR2, string, measureSequence);

    } else if (string.contains("VEG") && string.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_VEG, string, measureSequence);

    } else if (string.contains("VEG")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.VEG, string, measureSequence);

    } else if (string.contains("manual_mode")) {
      measureSequence.setMetadata(string.split(";"));

    } else if (string.contains("Voltage =")) {
      liveDataParser.runNextCommand();
    }
  }

  private void addValuesToMs(MeasureSequence.SequenceKeyName keyName, String string,
                             LiveMeasureSequence measureSequence) {

    String cleaned = string.replace(":", ";").replace(" ", "");
    System.out.println(cleaned);
    String[] splited = cleaned.split(";");

    for (String s : splited) {
      System.out.println(Double.parseDouble(s));

    }

    double[] doubleValues = null;
    measureSequence.addData(keyName, doubleValues);
  }


}
