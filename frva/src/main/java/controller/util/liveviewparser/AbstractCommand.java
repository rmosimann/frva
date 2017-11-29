package controller.util.liveviewparser;

import java.util.logging.Logger;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public abstract class AbstractCommand implements CommandInterface {
  private static final Logger logger = Logger.getLogger("FRVA");

  protected LiveDataParser liveDataParser;
  protected FrvaModel model;

  protected AbstractCommand(LiveDataParser liveDataParser, FrvaModel model) {
    this.liveDataParser = liveDataParser;
    this.model = model;
  }

  public void onQueueUpdate() {
    logger.fine("Nothing to do");
  }
}

