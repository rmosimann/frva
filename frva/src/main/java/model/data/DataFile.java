package model.data;

import java.util.List;

public class DataFile {

  private final String filename;
  private List<MeasureSequence> measureSequences;
  private Wavelength wavelength;
  private SensorCalibration sensorCalibration;


  /**
   * Constructor.
   * @param filename Name of the file
   */
  public DataFile(String filename) {
    this.filename = filename;
  }

}
