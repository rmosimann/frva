package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeHourItem extends FrvaTreeItem {

  private String hour;

  public FrvaTreeHourItem(String hour) {
    super(hour);
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

  public String getHour() {
    return hour;
  }
}
