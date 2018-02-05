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

/**
 * The CommandGetGpsinfo retrieves the current GPS information from the device.
 */
public class CommandGetGpsinfo extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  public CommandGetGpsinfo(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.G.toString());
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
    if (stringBuilder.toString().contains("GPS Time = ")) {
      liveDataParser.getDeviceStatus().setGpsTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("GPS Date = ")) {
      liveDataParser.getDeviceStatus().setGpsDate(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Lat = ")) {
      liveDataParser.getDeviceStatus().setLat(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Lon = ")) {
      liveDataParser.getDeviceStatus().setLongitude(parseNumber(stringBuilder.toString()));
      liveDataParser.runNextCommand();
    }
  }
}
