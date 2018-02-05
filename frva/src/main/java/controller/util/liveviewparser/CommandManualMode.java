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
 * The CommandManualMode switches to the manualMode and remains in idle afterwards.
 */
public class CommandManualMode extends AbstractCommand {

  private StringBuilder stringBuilder = new StringBuilder();
  private boolean commandSent = false;

  public CommandManualMode(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    if (!commandSent) {
      liveDataParser.executeCommand(Commands.C.toString());
      commandSent = true;
    }
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
    if (stringBuilder.toString().contains("awaiting commands...")) {

      liveDataParser.getCommandQueue()
          .removeIf(commandInterface -> {
            return commandInterface instanceof CommandAutoMode;
          });
      liveDataParser.runNextCommand();
    }
  }
}
