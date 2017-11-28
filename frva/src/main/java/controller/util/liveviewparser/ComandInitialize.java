package controller.util.liveviewparser;

import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public class ComandInitialize extends AbstractCommand {
  StringBuilder sb = new StringBuilder();

  public ComandInitialize(LiveDataParser ldP, FrvaModel model) {
    super(ldP, model);
  }

  @Override
  public void sendCommand() {
    liveDataParser.sendCommand(LiveDataParser.Commands.C.name());
  }

  @Override
  public void receive(char read) {
    sb.append((char) read);

    if (sb.toString().contains("awaiting commands...")) {
      //TODO liveDataParser.addToQueue(new CommandManualMode());
    }
    if(sb.toString().contains("; ;")){
      //TODO  liveDataParser.addToQueue(new CommandAutoMode());
    }


  }
}
