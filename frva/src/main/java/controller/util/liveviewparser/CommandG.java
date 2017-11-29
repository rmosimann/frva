package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandG extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  public CommandG(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.G.toString());
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
    if (stringBuilder.toString().contains("GPS Time = ")) {
      liveDataParser.getDeviceStatus().setGpsTime(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("GPS Date = ")) {
      liveDataParser.getDeviceStatus().setGpsDate(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Lat = ")) {
      liveDataParser.getDeviceStatus().setLat(parseNumber(stringBuilder.toString()));
    }
    if (stringBuilder.toString().contains("Lon = ")) {
      liveDataParser.getDeviceStatus().setLongitude(parseNumber(stringBuilder.toString()));
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
