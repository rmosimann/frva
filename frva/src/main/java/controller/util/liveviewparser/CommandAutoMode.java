package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandAutoMode extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int lineInCycle;
  private boolean nextCommandSent = false;

  public CommandAutoMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.A.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains(System.lineSeparator())) {
      handleLine(stringBuilder);
      stringBuilder = new StringBuilder();
    }
  }


  private void handleLine(StringBuilder line) {
    lineInCycle++;

    if (liveDataParser.getCommandQueue().size() > 0 && !nextCommandSent) {
      liveDataParser.getCommandQueue().peek().sendCommand();
      nextCommandSent = true;
    }


    if (line.toString().contains("Start FLAME Cycle")) {
      //Create MeasureSequence
      System.out.println("yey");
      lineInCycle = 1;
    }


    if (lineInCycle == 19 && nextCommandSent) {
      //do something with the line
      if (nextCommandSent) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser, model));
        liveDataParser.setCurrentCommand(liveDataParser.getCommandQueue().pollFirst());
      }
    }


  }
}
