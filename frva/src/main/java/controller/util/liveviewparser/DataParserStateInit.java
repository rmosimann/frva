package controller.util.liveviewparser;

import com.sun.media.jfxmedia.logging.Logger;

public class DataParserStateInit extends AbstractDataParserState {

  /**
   * Init state to parse data, reads some information and the transcends to Automode.
   *
   * @param liveDataParser The parserObject.
   */
  public DataParserStateInit(LiveDataParser liveDataParser) {
    super(liveDataParser);
    liveDataParser.sendCommand('C');
    liveDataParser.sendCommand('G');
   liveDataParser.sendCommand('c');
  }

  /**
   * Handles everything that comes in.
   *
   * @param read chars.
   */
  @Override
  public void handleInput(char read) {
    System.out.print((char) read);
  }
}
