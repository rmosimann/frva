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
import java.util.stream.Collectors;
import model.data.SdCard;

/**
 * The FrvaTreeRootItem represents the root item in the TreeView.
 */
public class FrvaTreeRootItem extends FrvaTreeItem {

  public FrvaTreeRootItem(String name) {
    super(name);
  }

  /**
   * Creates children as part of the LazyLoading procedure.
   *
   * @param list of which the treeview should be created from
   * @param createFull true when the full tree has to be created.
   */
  public void createChildren(List<SdCard> list, boolean createFull) {

    List<String> deviceNames = list.stream().map(f -> (f.getDeviceSerialNr()))
        .collect(Collectors.toList());
    boolean containesItemAlready = false;
    for (String deviceName : deviceNames) {
      FrvaTreeDeviceItem deviceItem = new FrvaTreeDeviceItem(deviceName, deviceName);
      Iterator it = this.getChildren().iterator();
      while (it.hasNext()) {
        Object currentElement = it.next();
        if (deviceItem.equals(currentElement)) {
          deviceItem = (FrvaTreeDeviceItem) currentElement;
          containesItemAlready = true;
        }
      }
      if (!containesItemAlready) {
        this.getChildren().add(deviceItem);
      }
      deviceItem.createChildren(list, createFull);
    }
  }

}