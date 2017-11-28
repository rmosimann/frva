package controller.util.liveviewparser.dataparserunused;

import controller.util.liveviewparser.LiveDataParser;

public abstract class AbstractDataParserState implements DataParserState {
  protected final LiveDataParser liveDataParser;

  public AbstractDataParserState(LiveDataParser liveDataParser) {
    this.liveDataParser = liveDataParser;
  }

  public void handleInput(char read) {
    System.out.println("stupid");
  }

  public void onQueueUpdate() {
    System.out.println("Nothing to do here");
  }
}
