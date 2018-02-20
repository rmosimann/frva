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

import java.util.ArrayList;
import java.util.List;
import model.data.CalibrationFile;

/**
 * The CommandGetCalibration retrieves the calibrations from the device.
 */
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
