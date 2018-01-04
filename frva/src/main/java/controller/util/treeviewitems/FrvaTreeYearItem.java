package controller.util.treeviewitems;

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
  public boolean equals(Object o) {
    return o instanceof FrvaTreeYearItem && ((FrvaTreeYearItem) o).year.equals(this.year);
  }
}