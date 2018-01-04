package controller.util.liveviewparser;

public class CommandAny extends AbstractCommand {

  private final String command;

  public CommandAny(LiveDataParser liveDataParser, String command) {
    super(liveDataParser);
    this.command = command;
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(command);
    liveDataParser.runNextCommand();
  }

  @Override
  public void receive(char read) {

  }
}
