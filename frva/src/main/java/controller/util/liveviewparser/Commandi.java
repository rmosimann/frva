package controller.util.liveviewparser;

import model.FrvaModel;

public class Commandi extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int intervalTime;


  public Commandi(LiveDataParser liveDataParser, FrvaModel model, int intervalTime) {
    super(liveDataParser, model);
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
    if (stringBuilder.toString().contains("Interval[s] = ")) {
      liveDataParser.getDeviceStatus().setIntervalTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("config.txt written")) {
      liveDataParser.runNextCommand();
    }
  }


  private long parseNumber(String string) {
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      if (Character.isDigit(string.charAt(i))) {
        number.append(string.charAt(i));
      }
    }
    return number.length() > 0 ? Long.parseLong(number.toString()) : 0L;
  }
}
