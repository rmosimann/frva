package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeYearItem extends FrvaTreeItem {
  public String getYear() {
    return year;
  }

  private String year;

  public FrvaTreeYearItem(String year) {
    super(year);
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
