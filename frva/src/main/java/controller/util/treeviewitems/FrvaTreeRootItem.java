package controller.util.treeviewitems;

/**
 * Created by patrick.wigger on 12.10.17.
 */
public class FrvaTreeRootItem extends FrvaTreeItem {

  public FrvaTreeRootItem(String name) {
    super(name);
  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";" + this.getValue().toString() + ";" + "Root" + ";";
  }

  @Override
  public int getDepth() {
    return 0;
  }

  @Override
  public void createChildren() {
  }
}
