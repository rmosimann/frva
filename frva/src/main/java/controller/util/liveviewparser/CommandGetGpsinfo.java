package controller.util.liveviewparser;

public class CommandGetGpsinfo extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  public CommandGetGpsinfo(LiveDataParser liveDataParser) {
    super(liveDataParser);
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
}
