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

import java.util.Iterator;
import java.util.List;
import model.data.SdCard;

/**
 * The FrvaTreeDeviceItem represents a device in the TreeView.
 */
public class FrvaTreeDeviceItem extends FrvaTreeItem {


  private String deviceSerialNr;

  public FrvaTreeDeviceItem(String name, String deviceSerialNr) {
    super(name);
    this.deviceSerialNr = deviceSerialNr;
  }

  /**
   * Creates its children depending on list.
   *
   * @param list      of SdCards which should be created
   * @param isPreview true if import scenario (Fully loaded then)
   */
  public void createChildren(List<SdCard> list, boolean isPreview) {
    for (SdCard sdCard : list) {
      FrvaTreeSdCardItem sdCardItem = new FrvaTreeSdCardItem(sdCard, isPreview);
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
        if (isPreview) {
          sdCardItem.createChildren(list);
        } else {
          FrvaTreeYearItem pseudoElement = new FrvaTreeYearItem("pseudo-Element");
          sdCardItem.getChildren().add(pseudoElement);
          pseudoElement.setPseudoCounter(sdCard.getPseudoCounter());
        }
      }

    }
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeDeviceItem
        && this.deviceSerialNr.equals(((FrvaTreeDeviceItem) o).deviceSerialNr);
  }

  public String getDeviceSerialNr() {
    return deviceSerialNr;
  }


  /**
   * Raises the counter of containing MeasureSequences.
   */
  @Override
  public void addMeasureSequence() {
    if (getParent() != null) {
      ((FrvaTreeItem) getParent()).addMeasureSequence();
    }
    this.containingMeasureSequences++;

    setValue(name + " (" + containingMeasureSequences + "/" + pseudoCounter + ")");
  }

  /**
   * Adjusts counter to minus 1.
   */
  @Override
  public void removeMeasureSequence() {
    if (getParent() != null) {
      ((FrvaTreeItem) getParent()).removeMeasureSequence();
      this.containingMeasureSequences--;
      if (this.getChildren().isEmpty()) {
        getParent().getChildren().remove(this);
      }
    }

    setValue(name + " (" + (pseudoCounter - 1) + "/" + pseudoCounter + ")");
  }
}