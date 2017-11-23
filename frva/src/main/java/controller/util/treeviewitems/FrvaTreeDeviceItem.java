package controller.util.treeviewitems;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
  public void createChildren(List<SdCard> list) {
    for (SdCard sdCard : list) {
      FrvaTreeItem sdCardItem = new FrvaTreeSdCardItem(sdCard);
      boolean containesItemAlready = false;

      Iterator it = this.getChildren().iterator();
      while (it.hasNext()) {
        Object currentElement = it.next();
        if (((FrvaTreeSdCardItem) currentElement).equals(sdCardItem)) {
          sdCardItem = (FrvaTreeSdCardItem) currentElement;
          containesItemAlready = true;
        }
      }
      if (!containesItemAlready) {
        this.getChildren().add(sdCardItem);
        sdCardItem.getChildren().add(new FrvaTreeYearItem("pseudo-Element"));
      }

    }
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeDeviceItem
        && this.deviceSerialNr.equals(((FrvaTreeDeviceItem) o).deviceSerialNr);

  }
}