package controller.util.liveviewparser;

import controller.LiveViewController;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 28.11.17.
 */
public abstract class AbstractCommand implements CommandInterface {
  protected LiveDataParser liveDataParser;
  public FrvaModel model;

  public AbstractCommand(LiveDataParser ldP, FrvaModel model) {
    this.liveDataParser = ldP;
    this.model = model;
  }

}

