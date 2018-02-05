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
 * The FrvaTreeMonthItem represents a month in the TreeView.
 */
public class FrvaTreeMonthItem extends FrvaTreeItem {

  public FrvaTreeMonthItem(String month) {
    super(month);
    this.month = month;
  }

  private String month;


  public String getMonth() {
    return this.month;
  }
}

