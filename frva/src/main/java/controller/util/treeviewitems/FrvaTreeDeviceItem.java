package controller.util.treeviewitems;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaTreeDeviceItem extends FrvaTreeItem {

  private String deviceSerialNr;

  public FrvaTreeDeviceItem(String name, String deviceSerialNr) {
    super(name);
    this.deviceSerialNr = deviceSerialNr;
  }

  @Override
  public String serialize() {
    return "-1" + ";" + this.getDepth() + ";"
        + this.getValue().toString() + ";" + deviceSerialNr + ";";
  }

  @Override
  public int getDepth() {
    return 1;
  }

  @Override
  public void createChildren() {
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeDeviceItem
        && this.deviceSerialNr.equals(((FrvaTreeDeviceItem) o).deviceSerialNr);

  }
}