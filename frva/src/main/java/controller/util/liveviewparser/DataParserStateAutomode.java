package controller.util.liveviewparser;

public class DataParserStateAutomode extends AbstractDataParserState {
  private boolean goToDebugMode;
  private boolean goToManualMode;

  public DataParserStateAutomode(LiveDataParser liveDataParser) {
    super(liveDataParser);
    liveDataParser.sendCommand(LiveDataParser.Commands.A.name());
  }

  @Override
  public void handleInput(char read) {
    System.out.print((char) read);


    if (/*Measurement ist fertig &&*/ true) {
      if (liveDataParser.commandQueue.size() > 0) {
        liveDataParser.sendCommand(LiveDataParser.Commands.C.name());
        liveDataParser.setState(new DataParserStateManual(liveDataParser));
      } else if (liveDataParser.commandQueue.size() > 0) {


      }
    }
  }

  @Override
  public void goToDebugMode() {

    goToDebugMode = true;
  }

  @Override
  public void goToManualMode() {
    goToManualMode = true;
  }
}
