package controller.util.TreeviewItems;

import java.io.File;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeSdCardItem extends FrvaTreeItem {
  private File sdCard;


  public FrvaTreeSdCardItem(String name, File sdCard) {
    super(name);
    this.sdCard = sdCard;
  }


  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + sdCard.getAbsolutePath() + ";";

  }

  @Override
  public int getDepth() {
    return 2;
  }
}
