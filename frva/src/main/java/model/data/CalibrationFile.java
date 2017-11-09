package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalibrationFile {
  private final File originalFile;
  private final double[] calibration;

  /**
   * Constructor.
   *
   * @param input Array of strings containing the calibration.
   * @param skipLines Amount of lines to skip in the beginning.
   */
  public CalibrationFile(File input, int skipLines) {
    this.originalFile = input;

    List<String> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(input));) {
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          fileContent.add(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    calibration = fileContent.stream()
        .skip(skipLines)
        .mapToDouble(Double::parseDouble)
        .toArray();
  }

  public double[] getCalibration() {
    return calibration;
  }

  public File getCalibrationFile() {
    return this.originalFile;
  }

  public boolean equals(Object o){

    //TODO: proper implementation: Check if Calib File is truely the same!
   return o instanceof CalibrationFile && ((CalibrationFile) o).calibration[0]==(calibration[0]);
  }

}
