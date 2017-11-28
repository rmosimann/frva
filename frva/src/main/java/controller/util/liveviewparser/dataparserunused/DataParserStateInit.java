package controller.util.liveviewparser.dataparserunused;

import controller.util.liveviewparser.LiveDataParser;

public class DataParserStateInit extends AbstractDataParserState {
  StringBuilder sb = new StringBuilder();
  boolean awaitingGps = false;
  boolean awaitingCmdList = false;
  boolean awaitingConfig = false;
  boolean autoMode = false;
  String[] buffer;

  /**
   * Init state to parse data, reads some information and the transcends to Automode.
   *
   * @param liveDataParser The parserObject.
   */
  public DataParserStateInit(LiveDataParser liveDataParser) {
    super(liveDataParser);
    //    liveDataParser.sendCommand(LiveDataParser.controller.util.liveviewparser.Commands.C.name());
    //    awaitingCmdList = true;
  }


  /**
   * Handles everything that comes in.
   *
   * @param read chars.
   */
  @Override
  public void handleInput(char read) {
    sb.append((char) read);

    if (awaitingCmdList && sb.toString().contains("awaiting commands...")) {
      cmdList();
      awaitingGps = true;
      //      liveDataParser.sendCommand(LiveDataParser.controller.util.liveviewparser.Commands.G.name());
    }
    if (awaitingGps && sb.toString().length() == 70) {
      gps();
      awaitingConfig = true;
      //      liveDataParser.sendCommand(LiveDataParser.controller.util.liveviewparser.Commands.c.name());
    }

    if (awaitingConfig && sb.toString().contains("config.txt written")) {
      config();
      autoMode = true;
      //      liveDataParser.sendCommand("A");

    }
  }

  private void config() {
    awaitingConfig = false;
    buffer = sb.toString().split("\n");
    //System.out.println(buffer.length);
    System.out.println(buffer[5]);
    long integrationTime = parseNumber(buffer[5]);
    liveDataParser.getLiveViewController().getDeviceStatus().setIntegrationTime(integrationTime);
    long intervalTime = parseNumber(buffer[6]);
    liveDataParser.getLiveViewController().getDeviceStatus().setIntervalTime(intervalTime);
  }

  private void gps() {
    awaitingGps = false;
    //System.out.println(sb.toString());
    buffer = sb.toString().split("\n");
    for (String str : buffer) {
      System.out.println(str);
    }
    long gpsTime = parseNumber(buffer[0]);
    liveDataParser.getLiveViewController().getDeviceStatus().setGpsTime(gpsTime);
    long gpsDate = parseNumber(buffer[1]);
    liveDataParser.getLiveViewController().getDeviceStatus().setGpsDate(gpsDate);
    double gpsLat = parseDouble(buffer[2]);
    liveDataParser.getLiveViewController().getDeviceStatus().setLat(gpsLat);
    double gpsLon = parseDouble(buffer[3]);
    liveDataParser.getLiveViewController().getDeviceStatus().setLongitude(gpsLon);
    awaitingGps = false;

  }

  private void cmdList() {
    awaitingCmdList = false;
    sb = new StringBuilder();

  }

  private double parseDouble(String str) {
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      if (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {
        number.append(str.charAt(i));
      }
    }
    return Double.parseDouble(number.toString());
  }

  private long parseNumber(String str) {
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      if (Character.isDigit(str.charAt(i))) {
        number.append(str.charAt(i));
      }
    }
    System.out.println(number.toString());
    return number.length() > 0 ? Long.parseLong(number.toString()) : 0L;
  }
}
