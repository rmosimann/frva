package controller.util.TreeviewItems;

import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeMonthItem extends FrvaTreeItem {
  public FrvaTreeMonthItem(String month) {
    super(month);
    this.month = month;
  }

  private String month;

  @Override
  public String serialize() {
    System.out.println("serialized myself");
    System.out.println(this.month);
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + month + ";";

  }

  @Override
  public int getDepth() {
    return 4;
  }

  public String getMonth(){return this.month; }


}

