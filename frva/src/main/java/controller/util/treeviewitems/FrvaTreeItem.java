/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller.util.treeviewitems;

import java.util.logging.Logger;
import javafx.scene.control.CheckBoxTreeItem;

/**
 * The FrvaTreeItem  provides teh basic functionality required by all tree items.
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
