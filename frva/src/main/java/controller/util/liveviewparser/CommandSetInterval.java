package controller.util.liveviewparser;

public class CommandSetInterval extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int intervalTime;


  public CommandSetInterval(LiveDataParser liveDataParser, int intervalTime) {
    super(liveDataParser);
    this.intervalTime = intervalTime;
  }


  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.i.toString() + " "
        + String.valueOf(intervalTime));
  }


  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder);
      stringBuilder = new StringBuilder();
    }
  }


  private void handleLine(StringBuilder stringBuilder) {
    if (stringBuilder.toString().contains("Interval[s] =")) {
      liveDataParser.getDeviceStatus().setIntervalTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("config.txt written")) {
      liveDataParser.runNextCommand();
    }
  }
}
