package controller.util.liveviewparser;

public class DataParserStateAutomode extends AbstractDataParserState {
  public DataParserStateAutomode(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void handleInput(char read) {
    System.out.print((char) read);
  }


}
