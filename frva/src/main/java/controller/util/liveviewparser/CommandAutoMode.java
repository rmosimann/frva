package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandAutoMode extends AbstractCommand {

  public CommandAutoMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.sendCommand(Commands.A.toString());
  }

  @Override
  public void receive(char read) {

  }
}
