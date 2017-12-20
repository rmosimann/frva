package controller.util.treeviewitems;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeMonthItem extends FrvaTreeItem {

  public FrvaTreeMonthItem(String month) {
    super(month);
    this.month = month;
  }

  private String month;


  public String getMonth() {
    return this.month;
  }
}

