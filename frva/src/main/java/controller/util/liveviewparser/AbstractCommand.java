package controller.util.liveviewparser;

import java.util.logging.Logger;

/**
 * Created by patrick.wigger on 28.11.17.
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

