package model.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import model.FrvaModel;

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


    File dbFile = new File(sdCardFile + File.separator + "db.csv");

    if (!dbFile.exists()) {
      dataFiles = FileInOut.getDataFiles(this);
      if (isPathInLibrary()) {
        FileInOut.writeDatabaseFile(this);
      }
    } else {
      try {
        dataFiles = FileInOut.readDatafilesLazy(this);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (isPathInLibrary()) {
      pseudoCounter += FileInOut.getLineCount(dbFile);
    }

  }


  public boolean isPathInLibrary() {
    return this.sdCardFile.getPath().contains(FrvaModel.LIBRARYPATH);
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

