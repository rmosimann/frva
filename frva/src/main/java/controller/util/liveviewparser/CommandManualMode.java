package controller.util.liveviewparser;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandManualMode extends AbstractCommand {

  private StringBuilder stringBuilder = new StringBuilder();
  private boolean commandSent = false;

  public CommandManualMode(LiveDataParser liveDataParser) {
    super(liveDataParser);
  }

  @Override
  public void sendCommand() {
    if (!commandSent) {
      liveDataParser.executeCommand(Commands.C.toString());
      commandSent = true;
    }
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
