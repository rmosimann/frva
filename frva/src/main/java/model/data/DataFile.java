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

package model.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataFile represents the file on the SDCard where the MeasurementSequences are saved.
 */
public class DataFile {

  private File originalFile;
  private final SdCard sdCard;
  private List<MeasureSequence> measureSequences = new ArrayList<>();
  private MeasureSequence lastAddedMeasurement;
  private int measurementLength = 5;

  /**
   * Creates a Datafile by reading in the db.csv.
   *
   * @param sdCard    SDCARD containing the Datafile.
   * @param filename  the file.
   * @param metadatas content of db.csv corresponding to this Data-file.
   */
  public DataFile(SdCard sdCard, File filename, List<String[]> metadatas) {
    this.sdCard = sdCard;
    this.originalFile = filename;
    for (String[] metadata : metadatas) {
      measureSequences.add(new MeasureSequence(metadata, this));
    }
  }

  /**
   * Constructor for reading in metadata (in case db.csv does not exist).
   *
   * @param filename Name of the file
   * @param sdCard   The SDCARD the datafile belongs to
   */
  public DataFile(SdCard sdCard, File filename) {
    this.sdCard = sdCard;
    this.originalFile = filename;
    measureSequences = FileInOut.readInMetadataOfMeasureSequences(this);
  }

  public List<MeasureSequence> getMeasureSequences() {
    return measureSequences;
  }

  public String getOriginalFileName() {
    return originalFile.getName();
  }

  public String getFolderName() {
    return originalFile.getParentFile().getName();
  }

  public String getDataFileName() {
    return originalFile.getName();
  }

  public SdCard getSdCard() {
    return sdCard;
  }

  /**
   * returns true if Datafile has no measuresequences (all deleted).
   * @return a boolean.
   */
  public boolean isEmpty() {
    if (measureSequences.isEmpty()) {
      this.sdCard.deleteFile(originalFile);
      return true;
    }
    return false;
  }

  public File getOriginalFile() {
    return this.originalFile;
  }

  public MeasureSequence getLastAddedMeasurement() {
    return lastAddedMeasurement;
  }

  public int getMeasurementLength() {
    return measurementLength;
  }
}
