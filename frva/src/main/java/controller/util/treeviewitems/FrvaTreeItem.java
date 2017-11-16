package controller.util.treeviewitems;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public abstract class FrvaTreeItem extends CheckBoxTreeItem {
  private final Logger logger = Logger.getLogger("FRVA");
  private int containingMeasureSequences;
  private String name;


  public void addMeasureSequences(int containingMeasureSequences) {
    this.containingMeasureSequences += containingMeasureSequences;
    setValue(name + " (" + this.containingMeasureSequences + ")");
    //TODO: wrong value
  }

  public FrvaTreeItem(String name) {
    containingMeasureSequences = 0;
    this.name = name;
    setValue(name);
  }

  public abstract String serialize();

  public abstract int getDepth();

  public static FrvaTreeItem createTreeItem(String[] array, FrvaModel model) {

    String depth = array[1];
    switch (depth) {
      case "0":
        return new FrvaTreeRootItem(array[2]);
      case "1":
        return new FrvaTreeDeviceItem(array[2], array[3]);
      case "2":
        return new FrvaTreeSdCardItem(array[2], new File(array[3]), model);
      case "3":
        return new FrvaTreeYearItem(array[2]);
      case "4":
        return new FrvaTreeMonthItem(array[2]);
      case "5":
        return new FrvaTreeDayItem(array[2]);
      case "6":
        return new FrvaTreeHourItem(array[2], array[3]);
      case "7":
        return new FrvaTreeMeasurementItem(array[2], new File(array[3]), array[0], model, false);
      default:
        throw new NoSuchElementException("depth " + depth + "is unknown");
    }
  }

  public void setPathToLibrary() {
  }

  public abstract void createChildren();
}
