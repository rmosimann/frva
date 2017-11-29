package controller.util.liveviewparser;

import model.FrvaModel;

public class Commandix extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int intervalTime;


  public Commandix(LiveDataParser liveDataParser, FrvaModel model, int intervalTime) {
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
}
