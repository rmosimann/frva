package controller.util.treeviewitems;

import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public abstract class FrvaTreeItem extends CheckBoxTreeItem {

  private final Logger logger = Logger.getLogger("FRVA");
  private int containingMeasureSequences;
  private String name;

  /**
   * Constructor.
   *
   * @param name the name of this Item.
   */
  public FrvaTreeItem(String name) {
    containingMeasureSequences = 0;
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
   * Checks if contains children.
   *
   * @return a boolean with the anwser
   */
  public boolean checkIfEmpty() {
    return this.getChildren().isEmpty();
  }
}
