package controller.util.TreeviewItems;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public abstract class FrvaTreeItem extends CheckBoxTreeItem {
  private final Logger logger = Logger.getLogger("FRVA");

  public int getContainingMeasureSequences() {
    return containingMeasureSequences;
  }

  public void setContainingMeasureSequences(int containingMeasureSequences) {
    this.containingMeasureSequences = containingMeasureSequences;
    setValue(getValue().toString() + " (" + containingMeasureSequences + ")");
  }

  private int containingMeasureSequences;


  public FrvaTreeItem(String name) {
    setValue(name);
  }

  public abstract String serialize();

  public abstract int getDepth();

  public static FrvaTreeItem createTreeItem(String[] array, FrvaModel model) {

    String depth = array[1];
    switch (depth) {
      case "0":
        //  System.out.println("created new Root item");
        return new FrvaTreeRootItem(array[2]);
      case "1":
        // System.out.println("created new Device item");
        return new FrvaTreeDeviceItem(array[2], array[3]);
      case "2":
        // System.out.println("created new Sd item " + array[3]);
        // System.out.println("hello sfsfsknö230943984u "+array[2]+ " " + array[3]);
        return new FrvaTreeSdCardItem(array[2], new File(array[3]), model);
      case "3":
        //System.out.println("created new Year item");
        return new FrvaTreeYearItem(array[2]);
      case "4":
        //System.out.println("created new Month item");
        return new FrvaTreeMonthItem(array[2]);
      case "5":
        //System.out.println("created new Day item");
        return new FrvaTreeDayItem(array[2]);
      case "6":
        //System.out.println("created new Hour item");

        return new FrvaTreeHourItem(array[2], array[3]);
      case "7":
        //System.out.println("created new Measurement item");
        return new FrvaTreeMeasurementItem(array[2], new File(array[3]), array[0], model);
      default:
        throw new NoSuchElementException("depth " + depth + "is unknown");
    }
  }

  public void setPathToLibrary() {
  }

  ;
}
