package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandAutoMode extends AbstractCommand {
  StringBuilder stringBuilder = new StringBuilder();
  private int lineInCycle;
  private boolean commandWaiting = false;

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

    if (line.toString().contains("Start FLAME Cycle")) {
      //Create MeasureSequence
      System.out.println("yey");
      lineInCycle = 1;
    }


    if (lineInCycle == 23 && commandWaiting) {
      //do something with the line
      if (commandWaiting) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser, model));
        liveDataParser.setCurrentCommand(liveDataParser.getCommandQueue().pollFirst());
      }
    }


  }

  @Override
  public void onQueueUpdate() {
    if (!commandWaiting) {
      commandWaiting = true;
      liveDataParser.getCommandQueue().peek().sendCommand();
    }
  }
}
