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
import java.util.Vector;

/**
 * The CommandGetConfiguration retrieves the config file from the device.
 */
public class CommandGetConfiguration extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private boolean parsing3fldPixels = false;
  private Vector<Number> fldPixels = new Vector<>();

  public CommandGetConfiguration(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.c.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains("\n")) {
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
    if (parsing3fldPixels) {
      fldPixels.add(parseNumber(stringBuilder.toString()));
      if (fldPixels.size() == 5) {
        parsing3fldPixels = false;
        liveDataParser.getDeviceStatus().setFldPixels(Arrays.toString(fldPixels.toArray()));
      }
    }
    if (stringBuilder.toString().contains("3FLD pixels")) {
      parsing3fldPixels = true;
      stringBuilder.delete(0, stringBuilder.length());
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
