package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class CommandInitialize extends AbstractCommand {
  StringBuilder sb = new StringBuilder();

  public CommandInitialize(LiveDataParser liveDataParser, FrvaModel model) {
    super(liveDataParser, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.B.toString());

    liveDataParser.addCommandToQueue(new CommandGetGpsinfo(liveDataParser, model));

    liveDataParser.addCommandToQueue(new CommandGetCalibration(liveDataParser, model));

  }

  @Override
  public void receive(char read) {
    sb.append((char) read);

    if (sb.toString().contains("App?")) {

      liveDataParser.executeCommand("100");
      if (sb.toString().contains("; ;")) {
        liveDataParser.addCommandToQueue(new CommandAutoMode(liveDataParser, model));
      } else {

        liveDataParser.addCommandToQueue(new CommandIdle(liveDataParser, model));
      }
      liveDataParser.runNextCommand();
    }


  }
}
