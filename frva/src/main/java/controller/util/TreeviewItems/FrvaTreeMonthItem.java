package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeMonthItem extends FrvaTreeItem {
  public FrvaTreeMonthItem(String name, String month) {
    super(name);
    this.month = month;
  }

  private String month;

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + month + ";";

  }

  @Override
  public int getDepth() {
    return 4;
  }
}

