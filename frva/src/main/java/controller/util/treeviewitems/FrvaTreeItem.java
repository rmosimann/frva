package controller.util.treeviewitems;

import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;
import model.data.SdCard;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public abstract class FrvaTreeItem extends CheckBoxTreeItem {

  private final Logger logger = Logger.getLogger("FRVA");
  private int containingMeasureSequences;
  private String name;

  /**
   * Constructor.
   * @param name the name of this Item.
   */
  public FrvaTreeItem(String name) {
    containingMeasureSequences = 0;
    this.name = name;
    setValue(name);
  }

  /**
   * Raises the counter of containing MeasureSequences.
   *
   * @param containingMeasureSequences delts of MS.
   */
  public void addMeasureSequences(int containingMeasureSequences) {
    this.containingMeasureSequences += containingMeasureSequences;
    setValue(name + " (" + this.containingMeasureSequences + ")");
    //TODO: wrong value
  }

  public abstract String serialize();

  public abstract int getDepth();

  public abstract void createChildren(List<SdCard> list);
}
