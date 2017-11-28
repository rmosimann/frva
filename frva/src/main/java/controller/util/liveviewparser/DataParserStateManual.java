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

    liveDataParser.setCurrentCommand(new DataParserStateAutomode(liveDataParser));
    liveDataParser.setCurrentCommand(new DataParserStateDebug(liveDataParser));
  }

  @Override
  public void handleInput(char read) {
    System.out.print((char) read);
  }
}
