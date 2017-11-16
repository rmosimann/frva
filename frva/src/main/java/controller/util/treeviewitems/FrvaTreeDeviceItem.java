package controller.util.treeviewitems;

import java.util.List;
import model.data.SdCard;

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

  public void createChildren(List<SdCard> list) {
    //create Children: get all SDCards and check for Different devices
    this.getChildren().add(null);
  }

  private String getDeviceId(FrvaTreeRootItem item) {
    return deviceSerialNr;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeDeviceItem
        && this.deviceSerialNr.equals(((FrvaTreeDeviceItem) o).deviceSerialNr);

  }
}