package controller.util.liveviewparser;

public abstract class AbstractDataParserState implements DataParserState {
  protected final LiveDataParser liveDataParser;

  public AbstractDataParserState(LiveDataParser liveDataParser) {
    this.liveDataParser = liveDataParser;
  }

  public void handleInput(char read) {
    System.out.println("stupid");
  }

  public void goToDebugMode() {

  }

  public void goToManualMode() {

  }

  public void goToAutoMode() {

  }
}
