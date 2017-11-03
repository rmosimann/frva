package model.data;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SdCard {
  private final List<MeasureSequence> measureSequences;
  private File sdCardPath;
  private CalibrationFile wavelengthCalibrationFile;
  private CalibrationFile sensorCalibrationFileWr;
  private CalibrationFile sensorCalibrationFileVeg;
  private String name;

  /**
   * Constructor.
   *
   * @param sdCardPath a Path where the data lays as expected.
   */
  public SdCard(File sdCardPath, String name) {
    this.sdCardPath = sdCardPath;
    this.measureSequences=new ArrayList<>();
    wavelengthCalibrationFile = readCalibrationFile(sdCardPath, "wl_", 1);
    sensorCalibrationFileWr = readCalibrationFile(sdCardPath, "radioWR_", 0);
    sensorCalibrationFileVeg = readCalibrationFile(sdCardPath, "radioVEG_", 0);


    if (name == null) {
      this.name = sdCardPath.getName();
    } else {
      this.name = name;
    }
  }


  private CalibrationFile readCalibrationFile(File sdCardPath, String filter, int skipLines) {
    File folder = sdCardPath;
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


  /**
   * Getter for the devices Serial-Number.
   *
   * @return SerialNumber as String.
   */
  public String getDeviceSerialNr() {
    if (this.measureSequences == null || this.measureSequences.isEmpty()) {
      throw new IllegalArgumentException();
    }
    return this.measureSequences.stream().findAny().get().getSerial();
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

    return measureSequences;
  }

  /**
   * Checks if SDCARD is empty, empty DataFiles are removed before.
   *
   * @return true when empty.
   */
  public boolean isEmpty() {
    return this.measureSequences.isEmpty();


  }

  public File getPath() {
    return this.sdCardPath;
  }
}
