package controller.util.liveviewparser.dataparserunused;

import controller.util.liveviewparser.LiveDataParser;

public class DataParserStateAutomode extends AbstractDataParserState {
  private boolean goToDebugMode;
  private boolean goToManualMode;

  public DataParserStateAutomode(LiveDataParser liveDataParser) {
    super(liveDataParser);
    //    liveDataParser.executeCommand(
    // LiveDataParser.controller.util.liveviewparser.Commands.A.name());
  }

  @Override
  public void handleInput(char read) {
    //    System.out.print((char) read);
    //
    //
    //    if (/*Measurement ist fertig &&*/ true) {
    //      if (liveDataParser.commandQueue.size() > 0) {
    //        liveDataParser.executeCommand(
    // LiveDataParser.controller.util.liveviewparser.Commands.C.name());
    ////        liveDataParser.setCurrentCommand(new DataParserStateManual(liveDataParser));
    //      } else if (liveDataParser.commandQueue.size() > 0) {
    //
    //
    //      }
    //    }
  }

}
