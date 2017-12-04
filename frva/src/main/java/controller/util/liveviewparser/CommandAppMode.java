package controller.util.liveviewparser;

import model.FrvaModel;

public class CommandAppMode extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();


  public CommandAppMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }


  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.B.toString());
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
    if (stringBuilder.toString().contains("App?")) {
      liveDataParser.executeCommand("100");
    }
    if (stringBuilder.toString().contains("confirmed")) {
      liveDataParser.runNextCommand();
    }
  }
}
