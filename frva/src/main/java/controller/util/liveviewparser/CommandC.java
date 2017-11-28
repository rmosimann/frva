package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandC extends AbstractCommand {

  public CommandC(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.sendCommand(Commands.C.toString());
  }

  @Override
  public void receive(char read) {

  }
}
