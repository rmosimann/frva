package model.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import model.FrvaModel;

public class SdCard {
  private final Logger logger = Logger.getLogger("FRVA");
  private List<DataFile> dataFiles = new ArrayList<>();

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

    this.sdCardFile = sdCardFile;
    if (name == null) {
      String[] arr = sdCardFile.getPath().split(File.separator);
      this.name = arr[arr.length - 1];
    } else {
      this.name = name;
    }

    calibrationFile = FileInOut.readCalibrationFile(this, "cal", 1);

    if (!new File(sdCardFile + File.separator + "db.csv").exists()) {
      dataFiles = FileInOut.getDataFiles(this);
      if (isPathInLibrary()) {
        FileInOut.writeDB(this);
      }
    } else {
      try {
        dataFiles = FileInOut.readDatafilesLazy(this);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
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
      System.out.println("delete " + sdCardFile + " because it is empty");

      return true;
    }
    boolean isEmpty = true;
    for (DataFile dfile : dataFiles) {
      System.out.println("dataFile");
      if (!dfile.isEmpty()) {
        isEmpty = false;
      }
    }
    if (isEmpty) {
      deleteFile(sdCardFile);
      System.out.println("delete " + sdCardFile + " because it is empty");
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


  @Override
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

