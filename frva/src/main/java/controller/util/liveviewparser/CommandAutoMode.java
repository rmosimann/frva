package controller.util.liveviewparser;

import java.util.Arrays;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandAutoMode extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private LiveMeasureSequence currentMeasureSequence;


  public CommandAutoMode(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.A.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder.toString());
      stringBuilder = new StringBuilder();
    }
  }


  private void handleLine(String line) {

    if (line.contains("Start FLAME Cycle")) {
      currentMeasureSequence = liveDataParser.createLiveMeasurementSequence();

    } else if (line.contains("auto_mode")) {
      currentMeasureSequence.setMetadata(line.replace(" ", "").split(";"));
      liveDataParser.updateIntegrationTime(currentMeasureSequence);

    } else if (line.contains("VEGIT")) {
      logger.fine("nothing to do here");

    } else if (line.contains("WRIT")) {
      logger.fine("nothing to do here");

    } else if (line.contains("Voltage = ")) {
      currentMeasureSequence.setComplete(true,
          liveDataParser.getDeviceStatus().getCalibrationFile(),
          liveDataParser.getCurrentLiveSdCardPath());

      if (liveDataParser.getCommandQueue().size() > 0) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser));
        liveDataParser.runNextCommand();
      }
    } else if (line.contains("WR") && line.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_WR, line, currentMeasureSequence);

    } else if (line.contains("WR2")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR2, line, currentMeasureSequence);

    } else if (line.contains("WR")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.WR, line, currentMeasureSequence);

    } else if (line.contains("VEG") && line.contains("DC")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.DC_VEG, line, currentMeasureSequence);

    } else if (line.contains("VEG")) {
      addValuesToMs(MeasureSequence.SequenceKeyName.VEG, line, currentMeasureSequence);
    }

  }

  void addValuesToMs(MeasureSequence.SequenceKeyName keyName, String string,
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

  @Override
  public void onQueueUpdate() {
    liveDataParser.getCommandQueue().peek().sendCommand();
  }
}

