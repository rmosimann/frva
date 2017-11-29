package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandManualMode extends AbstractCommand {

  private StringBuilder stringBuilder = new StringBuilder();

  public CommandManualMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.C.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder);
      stringBuilder = new StringBuilder();
    }
  }

  private void handleLine(StringBuilder stringBuilder) {
    if (stringBuilder.toString().contains("awaiting commands...")) {

      liveDataParser.getCommandQueue()
          .removeIf(commandInterface -> {
            return commandInterface instanceof CommandAutoMode;
          });
      liveDataParser.runNextCommand();
    }
  }
}
