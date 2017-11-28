package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandManualMode extends AbstractCommand {

  public CommandManualMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {

  }

  @Override
  public void receive(char read) {

  }
}
