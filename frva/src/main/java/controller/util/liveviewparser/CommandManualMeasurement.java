/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller.util.liveviewparser;

import java.util.Arrays;
import model.data.LiveMeasureSequence;
import model.data.MeasureSequence;

/**
 * The CommandManualMeasurement captures and saves a measurement.
 */
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

    if (stringBuilder.toString().contains("\n")) {
      handleLine(stringBuilder.toString());
      stringBuilder = new StringBuilder();
    }
  }

  private void handleLine(String line) {
    if (line.contains("manual_mode")) {
      currentMeasureSequence.setMetadata(line.replace(" ", "").split(";"));
      liveDataParser.updateIntegrationTime(currentMeasureSequence);

    } else if (line.contains("VEGIT")) {
      logger.fine("nothing to do here");

    } else if (line.contains("WRIT")) {
      logger.fine("nothing to do here");

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

    } else if (line.contains("Voltage =")) {
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
