package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandAny extends AbstractCommand {

  private final String command;

  public CommandAny(LiveDataParser liveDataParser, FrvaModel model, String command) {
    super(liveDataParser, model);
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
