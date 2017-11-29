package model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CalibrationFile {
  private final File originalFile;
  private Vector<Double> wlF1;
  private Vector<Double> wlF2;
  private Vector<Double> upCoefF1;
  private Vector<Double> upCoefF2;
  private Vector<Double> dwCoefF1;
  private Vector<Double> dwCoefF2;
  private List<String> metadata;

  /**
   * Constructor.
   *
   * @param input     Array of strings containing the calibration.
   * @param skipLines Amount of lines to skip in the beginning.
   */
  public CalibrationFile(File input, int skipLines) {
    this.originalFile = input;
    this.wlF1 = new Vector<>();
    this.wlF2 = new Vector<>();
    this.upCoefF1 = new Vector<>();
    this.upCoefF2 = new Vector<>();
    this.dwCoefF1 = new Vector<>();
    this.dwCoefF2 = new Vector<>();
    this.metadata = new ArrayList<>();

    List<String[]> fileContent = new ArrayList<>();
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(input));) {
      br.readLine();
      while ((line = br.readLine()) != null) {
        if (!"".equals(line)) {
          fileContent.add(line.split(";"));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (String[] splitLine : fileContent) {
      wlF1.add(Double.parseDouble(splitLine[0]));
      upCoefF1.add(Double.parseDouble(splitLine[1]));
      dwCoefF1.add(Double.parseDouble(splitLine[2]));
      wlF2.add(Double.parseDouble(splitLine[3]));
      upCoefF2.add(Double.parseDouble(splitLine[4]));
      dwCoefF2.add(Double.parseDouble(splitLine[5]));
      if (splitLine.length > 6) {
        metadata.add(splitLine[6]);
      }
    }
  }

  /**
   * Creates a Calibration from the LiveView.
   *
   * @param wlF1     Vector.
   * @param upCoefF1 Vector.
   * @param dwCoefF1 Vector.
   * @param wlF2     Vector.
   * @param upCoefF2 Vector.
   * @param dwCoefF2 Vector.
   * @param metadata List containig the Metadata from the calib-File.
   */
  public CalibrationFile(Vector<Double> wlF1, Vector<Double> upCoefF1, Vector<Double> dwCoefF1,
                         Vector<Double> wlF2, Vector<Double> upCoefF2, Vector<Double> dwCoefF2,
                         List<String> metadata) {
    this.wlF1 = wlF1;
    this.wlF2 = wlF2;
    this.upCoefF1 = upCoefF1;
    this.upCoefF2 = upCoefF2;
    this.dwCoefF1 = dwCoefF1;
    this.dwCoefF2 = dwCoefF2;
    this.metadata = metadata;
    originalFile = null;
  }


  public File getCalibrationFile() {
    return this.originalFile;
  }

  public double[] getWlF1() {
    return getAsArray(wlF1);
  }

  public double[] getWlF2() {
    return getAsArray(wlF2);
  }

  public double[] getUpCoefF1() {
    return getAsArray(upCoefF1);
  }

  public double[] getUpCoefF2() {
    return getAsArray(upCoefF2);
  }

  public double[] getDwCoefF1() {
    return getAsArray(dwCoefF1);
  }

  public double[] getDwCoefF2() {
    return getAsArray(dwCoefF2);
  }

  public List<String> getMetadata() {
    return metadata;
  }

  private double[] getAsArray(List<Double> original) {
    return original.stream().mapToDouble(Double::doubleValue).toArray();
  }

  /**
   * Equals for the calibraion files, compares deeply.
   *
   * @param o Object to compare.
   * @return true when equal.
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof CalibrationFile && ((CalibrationFile) o).wlF1.equals(this.wlF1);
  }

}
