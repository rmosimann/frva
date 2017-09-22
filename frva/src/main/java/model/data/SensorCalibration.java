package model.data;

import java.util.Arrays;

public class SensorCalibration {
  private final double[] calibration;

  /**
   * Constructor.
   * @param input Array containing sensor Calibration data
   */
  public SensorCalibration(String[] input) {
    calibration = Arrays.stream(input)
        .mapToDouble(Double::parseDouble)
        .toArray();
  }

  public double[] getCalibration() {
    return calibration;
  }
}
