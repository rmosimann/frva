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

import java.io.File;
import model.data.MeasureSequence;

/**
 * The FrvaTreeMeasurementItem represents a Measurement in the TreeView.
 */
public class FrvaTreeMeasurementItem extends FrvaTreeItem {

  private MeasureSequence measureSequence;
  private String id;
  private File file;

  /**
   * Constructor.
   *
   * @param name      name.
   * @param ms        measurementSequence.
   */
  public FrvaTreeMeasurementItem(String name, MeasureSequence ms) {
    super(name);
    this.measureSequence = ms;
    this.id = ms.getId();
    this.file = ms.getDataFile().getOriginalFile();

  }



  /**
   * Getter for the Measurementsequence.
   *
   * @return the measurementsequence lazyloaded: read in from the file system.
   */
  public MeasureSequence getMeasureSequence() {
    if (this.measureSequence != null) {
      return measureSequence;
    } else {
      return null;
    }
  }


  public String getId() {
    return this.id;
  }

  private String checkFile() {
    return file.getAbsolutePath();
  }


}
