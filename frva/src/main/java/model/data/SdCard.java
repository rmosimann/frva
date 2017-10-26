package model.data;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SdCard {
  private final List<DataFile> dataFiles;
  private URL sdCardPath;
  private CalibrationFile wavelengthCalibrationFile;
  private CalibrationFile sensorCalibrationFileWr;
  private CalibrationFile sensorCalibrationFileVeg;
  private String name;

  /**
   * Constructor.
   *
   * @param sdCardPath a Path where the data lays as expected.
   */
  public SdCard(URL sdCardPath, String name) {
    this.sdCardPath = sdCardPath;

    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);

    dataFiles = readDatafiles(sdCardPath);


    if (name == null) {
      String[] arr = sdCardPath.getFile().split(File.separator);
      this.name = arr[arr.length - 1];
    } else {
      this.name = name;
    }
  }

  private List<DataFile> readDatafiles(URL sdCardPath) {
    List<DataFile> dataFiles = new LinkedList<>();
    File folder = new File(sdCardPath.getFile());

    File[] listOfDirectories = folder.listFiles(File::isDirectory);

    for (File directory : listOfDirectories) {
      File[] listOfDataFiles = directory.listFiles();
      for (File dataFile : listOfDataFiles) {
        dataFiles.add(new DataFile(this, dataFile));
      }
    }
    return dataFiles;
  }

  private CalibrationFile readCalibrationFile(URL sdCardPath, String filter, int skipLines) {
    File folder = new File(sdCardPath.getFile());
    File[] listOfFiles = folder.listFiles((dir, name) -> name.contains(filter)
        && name.endsWith(".csv"));

    return new CalibrationFile(listOfFiles[0], skipLines);
  }


  public CalibrationFile getWavelengthCalibrationFile() {
    return wavelengthCalibrationFile;
  }

  public CalibrationFile getSensorCalibrationFileWr() {
    return sensorCalibrationFileWr;
  }

  public CalibrationFile getSensorCalibrationFileVeg() {
    return sensorCalibrationFileVeg;
  }

  public List<DataFile> getDataFiles() {
    return dataFiles;
  }

  /**
   * Getter for the devices Serial-Number.
   *
   * @return SerialNumber as String.
   */
  public String getDeviceSerialNr() {
    if (this.dataFiles == null || this.dataFiles.isEmpty()) {
      throw new IllegalArgumentException();
    }
    return this.dataFiles.stream()
        .findAny().get().getMeasureSequences().stream().findAny().get().getSerial();
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

  /**
   * Checks if SDCARD is empty, empty DataFiles are removed before.
   *
   * @return true when empty.
   */
  public boolean isEmpty() {
    if (dataFiles.isEmpty()) {
      return true;
    }
    boolean isEmpty = true;
    for (DataFile dfile : dataFiles) {
      if (!dfile.isEmpty()) {
        isEmpty = false;
      }
    }
    return isEmpty;
  }

  public URL getPath() {
    return this.sdCardPath;
  }
}
