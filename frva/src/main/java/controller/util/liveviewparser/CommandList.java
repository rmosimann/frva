package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandList extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();

  public CommandList(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.C.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains("awaiting commands...")) {
      liveDataParser.runNextCommand();
    }
  }
}
