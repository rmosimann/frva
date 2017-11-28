package controller.util.liveviewparser;

public class DataParserStateInit extends AbstractDataParserState {

  /**
   * Init state to parse data, reads some information and the transcends to Automode.
   *
   * @param liveDataParser The parserObject.
   */
  public DataParserStateInit(LiveDataParser liveDataParser) {
    super(liveDataParser);
    liveDataParser.sendCommand(LiveDataParser.Commands.C.name());
//    liveDataParser.sendCommand(LiveDataParser.Commands.G.name());
//    liveDataParser.sendCommand(LiveDataParser.Commands.c.name());
  }


  /**
   * Handles everything that comes in.
   *
   * @param read chars.
   */
  @Override
  public void handleInput(char read) {
    System.out.print((char) read);

    //    if (finished reading stuff) {
    //      liveDataParser.setState(new DataParserStateAutomode(liveDataParser));
    //    }
  }
}
