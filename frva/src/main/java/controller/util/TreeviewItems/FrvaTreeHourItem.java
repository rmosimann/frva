package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeHourItem extends FrvaTreeItem {

  private String hour;

  public FrvaTreeHourItem(String name, String hour) {
    super(name);
    this.hour = hour;
  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + hour + ";";
  }

  @Override
  public int getDepth() {
    return 6;
  }
}
