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

import java.util.logging.Logger;

/**
 * The AbstractCommand implements the basic methods used by all commands.
 */
public abstract class AbstractCommand implements CommandInterface {
  static final Logger logger = Logger.getLogger("FRVA");

  protected LiveDataParser liveDataParser;

  protected AbstractCommand(LiveDataParser liveDataParser) {
    this.liveDataParser = liveDataParser;
  }

  public void onQueueUpdate() {
    logger.fine("Nothing to do");
  }

  /**
   * Returns the Number contained in a String.
   *
   * @param string the string containing the number.
   * @return the extracted number.
   */
  long parseNumber(String string) {
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      if (Character.isDigit(string.charAt(i))) {
        number.append(string.charAt(i));
      }
    }
    return number.length() > 0 ? Long.parseLong(number.toString()) : 0L;
  }


  /**
   * Parses On or Off in a String.
   *
   * @param string the string containing On or Off.
   * @return true when on, false when off or nothing.
   */
  boolean parseOnOff(String string) {
    if (string.toLowerCase().contains("on")) {
      return true;
    }
    return false;
  }


  boolean isStringNumeric(String s) {
    return s != null && s.matches("[-+]?\\d*\\.?\\d+");
  }

}

