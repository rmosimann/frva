package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandI extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int integrationTime;


  public CommandI(LiveDataParser liveDataParser, FrvaModel model, int integrationTime) {
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
      liveDataParser.getDeviceStatus().setIntegrationTime(parseNumber(stringBuilder.toString()));
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
