package controller.util.treeviewitems;

import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public abstract class FrvaTreeItem extends CheckBoxTreeItem {

  private final Logger logger = Logger.getLogger("FRVA");
  protected int containingMeasureSequences;
  protected String name;
  protected int pseudoCounter;

  /**
   * Constructor.
   *
   * @param name the name of this Item.
   */
  public FrvaTreeItem(String name) {
    containingMeasureSequences = 0;
    pseudoCounter = 0;
    this.name = name;
    setValue(name);
  }

  /**
   * Raises the counter of containing MeasureSequences.
   */
  public void addMeasureSequence() {
    if (getParent() != null) {
      ((FrvaTreeItem) getParent()).addMeasureSequence();
    }
    this.containingMeasureSequences++;
    setValue(name + " (" + this.containingMeasureSequences + ")");
  }

  /**
   * Adjusts counter to minus 1.
   */
  public void removeMeasureSequence() {
    if (getParent() != null) {
      ((FrvaTreeItem) getParent()).removeMeasureSequence();
      this.containingMeasureSequences--;
      if (containingMeasureSequences < 1) {
        getParent().getChildren().remove(this);
      }
    }

    setValue(name + " (" + this.containingMeasureSequences + ")");
  }

  /**
   * Initially called to have the no of Measurements when lazyLoaded.
   * @param amount of measurements.
   */
  public void setPseudoCounter(int amount) {
    if (getParent() != null) {
      ((FrvaTreeItem) getParent()).setPseudoCounter(amount);
      this.pseudoCounter += amount;
      setValue(name + " (" + pseudoCounter + ")");
    }
  }
}
