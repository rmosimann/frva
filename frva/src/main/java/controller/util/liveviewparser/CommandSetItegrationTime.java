package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandSetItegrationTime extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int integrationTime;


  public CommandSetItegrationTime(LiveDataParser liveDataParser,
                                  FrvaModel model, int integrationTime) {
    super(liveDataParser, model);
    this.integrationTime = integrationTime;
  }


  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.I.toString() + " " + String.valueOf(integrationTime));
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
    if (stringBuilder.toString().contains("IT = ")) {
      liveDataParser.getDeviceStatus().setIntegrationTimeConfigured(
          parseNumber(stringBuilder.toString()));
      liveDataParser.runNextCommand();
    }
  }
}
