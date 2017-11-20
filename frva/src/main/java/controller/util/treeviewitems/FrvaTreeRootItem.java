package controller.util.treeviewitems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.data.SdCard;

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


  public void createChildren(List<SdCard> list) {

    List<String> deviceNames= list.stream().map(f -> (f.getDeviceSerialNr())).collect(Collectors.toList());
    boolean containesItemAlready=false;
    for (String deviceName : deviceNames) {
      FrvaTreeItem deviceItem = new FrvaTreeDeviceItem(deviceName, deviceName);
      Iterator it = this.getChildren().iterator();
      while (it.hasNext()) {
        Object currentElement = it.next();
        if (deviceItem.equals(currentElement)) {
          deviceItem = (FrvaTreeDeviceItem) currentElement;
          containesItemAlready=true;

        }
      }
      if(!containesItemAlready){this.getChildren().add(deviceItem);}
      deviceItem.createChildren(list);
    }
  }
}