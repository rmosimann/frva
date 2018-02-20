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
 * The FrvaTreeYearItem represents a year in the TreeView.
 */
public class FrvaTreeYearItem extends FrvaTreeItem {
  public String getYear() {
    return year;
  }

  private String year;

  public FrvaTreeYearItem(String year) {
    super(year);
    this.year = year;
  }


  @Override
  public boolean equals(Object o) {
    return o instanceof FrvaTreeYearItem && ((FrvaTreeYearItem) o).year.equals(this.year);
  }
}
