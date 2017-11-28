package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandIdle extends AbstractCommand {

  public CommandIdle(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {

  }

  @Override
  public void receive(char read) {

  }

  @Override
  public void onQueueUpdate() {
    liveDataParser.runNextCommand();
  }
}
