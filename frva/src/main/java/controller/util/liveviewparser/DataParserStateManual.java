package controller.util.liveviewparser;

public class DataParserStateManual extends AbstractDataParserState {

  public DataParserStateManual(LiveDataParser liveDataParser) {
    super(liveDataParser);
    runCommands(liveDataParser);
  }

  private void runCommands(LiveDataParser liveDataParser) {
    liveDataParser.commandQueue.forEach(s -> {
      liveDataParser.sendCommand(s);
    });

    liveDataParser.setState(new DataParserStateAutomode(liveDataParser));
    liveDataParser.setState(new DataParserStateDebug(liveDataParser));
  }

  @Override
  public void handleInput(char read) {
    System.out.print((char) read);
  }
}
