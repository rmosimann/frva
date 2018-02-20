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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import model.FrvaModel;

/**
 * The SDCard is the uppermost level of data storage processed in this application.
 * This class represents a real world SDCard, that contains:
 *  - one CalibrationFile (calib.csv)
 *  - one or more DataFiles
 *
 */
public class SdCard {
  private final Logger logger = Logger.getLogger("FRVA");
  private List<DataFile> dataFiles = new ArrayList<>();
  private int pseudoCounter;

  private File sdCardFile;
  private CalibrationFile calibrationFile;
  private String name;

  /**
   * Constructor.
   *
   * @param sdCardFile a Path where the data lays as expected.
   * @param name       the Name of that SDCARD.
   */
  public SdCard(File sdCardFile, String name) {
    pseudoCounter = 0;
    this.sdCardFile = sdCardFile;
    if (name == null) {
      String[] arr = sdCardFile.getPath().split(File.separator);
      this.name = arr[arr.length - 1];
    } else {
      this.name = name;
    }
    calibrationFile = FileInOut.readCalibrationFile(this, "cal.csv");
    try {
      dataFiles = FileInOut.readDatafilesLazy(this);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    if (isPathInLibrary()) {
      File dbFile = new File(sdCardFile + File.separator + "db.csv");
      pseudoCounter += FileInOut.getLineCount(dbFile);
    }
  }

  public boolean isPathInLibrary() {
    return this.sdCardFile.getPath().contains(FrvaModel.LIBRARYPATH);
  }

  /**
   * Checks if SDCARD is empty, empty DataFiles are removed before.
   *
   * @return true when empty.
   */
  public boolean isEmpty() {
    if (dataFiles.isEmpty()) {
      deleteFile(sdCardFile);

      return true;
    }
    boolean isEmpty = true;
    for (DataFile dfile : dataFiles) {
      if (!dfile.isEmpty()) {
        isEmpty = false;
      }
    }
    if (isEmpty) {
      deleteFile(sdCardFile);
    }
    return isEmpty;
  }


  /**
   * Getter for the devices Serial-Number.
   *
   * @return SerialNumber as String.
   */
  public String getDeviceSerialNr() {
    return this.calibrationFile.getMetadata().get(0);
  }


  public String getName() {
    return this.name;
  }

  public int getPseudoCounter() {
    return this.pseudoCounter;
  }


  /**
   * Getter to read all MeasurementSequences in this SDCARD.
   *
   * @return List of MeasurementSequences.
   */
  public List<MeasureSequence> getMeasureSequences() {
    List<MeasureSequence> list = new ArrayList<>();

    for (DataFile dataFile : dataFiles) {
      list.addAll(dataFile.getMeasureSequences());

    }
    return list;
  }


  public int hashCode() {
    return calibrationFile.hashCode();
  }

  public CalibrationFile getCalibrationFile() {
    return calibrationFile;
  }

  public List<DataFile> getDataFiles() {
    return dataFiles;
  }

  public File getSdCardFile() {
    return this.sdCardFile;
  }

  /**
   * Deletes a specific file.
   *
   * @param file The File to delete.
   */
  public void deleteFile(File file) {
    if (file.exists() && file.isDirectory() && file.listFiles().length != 0) {
      for (File f : file.listFiles()) {
        deleteFile(f);
      }
    }
    file.delete();
    logger.info("Deleted File:" + file);
  }


}

