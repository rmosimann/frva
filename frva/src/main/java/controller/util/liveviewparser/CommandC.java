package controller.util.liveviewparser;

import controller.LiveViewController;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandC extends AbstractCommand {

  public CommandC(LiveDataParser ldP, FrvaModel model) {
    super(ldP, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.sendCommand(LiveDataParser.Commands.C.name());
  }

  @Override
  public void receive() {

  }
}
