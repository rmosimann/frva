package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeDayItem extends FrvaTreeItem{


  private String day;

  public FrvaTreeDayItem(String day) {
    super(day);
    this.day = day;
  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + day + ";";

  }

  @Override
  public int getDepth() {
    return 5;
  }

  public String getDay() {
    return day;
  }
}
