package controller.util.liveviewparser;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandIdle extends AbstractCommand {

  public CommandIdle(LiveDataParser liveDataParser) {
    super(liveDataParser);
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
