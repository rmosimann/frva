package controller.util.treeviewitems;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeDayItem extends FrvaTreeItem {

  private String day;

  public FrvaTreeDayItem(String day) {
    super(day);
    this.day = day;
  }




  public String getDay() {
    return day;
  }
}
