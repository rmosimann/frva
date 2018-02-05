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

/**
 * The FrvaTreeHourItem represents a hour in the TreeView.
 */
public class FrvaTreeHourItem extends FrvaTreeItem {

  private int hour;

  public FrvaTreeHourItem(int hour) {
    super(hour + ":00-" + ((hour + 1) % 24) + ":00 ");
    this.hour = hour;
  }



  public int getHour() {
    return hour;
  }

}
