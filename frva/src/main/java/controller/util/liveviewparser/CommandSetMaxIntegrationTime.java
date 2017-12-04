package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandSetMaxIntegrationTime extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int maxIntegrationTime;


  public CommandSetMaxIntegrationTime(LiveDataParser liveDataParser, FrvaModel model, int maxIntegrationTime) {
    super(liveDataParser, model);
    this.maxIntegrationTime = maxIntegrationTime;
  }


  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.IM.toString() + " "
        + String.valueOf(maxIntegrationTime));
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
    if (stringBuilder.toString().contains("Max IT[ms] = ")) {
      liveDataParser.getDeviceStatus().setMaxIntegrationTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("config.txt written")) {
      liveDataParser.runNextCommand();
    }
  }
}
