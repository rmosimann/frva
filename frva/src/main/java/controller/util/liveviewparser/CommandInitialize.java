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
 * The CommandInitialize runs the following commands:
 *  - CommandGetGpsinfo
 *  - CommandGetConfiguration
 *  - CommandGetCalibration
 * and switches the to the state the device is detected in (manual/auto).
 */
public class CommandInitialize extends AbstractCommand {
  StringBuilder sb = new StringBuilder();

  public CommandInitialize(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.B.toString());

    liveDataParser.addCommandToQueue(new CommandGetGpsinfo(liveDataParser));
    liveDataParser.addCommandToQueue(new CommandGetConfiguration(liveDataParser));
    liveDataParser.addCommandToQueue(new CommandGetCalibration(liveDataParser));

  }

  @Override
  public void receive(char read) {
    sb.append((char) read);

    if (sb.toString().contains("App?")) {

      liveDataParser.executeCommand("100");
      if (sb.toString().contains("; ;")) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser));
      } else {

        liveDataParser.addCommandToQueue(new CommandIdle(liveDataParser));
      }
      liveDataParser.runNextCommand();
    }


  }
}
