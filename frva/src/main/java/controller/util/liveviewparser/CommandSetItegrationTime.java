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
 * The CommandSetIntegrationTime sends the command to set the integrationtime to the device.
 */
public class CommandSetItegrationTime extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int integrationTime;


  public CommandSetItegrationTime(LiveDataParser liveDataParser, int integrationTime) {
    super(liveDataParser);
    this.integrationTime = integrationTime;
  }


  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.I.toString() + " " + String.valueOf(integrationTime));
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
    if (stringBuilder.toString().contains("IT = ")) {
      liveDataParser.getDeviceStatus().setIntegrationTimeConfigured(
          parseNumber(stringBuilder.toString()));
      liveDataParser.runNextCommand();
    }
  }
}
