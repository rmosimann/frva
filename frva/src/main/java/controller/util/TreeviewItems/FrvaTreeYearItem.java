package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeYearItem extends FrvaTreeItem {
  private String year;

  public FrvaTreeYearItem(String name, String year) {
    super(name);
    this.year = year;
  }


  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + year + ";";

  }

  @Override
  public int getDepth() {
    return 3;
  }
}
