package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandAutoMode extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int lineInCycle;

  public CommandAutoMode(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.sendCommand(Commands.A.toString());
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
    System.out.print(line.toString());

    if (line.toString().contains("Start FLAME Cycle")) {
      //Create MeasureSequence
      System.out.println("yey");
      lineInCycle = 1;
    }


    if (lineInCycle == 23) {
      //do something with the line
      if (liveDataParser.getCommandQueue().size() > 0) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser, model));
        liveDataParser.runNextCommand();
      }
    }

  }
}
