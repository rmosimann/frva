package controller.util.liveviewparser;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandInitialize extends AbstractCommand {
  StringBuilder sb = new StringBuilder();

  public CommandInitialize(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.B.toString());

    liveDataParser.addCommandToQueue(new CommandGetGpsinfo(liveDataParser));
    liveDataParser.addCommandToQueue(new CommandGetConfiguration(liveDataParser));
    liveDataParser.addCommandToQueue(new CommandGetCalibration(liveDataParser));

  }

  @Override
  public void receive(char read) {
    sb.append((char) read);

    if (sb.toString().contains("App?")) {

      liveDataParser.executeCommand("100");
      if (sb.toString().contains("; ;")) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser));
      } else {

        liveDataParser.addCommandToQueue(new CommandIdle(liveDataParser));
      }
      liveDataParser.runNextCommand();
    }


  }
}
