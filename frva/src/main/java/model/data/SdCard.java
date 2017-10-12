package model.data;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class SdCard {
  private final List<DataFile> dataFiles;
  private URL sdCardPath;
  private CalibrationFile wavelengthCalibrationFile;
  private CalibrationFile sensorCalibrationFileWr;
  private CalibrationFile sensorCalibrationFileVeg;

  /**
   * Constructor.
   *
   * @param sdCardPath a Path where the data lays as expected.
   */
  public SdCard(URL sdCardPath) {
    this.sdCardPath = sdCardPath;

    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);

    dataFiles = readDatafiles(sdCardPath);
  }

  private List<DataFile> readDatafiles(URL sdCardPath) {
    List<DataFile> dataFiles = new LinkedList<>();
    File folder = new File(sdCardPath.getFile());

    File[] listOfDirectories = folder.listFiles(File::isDirectory);

    for (File directory : listOfDirectories) {
      System.out.println("dire:" + directory.getAbsolutePath());
      File[] listOfDataFiles = directory.listFiles();
      for (File dataFile : listOfDataFiles) {
        System.out.println("fiile:" + dataFile.getAbsolutePath());
        dataFiles.add(new DataFile(dataFile, this));
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
}
