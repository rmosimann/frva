package controller.util.treeviewitems;

import java.util.List;
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
  public void createChildren(List<SdCard> list) {
  }

  public String getMonth() {
    return this.month;
  }
}

