package controller.util.liveviewparser;

import java.time.LocalDateTime;
import model.FrvaModel;

public class CommandT extends AbstractCommand {
  private final LocalDateTime dateTime;
  StringBuilder stringBuilder = new StringBuilder();

  public CommandT(LiveDataParser liveDataParser, FrvaModel model, LocalDateTime dateTime) {
    super(liveDataParser, model);
    this.dateTime = dateTime;
  }

  @Override
  public void sendCommand() {
    liveDataParser.executeCommand(Commands.T.toString());
  }

  @Override
  public void receive(char read) {
    stringBuilder.append((char) read);

    if (stringBuilder.toString().contains("say Year")) {
      String yeartouse;
      if (dateTime == null) {
        yeartouse = String.valueOf(LocalDateTime.now().getYear());
      } else {
        yeartouse = String.valueOf(dateTime.getYear());
      }
      liveDataParser.executeCommand(yeartouse);
      stringBuilder = new StringBuilder();
    }
  }
}
